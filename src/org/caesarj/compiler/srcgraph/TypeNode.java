package org.caesarj.compiler.srcgraph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * ... 
 * 
 * @author Ivica Aracic 
 */
public abstract class TypeNode {

    private TypeNode ownerNode = null;
    private List subTypes   = new LinkedList(); // of TypeNode
    private List superTypes = new LinkedList(); // of TypeNode
    protected int level = Integer.MIN_VALUE;
    
    TypeNode() {        
    }
    
    public void addSuperTypeNode(TypeNode node) {
        superTypes.add(node);
        node.getSubTypes().add(this);
    }
    
    public void addSubTypeNode(TypeNode node) {
        subTypes.add(node);
        node.getSuperTypes().add(this);
    }

    public TypeNode getOwnerType() {
        return ownerNode;
    }

    public List getSubTypes() {
        return subTypes;
    }

    public List getSuperTypes() {
        return superTypes;
    }

    public int getLevel() {
        return level;
    }
    
    public boolean isCompositeType() {
        return superTypes.size() > 1;
    }

    public void updateLevel() {
    }

    public abstract String getName();

    public abstract void calculateLevel(int i);
    
    protected void debug(String indent, Set visited) {
        if(!visited.contains(this)) {        
            visited.add(this);
            
            System.out.print(indent);
            System.out.print(getLevel());
            System.out.print(":");
            System.out.println(getName());
            
            for(Iterator it=getSubTypes().iterator(); it.hasNext(); ) {
                TypeNode subNode = (TypeNode)it.next();            
                subNode.debug(indent+'\t', visited);
            }
        }
    }
}
