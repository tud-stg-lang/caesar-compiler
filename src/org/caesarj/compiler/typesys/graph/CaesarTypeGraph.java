package org.caesarj.compiler.typesys.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeGraph {
	/** root of inner and inheritance hierarchy */
    private Set topClassRoot    = new HashSet();
    private Set inheritanceRoot = new HashSet();
    private HashMap typeMap     = new HashMap();
    
    public CaesarTypeGraph() {
    }
    
    public boolean hasType(String qualifiedName) {
        return typeMap.containsKey(qualifiedName);
    }
    
    public CaesarTypeNode getTypeCreateIfNotExsistent(JavaQualifiedName qualifiedName, CaesarTypeNode.Kind kind) {
        CaesarTypeNode res = getType(qualifiedName);
        
        if(res == null) {
            res = new CaesarTypeNode(this, kind, qualifiedName);
            typeMap.put(qualifiedName, res);
        }
        
        return res;
    }
    
    public CaesarTypeNode getType(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = (CaesarTypeNode)typeMap.get(qualifiedName);
        return res;
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
