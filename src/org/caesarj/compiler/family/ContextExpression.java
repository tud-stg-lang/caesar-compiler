package org.caesarj.compiler.family;

import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;

/**
 * ctx(k)
 * 
 * @author Ivica Aracic
 */
public class ContextExpression extends Path {

    private int k = 0;
    
    public ContextExpression() {
        this(0);
    }

    public ContextExpression(int k) {
        this.k = k;
        
        if(k < 0)
            throw new InconsistencyException();
    }
        
    public int getK() {
        return k;
    }
    
    public StaticObject type(CClass context) {
        if(k > context.getDepth()+1) 
            throw new InconsistencyException();
        
        if(context.getDepth()+1 == k) {
            return new StaticObject(null, null);
        }
        else {
            CClass type = context;
            
            for (int i = 0; i < k; i++)
                type = type.getOwner();

            return new StaticObject(new ContextExpression(k+1), type);
        }        
    }
    
    public boolean equals(Path other) {
        return (other instanceof ContextExpression) && ((ContextExpression)other).k==k;
    }
    
    public String toString() {
        return "ctx("+k+")";
    }
}
