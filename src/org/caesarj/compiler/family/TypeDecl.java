package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CType;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class TypeDecl {
    
    Path prefix;
    CType type;    
    
    public TypeDecl(Path prefix, CType type) {
        this.prefix = prefix;
        this.type = type;
    }
    
    public Path getPrefix() {
        return prefix;
    }
    
    public CType getTypeName() {
        return type;
    }
    
    public boolean equals(Path other) {
        return false;
    }     
    
    public String toString() {
        return prefix+"."+type;
    }   
}
