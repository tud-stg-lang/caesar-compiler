package org.caesarj.compiler.ast;


import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JParenthesedExpression;

public class FjParenthesedExpression extends JParenthesedExpression {

	protected JExpression cachedExpression;

	public FjParenthesedExpression(TokenReference where, JExpression expr) {
		super(where, expr);
		cachedExpression = expr;
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		return cachedExpression.getFamily( context );
	}
}
