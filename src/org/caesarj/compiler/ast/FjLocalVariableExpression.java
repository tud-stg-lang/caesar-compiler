package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;


public class FjLocalVariableExpression extends JLocalVariableExpression {

	JLocalVariable cachedVar;

	public FjLocalVariableExpression(
		TokenReference where,
		JLocalVariable variable) {
		super(where, variable);
		cachedVar = variable;
	}
	
	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		return super.analyse(context);
	}
	
	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		if( cachedVar.getIdent() == FjConstants.SELF_NAME )
			return new FjThisExpression( getTokenReference() ).getFamily( context );
		return FjFamilyContext.getInstance().lookupFamily( cachedVar );
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		if( cachedVar.getIdent() == FjConstants.SELF_NAME )
			return new FjThisExpression( getTokenReference() ).toFamily( context );
		try {
			return new FjTypeSystem().resolveFamily( context, getIdent(), false );
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}
}
