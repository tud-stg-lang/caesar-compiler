package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

// FJKEEP this one is needed to handle privileged access properly
public class CjAssignmentExpression extends JAssignmentExpression {

	public CjAssignmentExpression(
		TokenReference where,
		JExpression left,
		JExpression right) {
		super(where, left, right);
	}

	public CjAssignmentExpression(
		TokenReference where,
		JExpression left,
		JExpression right,
		CType type) {
		super(where, left, right, type);
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		// analyse before checking families to enable
		// checkFamilies to access final fields (after
		// analyse these are set)
		JExpression oldLeft = left;
		JExpression oldRight = right;
		JExpression result = null;

		try {
			result = super.analyse(context);
		} catch (PositionedError e) {
			if (e
				.getFormattedMessage()
				.getDescription()
				.equals(KjcMessages.ASSIGNMENT_NOTLVALUE)
				&& left instanceof CjMethodCallExpression) {

				CjMethodCallExpression methodCallExp =
					(CjMethodCallExpression) left;

				JExpression[] args = { methodCallExp.getPrefix(), right };
				methodCallExp.setArgs(args);

				result = methodCallExp;

			} else {
				throw e;
			}
		}
				
		
		return result;
	}
	
}
