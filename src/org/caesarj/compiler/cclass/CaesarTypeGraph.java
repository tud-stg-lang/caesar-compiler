package org.caesarj.compiler.cclass;

import java.util.*;

import org.caesarj.util.InconsistencyException;

/**
 * This is bidirectional inheritance graph 
 * for JTypeDeclaration 
 * 
 * @author Ivica Aracic 
 */
public class CaesarTypeGraph {

    /** root of inner and inheritance hierarchy */
    private Set topClassRoot    = new HashSet();
    private Set inheritanceRoot = new HashSet();
    private HashMap typeMap = new HashMap();
    
    public CaesarTypeGraph() {
    }
    
    public Iterator iterator() {
        return typeMap.values().iterator();
    }
    
    public boolean hasType(String qualifiedName) {
        return typeMap.containsKey(qualifiedName);
    }
    
    public CaesarTypeNode getTypeCreateIfNotExsistent(JavaQualifiedName qualifiedName, boolean implicit) {
        CaesarTypeNode res = getType(qualifiedName);
        
        if(res == null) {
            res = new CaesarTypeNode(this, qualifiedName, implicit);
            typeMap.put(qualifiedName, res);
        }
        
        return res;
    }
    
    public CaesarTypeNode getType(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = (CaesarTypeNode)typeMap.get(qualifiedName);
        return res;
    }
    
    public void checkFurtherbindings(CaesarTypeGraph completeGraph) {
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode t = (CaesarTypeNode)((Map.Entry)it.next()).getValue();
            
            t.setFurtherbinding(false);
            
            CaesarTypeNode tInCompleteGraph =
                completeGraph.getType(t.getQualifiedName());
            
            if(tInCompleteGraph == null)
                throw new InconsistencyException("explicit graph should be subgraph of complete graph");
            
            CaesarTypeNode tInCompleteGraphOuter =
                tInCompleteGraph.getOuter();
            
            if(tInCompleteGraphOuter != null) {
                for (Iterator it2 = tInCompleteGraphOuter.getParents().iterator(); it2.hasNext();) {
                    CaesarTypeNode outerSuper = (CaesarTypeNode) it2.next();
                    if(outerSuper.lookupInner(t.getQualifiedName().getIdent()) != null) {                     
                        t.setFurtherbinding(true);
                        break;
                    }
                }
            }
        }
    }
    
    public void generateMixinLists(CaesarTypeGraph explicitTypeGraph) {
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode type = (CaesarTypeNode)((Map.Entry)it.next()).getValue();
            
            type.createMixinList(explicitTypeGraph);
        }
    }
    
    /**
     * adds all implicit relations and types
     */
    public void addImplicitTypesAndRelations() {
        Set visited = new HashSet();
        Set added = new HashSet();
        
        for (Iterator it = typeMap.keySet().iterator(); it.hasNext();) {
            ((CaesarTypeNode)typeMap.get(it.next())).addImplicitTypesAndRelations(visited, added);
        }
        
        visited.clear();
        
        for (Iterator it = added.iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            typeMap.put(item.getQualifiedName(), item);
        }
    }
    
    public void debug() {
        for (Iterator it = typeMap.keySet().iterator(); it.hasNext();) {
            System.out.println(typeMap.get(it.next()));
        }
    }

    public Set getInheritanceRoot() {
        return inheritanceRoot;
    }

    public Set getTopClassRoot() {
        return topClassRoot;
    }
    
    public Map getTypeMap() {
        return typeMap;
    }
}
