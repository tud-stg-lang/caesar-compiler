package org.caesarj.compiler.family;

import org.caesarj.compiler.export.CClass;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class StaticObject {
    
    private Path path;
    private CClass type;
    
    public StaticObject(Path path, CClass type) {
        this.path = path;
        this.type = type;
    }    
    
    public Path getPath() {
        return path;
    }
    
    public CClass getType() {
        return type;
    }
    
    public String toString() {
        return path+"."+type.getCClass().getIdent();
    }
    
    public boolean equals(StaticObject other) {
        return 
        	type == other.type
        	&& path.equals(other.path);
    }
}
