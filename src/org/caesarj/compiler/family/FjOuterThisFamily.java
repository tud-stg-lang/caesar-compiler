package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CClassContext;


public class FjOuterThisFamily extends FjFamily {
	protected CClassContext context;
	
	public FjOuterThisFamily( CClassContext context ) {
		super( new FjTypeSystem().cleanInterface( 
			context.getCClass().getOwner() ).getAbstractType(),
			new JMethodCallExpression(
				FjConstants.STD_TOKEN_REFERENCE,
				null,
				FjConstants.GET_FAMILY_METHOD_NAME,
				JExpression.EMPTY ) );
		// inner class context
		this.context = context;
	}
	
	public String getIdentification() {
		FjTypeSystem fjts = new FjTypeSystem();
		return fjts.cleanInterface( 
			context.getCClass() ).getQualifiedName() +
			"|" + FjConstants.OUTER_THIS_NAME;
	}
	
	public boolean isOuterThis() {
		return true;
	}
}

