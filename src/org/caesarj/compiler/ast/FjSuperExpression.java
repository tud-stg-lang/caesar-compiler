package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;


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
