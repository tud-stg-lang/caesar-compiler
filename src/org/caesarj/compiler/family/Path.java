package org.caesarj.compiler.family;

import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JOwnerExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public abstract class Path {

    public abstract StaticObject type(CClass context);
    
    public abstract boolean equals(Path other);
    
    
    /**
     * CONVERTER
     */
    public static Path createFrom(CClass contextClass, JFieldAccessExpression expr) {
        List l = new LinkedList();        
        JExpression tmp = expr;
        while(tmp instanceof JFieldAccessExpression) {
            l.add(0, tmp);
            tmp = ((JFieldAccessExpression)tmp).getPrefix();
        }
        
        if(!( (tmp instanceof JOwnerExpression) || (tmp instanceof JThisExpression) ) ) {
            System.out.println("a path may only have a field access and it may only start with a this or an owner expression; line = "+expr.getTokenReference().getLine());
            throw new InconsistencyException();
        }
        
        JFieldAccessExpression fields[] = 
            (JFieldAccessExpression[])l.toArray(new JFieldAccessExpression[l.size()]); 
        
        // determine k
        int k = 0;
        CClass owner = fields[0].getField().getOwner();
        CClass ctxClass = contextClass;
        
        while(owner != ctxClass) {
            ctxClass = ctxClass.getOwner();
            k++;
            if(ctxClass == null) {
                System.out.println("can not calc k");
                throw new InconsistencyException();
            }
        }
        
        Path pathExpr = new ContextExpression(k);
        
        for (int i = 0; i < fields.length; i++) {
            pathExpr = new FieldAccess(pathExpr, fields[i].getIdent());
        }
        
        return pathExpr;
    }
}
