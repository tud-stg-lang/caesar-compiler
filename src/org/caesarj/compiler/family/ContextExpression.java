package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.InconsistencyException;

/**
 * ctx(k)
 * 
 * @author Ivica Aracic
 */
public class ContextExpression extends Path {

    private Path prefix;
    private int k = 0;
    
    public ContextExpression(int k, CReferenceType type) {
        super(type);
        this.k = k;
        
        if(k < 0)
            throw new InconsistencyException();
    }
        
    public int getK() {
        return k;
    }
    
    public boolean equals(Path other) {
        return (other instanceof ContextExpression) && ((ContextExpression)other).k==k;
    }
    
    public String toString() {
        return "ctx("+k+")";
    }
}
