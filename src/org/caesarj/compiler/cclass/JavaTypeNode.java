package org.caesarj.compiler.cclass;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    private List inners = new LinkedList();
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

        System.out.print("("+id+") ");
        
        for(int i=0; i<level; i++)
            System.out.print("  ");
         
        System.out.println(this);
        System.out.println();
        
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

        res.append(" [T:");
        res.append(type!=null ? type.getQualifiedName().toString() : "---");
        res.append("]"); 

        res.append(" [M:");
        res.append(!(type!=null && type.isImplicit()) ? getQualifiedMixinName().toString() : "---");
        res.append("]");
                       
        res.append(" [QN:");
        res.append(getQualifiedName());
        res.append("]");

        res.append(" [F:");
        res.append(isToBeGeneratedInBytecode() ? 'G' : '-');
        res.append(isToBeGeneratedInAst() ? 'I' : '-');
        res.append("]");
        
        res.append(" [Orig:");
        if(original != null)
            res.append(original.getId());
        else
            res.append('-');
        res.append("] [Outer:");
        if(outer != null)
            res.append(outer.getId());
        else
            res.append('-');
        res.append("]");
                
        
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

    public void genMixinCopyDependencies() {       
        if(this.isToBeGeneratedInBytecode()) {
            JavaTypeNode compilationNode = 
                compilationGraph.getOriginalMixin(getQualifiedMixinName());
            
            if(compilationNode != null) {
                compilationNode.addMixinCopy(this);
            }
        }

        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode compilationNode = (JavaTypeNode) it.next();
            compilationNode.genMixinCopyDependencies();
        }
    }
    
    private void addMixinCopy(JavaTypeNode node) {
        mixinCopies.add(node);
        node.original = this;
    }

    public CaesarTypeNode getMixin() {
        return mixin;
    }
    
    public void genOuterAndQualifiedNames() {
        if(parent == null) {
            this.qualifiedName = new JavaQualifiedName("java/lang/Object");
            setOuter(null);
        }
        else if(type != null) {
            this.qualifiedName = type.getQualifiedImplName();
            
            if(type.getOuter() != null)
                setOuter(compilationGraph.getJavaTypeNode(type.getOuter()));
        }
        else {
            this.qualifiedName = null;
            setOuter(null);
        }
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.genOuterAndQualifiedNames();
        }
    }

    public void genOuterAndQNForGeneratedTypes(Set visited) {
        
        if(visited.contains(this))
            return;
        
        if(isToBeGeneratedInBytecode()) {
            /*
             * calc outer
             */ 
            if(mixin.getOuter() != null) {   
                setOuter(compilationGraph.getJavaTypeNode(mixin.getOuter()));
                setOuter(getMostSpecificOuter(getOuter()));
                
                Collection leafs = new LinkedList();
                this.collectLeafs(leafs);
                
                JavaTypeNode n = (JavaTypeNode)leafs.iterator().next();
                JavaTypeNode nOuter = n.getOuter();
                
                while(nOuter != null) {
                    if(getOuter().getMixin().equals(nOuter.getMixin())) {
                        setOuter(nOuter);
                        break;
                    }
                    nOuter = nOuter.getParent();
                }
            }
            
            
            /*
             * calc the qualified name
             */ 
            StringBuffer qualifiedName = new StringBuffer();
            if(getOuter() != null) {
                getOuter().genOuterAndQNForGeneratedTypes(visited);
                qualifiedName.append(getOuter().getQualifiedName().toString());
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
    
        // register in graph's node map
        compilationGraph.registerNode(qualifiedName, this);
        
        visited.add(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.genOuterAndQNForGeneratedTypes(visited);
        }
    }
    
    private JavaTypeNode getMostSpecificOuter(JavaTypeNode mostSpecificTillNow) {
        JavaTypeNode newMostSpecific = mostSpecificTillNow;         
        
        // check if our most specific is still valid
        if(getOuter() != null) {
            if(getOuter().isSubClassOf(mostSpecificTillNow))
                newMostSpecific = getOuter();
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

    public boolean isLeaf() {
        return subNodes.size() == 0;
    }

    public boolean isRoot() {
        return parent == null;
    }
    
    public void collectLeafs(Collection res) {
        if(isLeaf()) {
            res.add(this);
        }
        else {
            for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
                JavaTypeNode item = (JavaTypeNode) it.next();
                item.collectLeafs(res);
            }
        }
    }
    
    public void collectGeneratedTypes(Collection res) {
        if(isToBeGeneratedInBytecode())
            res.add(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.collectGeneratedTypes(res);
        }
    }

    public void collectAllTypes(Collection res) {        
        res.add(this);
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.collectAllTypes(res);
        }
    }

    protected void setOuter(JavaTypeNode outer) {
        if(this.outer != null)
            this.outer.getInners().remove(this);        
        
        this.outer = outer;
        
        if(outer != null)
            outer.getInners().add(this);
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

    public List getInners() {
        return inners;
    }
}
