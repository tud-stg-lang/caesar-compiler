package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JavaTypeNode {
    
    private static Object idLock = new Object();
    private static int currentId = 100;
    
    private int id;
    private int level = 0;
    
    private JavaQualifiedName qualifiedName;
    
    private boolean toGenerate = false;
    
    private CaesarTypeNode type = null;
    private CaesarTypeNode mixin = null;
    
    private JavaTypeNode original = null;
    private JavaTypeNode parent = null;
    private HashMap subNodes = new HashMap();
    private Set mixinCopies = new HashSet();
    
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
        res.append('(');
        res.append(id);        
        res.append(") [L");
        res.append(level);
        res.append("]");

        res.append('[');
        res.append(isToBeGenerated() ? 'G' : '-');
        res.append("]");
        
        res.append(" [M:");
        res.append(!(type!=null && type.isImplicit()) ? getQualifiedMixinName().toString() : "<empty>");
        res.append("]");
        
        res.append(" [T:");
        res.append(type!=null ? type.getQualifiedName().toString() : "<unreachable>");
        res.append("]"); 
               
        res.append(" [O:");
        if(original != null)
            res.append(original.getId());
        else
            res.append('-');
        res.append(']');
        
        
        return res.toString();
    }

    public CaesarTypeNode getType() {
        return type;
    }

    public void setType(CaesarTypeNode type) {
        this.type = type;
    }
    
    public boolean isToBeGenerated() {
        return parent!=null && (type==null || type.isImplicit());               
    }

    public void calculateMixinCopyDependencies() {       
        if(this.isToBeGenerated()) {
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

    public void calculateCompilationLevel(int parentLevel, boolean parentInGeneratedLayer) {
        int newLevel = parentLevel;
        boolean thisInGeneratedLayer = isToBeGenerated(); 
        
        if(!parentInGeneratedLayer && thisInGeneratedLayer) {
            newLevel++;
        }
        
        if(newLevel > this.level)
            this.level = newLevel;
        
        if(type != null) {
            if(type.getImplDeclaration() != null) {
                type.getImplDeclaration().setEnabledInPass(this.level);
            }
        }
        
        for (Iterator it = mixinCopies.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.calculateCompilationLevel(this.level, thisInGeneratedLayer);
        }
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.calculateCompilationLevel(this.level, thisInGeneratedLayer);
        }
    }

    public void calculateQualifiedNames() {
        
        if(type != null) {
            this.qualifiedName = type.getQualifiedName();
        }
        else {
            // go the path back to root and generate the name
            
        }
        
        for (Iterator it = subNodes.values().iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
            item.calculateQualifiedNames();
        }
    }
}
