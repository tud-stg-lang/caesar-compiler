package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JLocalVariableExpression;

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
