package org.caesarj.compiler.cclass;

import java.util.LinkedList;
import java.util.List;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class Node {
    
    private String mixin;
    private List children = new LinkedList();
    private Node parent = null;
    private int level = 0;
    
    private boolean toBeGenerated = false;
    
    public Node() {
    }
    

    public void calculateLevel(int i) {
        // ...
    }
    
    /*
     * GETTER & SETTER 
     */
    
    public int getLevel() {
        return level;
    }
    
    public List getChildren() {
        return children;
    }


    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isToBeGenerated() {
        return toBeGenerated;
    }

    public void setToBeGenerated(boolean toBeGenerated) {
        this.toBeGenerated = toBeGenerated;
    }
}
