package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FieldAccess extends Path {

    private Path prefix;
    private String name;
    
    public FieldAccess(Path prefix, String field, CReferenceType type) {
        super(type);
        this.prefix = prefix;
        this.name = field;
    }

    public String getName() {
        return name;
    }
    
    private Path getReceiver() {
        return null;
    }
    
    private Path getPrefix() {
        return prefix;
    }   

    public boolean equals(Path other) {
        return false;
    }
    
    public String toString() {
        return prefix+"."+name;
    }
}
