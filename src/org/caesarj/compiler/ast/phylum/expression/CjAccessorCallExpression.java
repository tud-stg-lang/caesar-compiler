package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.util.TokenReference;

/**
 * handles read only field access to public caesar fields
 * 
 * @author Ivica Aracic
 */
public class CjAccessorCallExpression extends JMethodCallExpression {
   
    private String fieldIdent;
    
    public CjAccessorCallExpression(
        TokenReference where,
        JExpression prefix,
        String ident
    ) {
        super(where, prefix, "get_"+ident, JExpression.EMPTY);
        fieldIdent = ident;
    }
    
    public String getFieldIdent() {
        return fieldIdent;
    }
}
