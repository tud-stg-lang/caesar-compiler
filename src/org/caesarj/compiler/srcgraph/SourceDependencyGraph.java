package org.caesarj.compiler.srcgraph;

import java.util.HashMap;
import java.util.Iterator;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CCompositeType;

/**
 * This is bidirectional inheritance dependency graph 
 * for JTypeDeclaration 
 * 
 * @author Ivica Aracic 
 */
public class SourceDependencyGraph {

    // the root node representes any non-source type
    private RootTypeNode rootNode = new RootTypeNode();
    private HashMap typeMap = new HashMap();

    public SourceDependencyGraph() {
    }
    
    public Iterator iterator() {
        return typeMap.values().iterator();
    }
    
    public TypeNode getRoot() {
        return rootNode;
    }
    
    public TypeNode getNode(String qualifiedName) {
        return (TypeNode)typeMap.get(qualifiedName);
    }
    
    public RegularTypeNode createNode(CClass clazz) {
        RegularTypeNode node = new RegularTypeNode(clazz);
        typeMap.put(node.getName(), node);
        return node;
    }

    public CompositeTypeNode createNode(CCompositeType compositeType) {
        CompositeTypeNode node = new CompositeTypeNode(compositeType);
        typeMap.put(node.getName(), node);
        return node;
    }

    public void calculateLevels() {
        rootNode.calculateLevel(0);
    }
    
    public void debug() {
        rootNode.debug();
    }
}
