package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.Iterator;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CompilationNode {    
    private int level = 0;
    
    private boolean toGenerate = false;
    
    private CaesarTypeNode type = null;
    private CaesarTypeNode mixin = null;
    
    private CompilationNode parent = null;
    private HashMap subNodes = new HashMap();
    
    CompilationGraph compilationGraph;
    
    public CompilationNode(CompilationGraph compilationGraph, CaesarTypeNode mixin) {
        this.compilationGraph = compilationGraph;
        this.mixin = mixin;
    }
    
    public CompilationNode getSubNode(String subMixinName) {
        return (CompilationNode)subNodes.get(subMixinName);
    }
    
    public void addSubNode(CompilationNode subNode) {
        subNodes.put(subNode.getQualifiedMixinName().toString(), subNode);
        subNode.parent = this;
    }
    
    public void removeSubNode(CompilationNode subNode) {
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
            CompilationNode item = (CompilationNode) it.next();
            
            item.debug(level+1);
        }
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append('[');
        res.append(isToBeGenerated() ? 'G' : '-');
        res.append("]");
        
        res.append(" [M:");
        res.append(!(type!=null && type.isImplicit()) ? getQualifiedMixinName().toString() : "<empty>");
        res.append("]");
        
        res.append(" [T:");
        res.append(type!=null ? type.getQualifiedName().toString() : "<unreachable>");
        res.append("]");        
        
        
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
}
