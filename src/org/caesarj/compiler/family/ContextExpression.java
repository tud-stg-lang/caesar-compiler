package org.caesarj.compiler.family;

import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
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
    
//    public StaticObject type(CClass context) {
//    public StaticObject type(StaticPath sp){
    public StaticObject type(CContext context) {
        int n = context.getDepth(); //sp.getLength();
        if (k > n)
            throw new InconsistencyException();
        if (k == n){
            return new StaticObject(null, null);
        }
        else {
           
            CContext ctx = context;
//            for (int p=0; p<k; ){
            while (ctx.getDepth() > n-k){
                ctx = ctx.getParentContext();
            }
            
            if (ctx instanceof CClassContext){
                CClass type = ((CClassContext)ctx).getCClass(); // ((StaticPath.ClassElement)element).getCClass();
                
                return new StaticObject(new ContextExpression(k+1), type);
            } else {
                throw new InconsistencyException("Invalid context expression in path");
            }
            
        }        
    }
    
    public boolean equals(Path other) {
        return (other instanceof ContextExpression) && ((ContextExpression)other).k==k;
    }
    
    public String toString() {
        return "ctx("+k+")";
    }
}
