package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FieldAccess extends Path {

    private String name;
    
    public FieldAccess(Path prefix, String field, CReferenceType type) {
        super(prefix, type);
        this.name = field;
    }

    public String getName() {
        return name;
    }
    
    private Path getReceiver() {
        return null;
    }
    
    public String toString() {
        return prefix+"."+name;
    }

    public Path normalize() {
        Path typePath = type.getPath().clonePath();
        Path typePathHeadPred = typePath.getHeadPred();
        Path typePathHead = typePath.getHead();
        typePathHead.prefix = prefix.clonePath();
        
        return typePathHead._normalize(typePathHeadPred, typePath);
    }

    protected Path _normalize(Path pred, Path tail) {
        return prefix._normalize(this, tail);
    }
    
    protected Path clonePath() {
        return new FieldAccess(prefix==null ? null : prefix.clonePath(), name, type);
    }
}
