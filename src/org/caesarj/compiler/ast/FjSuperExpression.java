package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JSuperExpression;

public class FjSuperExpression extends JSuperExpression {

	public FjSuperExpression(TokenReference where) {
		super(where);
	}

	public FjSuperExpression(TokenReference where, JExpression prefix) {
		super(where, prefix);
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		try {
			return new FjTypeSystem().resolveFamily( context.getBlockContext(), FjConstants.OUTER_THIS_NAME, false );
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
