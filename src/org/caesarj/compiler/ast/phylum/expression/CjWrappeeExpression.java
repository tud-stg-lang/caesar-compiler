package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * 
 * wrappee expression
 * 
 * @author Ivica Aracic
 */
public class CjWrappeeExpression extends JExpression {

    private JExpression fieldAccess = null;
    
    public CjWrappeeExpression(TokenReference where) {
        super(where);
    }
    
    public CType getType(TypeFactory factory) {
        if(fieldAccess == null) throw new InconsistencyException();
        return fieldAccess.getType(factory);
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        if(fieldAccess == null) throw new InconsistencyException();
        fieldAccess.genCode(context, discardValue);
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        fieldAccess = new JNameExpression(getTokenReference(), CaesarConstants.WRAPPER_WRAPPEE_FIELD);
        fieldAccess = fieldAccess.analyse(context);
        return this;
    }
    
    public boolean isFinal() {
        return true;
    }
}
