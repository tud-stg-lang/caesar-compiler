package org.caesarj.compiler.family;

import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class TypeDecl extends Path {

    Path prefix;
    String type;
    
        
    public Path getPrefix() {
        return prefix;
    }
    
    public String getTypeName() {
        return type;
    }
    
    public TypeDecl(Path prefix, String type) {
        this.prefix = prefix;
        this.type = type;
    }
    
    public StaticObject type(CClass context) {
        try {
            StaticObject so = prefix.type(context);    
            CClass clazz = so.getType().lookupClass(context, type);
            return new StaticObject(prefix, clazz);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new InconsistencyException();
        }
    }
    
    public boolean equals(Path other) {
        return 
        	(other instanceof TypeDecl)
        	&& prefix.equals(((TypeDecl)other).prefix)
        	&& type.equals(((TypeDecl)other).type);
    }     
    
    public String toString() {
        return prefix+"."+type;
    }   
}
