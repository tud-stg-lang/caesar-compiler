/*
 * Created on 19.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.family.Path;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CjFamilyCastExpression extends JExpression {
	private JExpression expr;
	
    public CjFamilyCastExpression(TokenReference where, JExpression expr, 
    		Path family, Path thisAsFamily) {
        super(where);
        this.expr = expr;
        this.family = family;
        this.thisAsFamily = thisAsFamily;
    }
    
    /**
     * Compute the type of this expression.
     * 
     * @return the type of this expression
     */
    public CType getType(TypeFactory factory) {
        return expr.getType(factory);
    }
    
    /**
     * Analyses the expression (semantically).
     * 
     * @param context
     *            the analysis context
     * @return an equivalent, analysed expression
     * @exception PositionedError
     *                the analysis detected an error
     */
    public JExpression analyse(CExpressionContext context) throws PositionedError {
    	return new CjFamilyCastExpression(getTokenReference(), expr.analyse(context), getFamily(), getThisAsFamily());
    }
    
    /**
     * Generates JVM bytecode to evaluate this expression.
     * 
     * @param code the bytecode sequence
     * @param discardValue discard the result of the evaluation ?
     */
    public void genCode(GenerationContext context, boolean discardValue) {
        expr.genCode(context, discardValue);
    }

    public void recurse(IVisitor s) {
        expr.accept(s);
    }

    public JExpression getExpression() {
        return expr;
    }  
}
