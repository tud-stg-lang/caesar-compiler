package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class FjEqualityExpression extends JEqualityExpression {

	public FjEqualityExpression(
		TokenReference where,
		boolean equal,
		JExpression left,
		JExpression right) {
		super(where, equal, left, right);
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		return new JMethodCallExpression(
			getTokenReference(),
			new JTypeNameExpression(
				getTokenReference(),
				new CClassNameType( FjConstants.IDENTITY_TYPE_NAME ) ),
			FjConstants.IS_IDENTICAL_METHOD_NAME,
			new JExpression[] { left, right } ).analyse( context );
	}
}
