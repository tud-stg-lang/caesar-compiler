package org.caesarj.compiler.ast.phylum.expression;


import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.family.FjFamily;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

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
