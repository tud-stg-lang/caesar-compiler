package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JEqualityExpression;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JTypeNameExpression;

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
