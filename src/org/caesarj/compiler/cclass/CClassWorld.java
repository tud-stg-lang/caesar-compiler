package org.caesarj.compiler.cclass;

import java.util.HashMap;

/**
 * This singleton instance hold all informations about all cclass types
 * 
 * @author Ivica Aracic
 */
public class CClassWorld {
    /*
     * SINGLETON
     */
    private CClassWorld() {
    }
    
    private static CClassWorld singleton = new CClassWorld();
    
    public static CClassWorld instance() {
    	return singleton;
    }
    
    /*
     * DATA
     */
    HashMap typeMap = new HashMap();
    
    public CaesarSourceType getType(String qualifiedName) {
        return (CaesarSourceType)typeMap.get(qualifiedName);
    }
    
    public CaesarSourceType createCaesarType(String qualifiedName) {
        CaesarSourceType type = new CaesarSourceType(qualifiedName);
        typeMap.put(qualifiedName, type);
        return type;
    }
}
