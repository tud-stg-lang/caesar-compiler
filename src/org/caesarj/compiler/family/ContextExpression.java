package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.InconsistencyException;

/**
 * ctx(k)
 * 
 * @author Ivica Aracic
 */
public class ContextExpression extends Path {

    private int k = 0;
    
    public ContextExpression(Path prefix, int k, CReferenceType type) {
        super(prefix, type);
        this.k = k;
        
        if(k < 0)
            throw new InconsistencyException();
    }
        
    public int getK() {
        return k;
    }
    
    public String toString() {
        return (prefix==null?"":prefix.toString()+".")+"ctx("+k+")";
    }
    
    public Path normalize() {
        return this.clonePath();
    }
    
    protected Path _normalize(Path pred, Path tail) {
        
        System.out.println("\t----->"+tail);
        
        if(prefix == null) {
            return tail;
        }
        else {
            if(k == 0) {
                pred.prefix = prefix;
                return prefix._normalize(pred, tail);
            }
            else if(prefix instanceof ContextExpression) {
                this.k += ((ContextExpression)prefix).getK();
                this.prefix = prefix.prefix;
                if(prefix == null)
                    return tail;
                else 
                    return this._normalize(pred, tail);
            }
            else {
                Path typePath = prefix.getType().getPath().clonePath();
                Path typePathHead = typePath.getHead();
                //Path typePathHeadPred = typePath.getHeadPred();
                
                k--;                                
                typePathHead.prefix = prefix.prefix;
                prefix = typePath;
                
                return this._normalize(pred, tail);
            }
        }
    }
    
    protected Path clonePath() {
        return new ContextExpression(prefix==null ? null : prefix.clonePath(), k, type);
    }
}
