package org.caesarj.compiler.cclass;

import java.util.*;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JavaTypeGraph {
    
    private JavaTypeNode root;
    private HashMap caesar2javaMap = new HashMap();
    private HashMap nodes = new HashMap();
    
    public JavaTypeGraph() {
        CaesarTypeNode typeNode = new CaesarTypeNode(null, "org/caesarj/runtime/CaesarObject", false);
        root = new JavaTypeNode(this, typeNode);
        root.setType(typeNode);
    }
    
    // CTODO this is buggy!
    // we can not sort into inheritance hierarchy, we also have to consider outer!
    public void generateFrom(CaesarTypeGraph completeGraph) {
    	buildJavaTypeGraph(completeGraph);
                       
        root.genOuterAndQualifiedNames();
        root.genOuterAndQNForGeneratedTypes(new HashSet());
    }  
    
    private void buildJavaTypeGraph(CaesarTypeGraph completeGraph) {    
        Map typeMap = completeGraph.getTypeMap();
        
        // generate graph
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode t = (CaesarTypeNode) ((Map.Entry)it.next()).getValue();
            
            List mixinList = t.getMixinList();
            
            // sort list into compilation graph
            CaesarTypeNode[] mixins = new CaesarTypeNode[mixinList.size()];
            mixins = (CaesarTypeNode[])mixinList.toArray(mixins);
            
            JavaTypeNode current = root;
            
            for (int i=mixins.length-1; i>=0; i--) {
                CaesarTypeNode mixin = mixins[i];
                JavaTypeNode next = current.getSubNode(mixin.getQualifiedName().toString());
                
                if(next == null) {
                    next = new JavaTypeNode(this, mixin);
                    current.addSubNode(next);
                }
                                
                current = next;
            }
            
            if(t.isImplicit()) {
                // append as leaf
                JavaTypeNode next = new JavaTypeNode(this, t);
                next.setType(t);
                current.addSubNode(next);
            }
            else {
                current.setType(t);
            }
        }
    }

    private JavaTypeNode findCompatibleSubNode(
        JavaTypeNode[] outerList, 
        CaesarTypeNode[] mixinList,
        int i
    ) {        
        return null;
    }

    public void debug() {
        root.debug(0);
    }

    public void registerJavaType(CaesarTypeNode type, JavaTypeNode node) {
        caesar2javaMap.put(type, node);
    }    
    
    public JavaTypeNode getJavaTypeNode(CaesarTypeNode type) {
        return (JavaTypeNode)caesar2javaMap.get(type);
    }
    
    public JavaTypeNode getNode(JavaQualifiedName qn) {
        return (JavaTypeNode)nodes.get(qn);
    }
    
    protected void registerNode(JavaQualifiedName qn, JavaTypeNode node) {
        nodes.put(qn, node);
    }

    /** 
     * @return preorder sorted list of types which has to be generated 
     */
    public Collection getTypesToGenerate() {
        Collection res = new LinkedList();
        root.collectGeneratedTypes(res);
        return res;
    }

    /**
     * @return preorder sorted list of all types
     */
    public Collection getAllTypes() {
        Collection res = new LinkedList();
        root.collectAllTypes(res);
        return res;
    }
    
    /**
     * CTODO getAllSourceTypes
     */
    public Collection getAllSourceTypes() {
        return null;
    }
}
