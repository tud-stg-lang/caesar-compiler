package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class FjCastExpression extends JCastExpression {

	private FjFamily family;
	private boolean familyLookedUp;
	private boolean lookupThisFamily;
	private boolean insertRuntimeFamilyCheck;

	public FjCastExpression(
		TokenReference where,
		JExpression expr,
		CType dest) {
		this(where, expr, dest, true);
	}

	public FjCastExpression(
		TokenReference where,
		JExpression expr,
		CType dest,
		boolean lookupThisFamily) {
		this(where, expr, dest, lookupThisFamily, false);
	}

	public FjCastExpression(
		TokenReference where,
		JExpression expr,
		CType dest,
		boolean lookupThisFamily,
		boolean insertRuntimeFamilyCheck) {
		super(where, expr, dest);
		familyLookedUp = false;
		this.lookupThisFamily = lookupThisFamily;
		this.insertRuntimeFamilyCheck = insertRuntimeFamilyCheck;
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		lookUpFamily( context );
		if( family != null && insertRuntimeFamilyCheck ) {
			JMethodCallExpression newExpr = new JMethodCallExpression(
				getTokenReference(),
				new JTypeNameExpression(
					getTokenReference(),
					new CClassNameType( FjConstants.CAST_IMPL_NAME ) ),
				FjConstants.CHECK_FAMILY_METHOD_NAME,
				new JExpression[] {
					family.getFamilyAccessor(),
					new JCastExpression(
						getTokenReference(),
						expr,
						FjConstants.CHILD_TYPE )
				} );
			expr = newExpr;
		}
		return super.analyse(context);
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		return expr.toFamily(context);
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		lookUpFamily( context );
		if( family != null )
			return family;
		else
			return expr.getFamily(context);
	}
	
	protected void lookUpFamily( CExpressionContext context )
		throws PositionedError {
		if( familyLookedUp )
			return;

		familyLookedUp = true;			
		FjTypeSystem fjts = new FjTypeSystem();		
		try {
			// Walter: inserted the second param in the method call bellow
			family = fjts.resolveFamily(context.getBlockContext(), 
				context.getClassContext().getCClass(), dest, lookupThisFamily );				
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}

		if( family != null ) {
			dest = family.getInnerType();
		} else {
			try {
				dest = dest.checkType( context );
			} catch( UnpositionedError e ) {
				throw e.addPosition( getTokenReference() );
			}
		}
	}
}
