package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Caesar Type Graph.  
 * 
 * @author Ivica Aracic 
 */
public abstract class CaesarTypeGraph {

    /** root of inner and inheritance hierarchy */
    private Set topClassRoot    = new HashSet();
    private Set inheritanceRoot = new HashSet();
    private HashMap typeMap = new HashMap();
    
    public CaesarTypeGraph() {
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
