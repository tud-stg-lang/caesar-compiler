package org.caesarj.compiler.ast;

import org.caesarj.kjc.CBodyContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

public class FjReturnStatement extends JReturnStatement {
	
	public FjReturnStatement(
		TokenReference where,
		JExpression expr,
		JavaStyleComment[] comments) {
		super(where, expr, comments);
	}

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
}
