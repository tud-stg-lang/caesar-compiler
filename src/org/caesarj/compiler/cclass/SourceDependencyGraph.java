package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.Iterator;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CCompositeType;

/**
 * This is bidirectional inheritance graph 
 * for JTypeDeclaration 
 * 
 * @author Ivica Aracic 
 */
public class SourceDependencyGraph {

    // the root node representes any non-source type
    private RootNode rootNode = new RootNode();
    private HashMap typeMap = new HashMap();

    public SourceDependencyGraph() {
    }
    
    public Iterator iterator() {
        return typeMap.values().iterator();
    }
    
    public Node getRoot() {
        return rootNode;
    }
    
    public Node getNode(String qualifiedName) {
        return (Node)typeMap.get(qualifiedName);
    }
    
    public Node createNode(CClass clazz) {        
        return null;
    }

    public void calculateLevels() {
        //rootNode.calculateLevel(0);
    }
    
}
