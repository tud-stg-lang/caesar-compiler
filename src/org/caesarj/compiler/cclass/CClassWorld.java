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
    
    public CaesarType getType(String qualifiedName) {
        return (CaesarType)typeMap.get(qualifiedName);
    }
    
    public CaesarType createCaesarType(String qualifiedName) {
        CaesarType type = new CaesarType(qualifiedName);
        typeMap.put(qualifiedName, type);
        return type;
    }
}
