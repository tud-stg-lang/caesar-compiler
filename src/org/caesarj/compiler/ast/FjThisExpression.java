package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JThisExpression;

public class FjThisExpression extends JThisExpression {

	protected boolean transformToSelf;

	public FjThisExpression(TokenReference where) {
		this(where, true);
	}

	public FjThisExpression(TokenReference where, boolean transformToSelf) {
		super(where);
		this.transformToSelf = transformToSelf;
	}

	public FjThisExpression(TokenReference where, CClass self) {
		super(where, self);
	}

	public FjThisExpression(TokenReference where, JExpression prefix) {
		super(where, prefix);
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		
		if( isWithinImplementationMethod( context ) && transformToSelf )
			return new FjNameExpression (
				getTokenReference(),
				FjConstants.SELF_NAME ).analyse( context );
		else
			return super.analyse(context);
	}

	protected boolean isWithinImplementationMethod( CExpressionContext context ) {		
		return FjConstants.isImplementationMethodName(
			context.getMethodContext().getCMethod().getIdent() );
	}	

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		try {
			FjTypeSystem fjts = new FjTypeSystem();
			FjFamily family = fjts.resolveFamily(
				context.getBlockContext(),
				FjConstants.OUTER_THIS_NAME,
				false);
			return family;
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		try {
			return new FjTypeSystem().resolveFamily( context, FjConstants.THIS_NAME, false );
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}
}
