package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

// FJPULLUP
public class FjReturnStatement extends JReturnStatement {
	
	public FjReturnStatement(
		TokenReference where,
		JExpression expr,
		JavaStyleComment[] comments) {
		super(where, expr, comments);
	}

	/* FJRM
	public void analyse(CBodyContext context) throws PositionedError {
		JExpression unAnalysedExpr = expr;	
		super.analyse(context);
		CType returnType = context.getMethodContext().getCMethod().getReturnType();

		if( !returnType.isReference() )
			return;

		CClass contextClass = context.getClassContext().getCClass();
		CClass returnClass = returnType.getCClass();

		if( !((returnClass.getModifiers() & Constants.FJC_VIRTUAL) != 0) )
			return;

		FjTypeSystem fjts = new FjTypeSystem();
		CExpressionContext expressionContext = new CExpressionContext( context, context.getEnvironment() );
		
		if( returnClass.getOwner().equals( contextClass ) ) {
			// family (this) expected
			fjts.checkFamilies(
				expressionContext,
				new FjThisExpression( getTokenReference() ).toFamily( expressionContext.getBlockContext() ),
				unAnalysedExpr );
		} else if( contextClass.getOwner() != null 
			&& returnClass.getOwner().equals( contextClass.getOwner() ) ) {
			// family (outerThis) expected
			fjts.checkFamilies(
				expressionContext,
				new FjThisExpression( getTokenReference() ).getFamily( expressionContext ),
				unAnalysedExpr );
		}
	}
	*/
}
