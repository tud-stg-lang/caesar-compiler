package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CClassContext;

public class FjThisFamily extends FjFamily {
	protected CClassContext context;
	
	public FjThisFamily( CClassContext context ) {
		super( new FjTypeSystem().cleanInterface( 
			context.getCClass() ).getAbstractType(),
			new JThisExpression( FjConstants.STD_TOKEN_REFERENCE ) );
		this.context = context;
	}
	
	public String getIdentification() {
		FjTypeSystem fjts = new FjTypeSystem();
		return fjts.cleanInterface( 
			context.getCClass() ).getQualifiedName() +
			"|" + FjConstants.THIS_NAME;
	}

	public boolean isThis() {
		return true;
	}	
}
