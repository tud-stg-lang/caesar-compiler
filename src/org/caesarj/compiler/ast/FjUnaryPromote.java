package org.caesarj.compiler.ast;

import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;

public class FjUnaryPromote extends JUnaryPromote {

	protected JExpression cachedExpr;

	public FjUnaryPromote(CTypeContext context, JExpression expr, CType type) {
		super(context, expr, type);
		cachedExpr = expr;
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		return cachedExpr.getFamily( context );
	}
}
