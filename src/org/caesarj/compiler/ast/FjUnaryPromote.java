package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JUnaryPromote;

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
