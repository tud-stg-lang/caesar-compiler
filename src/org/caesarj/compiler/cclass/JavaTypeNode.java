package org.caesarj.compiler.cclass;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.export.CClass;
import org.caesarj.mixer.NameGenerator;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JavaTypeNode {
    
    private static Object idLock = new Object();
    private static int currentId = 100;
    
    private int id;
    
    private JavaQualifiedName qualifiedName;
    
    private boolean toGenerate = false;
    
    private CaesarTypeNode type = null;
    private CaesarTypeNode mixin = null;
    
    private JavaTypeNode outer = null;
    private JavaTypeNode original = null;
    private JavaTypeNode parent = null;
    private HashMap subNodes = new HashMap();
    private Set mixinCopies = new HashSet();
    
    private CjClassDeclaration declaration = null;
    private CClass clazz = null;
    
    JavaTypeGraph compilationGraph;
    
    public JavaTypeNode(JavaTypeGraph compilationGraph, CaesarTypeNode mixin) {
        this.compilationGraph = compilationGraph;
        this.mixin = mixin;
        
        synchronized(idLock) {
            this.id = currentId++;
        }
    }
    
    public JavaTypeNode getSubNode(String subMixinName) {
        return (JavaTypeNode)subNodes.get(subMixinName);
    }
    
    public void addSubNode(JavaTypeNode subNode) {
        subNodes.put(subNode.getQualifiedMixinName().toString(), subNode);
        subNode.parent = this;
    }
    
    public void removeSubNode(JavaTypeNode subNode) {
        if(subNodes.containsKey(subNode.getQualifiedMixinName().toString())) {
            subNodes.remove(subNode.getQualifiedMixinName().toString());
            subNode.parent = null;
        }
    }

    public JavaQualifiedName getQualifiedMixinName() {
        return mixin.getQualifiedName();
    }

    public void debug(int level) {
        for(int i=0; i<level; i++)
            System.out.print('\t');
        
        System.out.println(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            
            item.debug(level+1);
        }
    }
        
    public int getId() {
        return id;
    }
        
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append('{');
        res.append(id);        
        res.append("}");

        res.append(" [");
        res.append(isToBeGeneratedInBytecode() ? 'G' : '-');
        res.append(isToBeGeneratedInAst() ? 'I' : '-');
        res.append("]");
        
        res.append(" [M:");
        res.append(!(type!=null && type.isImplicit()) ? getQualifiedMixinName().toString() : "---");
        res.append("]");
        
        res.append(" [T:");
        res.append(type!=null ? type.getQualifiedName().toString() : "---");
        res.append("]"); 
               
        res.append(" [Orig:");
        if(original != null)
            res.append(original.getId());
        else
            res.append('-');
        res.append("; Outer:");
        if(outer != null)
            res.append(outer.getId());
        else
            res.append('-');
        res.append("]");
        
        res.append(" QN: ");
        res.append(getQualifiedName());
        
        
        return res.toString();
    }

    public CaesarTypeNode getType() {
        return type;
    }

    public void setType(CaesarTypeNode type) {
        this.compilationGraph.registerJavaType(type, this);
        this.type = type;
    }
    
    public boolean isToBeGeneratedInBytecode() {
        return type==null;               
    }
    
    public boolean isToBeGeneratedInAst() {
        return type != null && type.isImplicit();
    }

    public void calculateMixinCopyDependencies() {       
        if(this.isToBeGeneratedInBytecode()) {
            JavaTypeNode compilationNode = 
                compilationGraph.getOriginalMixin(getQualifiedMixinName());
            
            if(compilationNode != null) {
                compilationNode.addMixinCopy(this);
            }
        }

        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode compilationNode = (JavaTypeNode) it.next();
            compilationNode.calculateMixinCopyDependencies();
        }
    }
    
    private void addMixinCopy(JavaTypeNode node) {
        mixinCopies.add(node);
        node.original = this;
    }

    public CaesarTypeNode getMixin() {
        return mixin;
    }

    public void calculateOuterAndQualifiedName() {
        
        if(parent == null) {
            this.qualifiedName = new JavaQualifiedName("java/lang/Object");
        }
        else if(type != null) {
            this.qualifiedName = type.getQualifiedImplName();
            
            if(type.getOuter() != null)
                this.outer = compilationGraph.getJavaTypeNode(type.getOuter()); 
        }
        else {
            // calc outer
            if(mixin.getOuter() != null) {
                this.outer = compilationGraph.getJavaTypeNode(mixin.getOuter());                
                this.outer = getMostSpecificOuter(this.outer);
            }
            
            // and then calc the qualified name
            StringBuffer qualifiedName = new StringBuffer();
            if(this.outer != null) {
                qualifiedName.append(this.outer.getType().getQualifiedImplName().toString());
                qualifiedName.append('$');
            }
            else {
                qualifiedName.append(mixin.getQualifiedName().getPackagePrefix());
            }
            

            String genHashCode = 
                NameGenerator.generateHashCode(
                    parent.getQualifiedName().toString()+
                    mixin.getQualifiedName().toString()
                );
            
            qualifiedName.append("gen_");
            qualifiedName.append(genHashCode);
            
            this.qualifiedName = new JavaQualifiedName(qualifiedName.toString());
        }
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.calculateOuterAndQualifiedName();
        }
    }
    
    private JavaTypeNode getMostSpecificOuter(JavaTypeNode mostSpecificTillNow) {
        JavaTypeNode newMostSpecific = mostSpecificTillNow;         
        
        // check if our most specific is still valid
        if(this.outer != null) {
            if(this.outer.isSubClassOf(mostSpecificTillNow))
                newMostSpecific = this.outer;
        }
        
        if(parent != null)
            return parent.getMostSpecificOuter(newMostSpecific);
        else
            return newMostSpecific;
    }

    public boolean isSubClassOf(JavaTypeNode clazz) {
        if(this == clazz)
            return true;
        else if(parent == null)
            return false;
        else 
            return parent.isSubClassOf(clazz);
    }

    public JavaQualifiedName getQualifiedName() {
        return qualifiedName;
    }

    public JavaTypeNode getParent() {
        return parent;
    }

    public void getTypesToGenerate(Collection res) {
        if(isToBeGeneratedInBytecode())
            res.add(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.getTypesToGenerate(res);
        }
    }

    public void getAllTypes(Collection res) {        
        res.add(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.getAllTypes(res);
        }
    }

    public JavaTypeNode getOuter() {
        return outer;
    }

    public CClass getCClass() {
        return clazz;
    }

    public void setCClass(CClass clazz) {
        this.clazz = clazz;
    }

    public void setDeclaration(CjClassDeclaration declaration) {
        this.declaration = declaration;
    }
    
    public CjClassDeclaration getDeclaration() {
        return declaration;
    }
}
