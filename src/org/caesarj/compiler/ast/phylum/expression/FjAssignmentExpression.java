package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

// FJPULLUP
public class FjAssignmentExpression extends JAssignmentExpression {

	public FjAssignmentExpression(
		TokenReference where,
		JExpression left,
		JExpression right) {
		super(where, left, right);
	}

	public FjAssignmentExpression(
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
		PositionedError aError = null;
		try {
			result = super.analyse(context);
		} catch (PositionedError e) {
			aError = e;
		}
		/* FJRM
		PositionedError fError = null;		
		try {
			FjTypeSystem fjts = new FjTypeSystem();
			fjts.checkFamilies(context, oldLeft, oldRight);
		} catch (PositionedError e) {
			fError = e;
		}
		if (fError != null) {
			if (aError != null)
				context.reportTrouble(aError);
			throw fError;
		} else*/ if (aError != null) {
			if (aError
				.getFormattedMessage()
				.getDescription()
				.equals(KjcMessages.ASSIGNMENT_NOTLVALUE)
				&& left instanceof FjMethodCallExpression) {

				FjMethodCallExpression methodCallExp =
					(FjMethodCallExpression) left;

				JExpression[] args = { methodCallExp.getPrefix(), right };
				methodCallExp.setArgs(args);

				result = methodCallExp;

			} else {

				throw aError;

			}

		}
		
		return result;
	}
	
	/*
	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		return left.getFamily(context);
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		return left.toFamily(context);
	}
	*/
}
