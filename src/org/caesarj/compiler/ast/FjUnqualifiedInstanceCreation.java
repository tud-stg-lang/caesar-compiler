package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;

public class FjUnqualifiedInstanceCreation
	extends JUnqualifiedInstanceCreation {

	private JExpression analysedSelf;

	public FjUnqualifiedInstanceCreation(
		TokenReference where,
		CReferenceType type,
		JExpression[] params) {
		super(where, type, params);
		cachedParams = params;
		analysedSelf = null;
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError {
		
		if( analysedSelf == null ) {
			analysedSelf = internalAnalyse( context );
		}
		
		return analysedSelf;
	}

	protected JExpression internalAnalyse( CExpressionContext context )
		throws PositionedError {
		CClass classOrInterface = null;
		FjTypeSystem fjts = new FjTypeSystem();
		try {
			type = (CReferenceType) type.checkType( context );
			classOrInterface = type.getCClass();
			if( fjts.isCleanIfc( context, classOrInterface ) ) {
				
				// look for a factory method
				String factoryMethodName = FjConstants.factoryMethodName( type.getIdent() );
				if( fjts.hasMethod( context.getClassContext().getCClass(), factoryMethodName )
					|| context.getClassContext().getCClass().getOwner() != null
					&& fjts.hasMethod( context.getClassContext().getCClass().getOwner(), factoryMethodName ) ) {
					CReferenceType lowerBoundType = fjts.lowerBound( context, type.getIdent() );
					return new FjCastExpression (
						getTokenReference(),
						new FjMethodCallExpression(
							getTokenReference(),
							null,
							factoryMethodName,
							cachedParams ),
						lowerBoundType ).analyse( context );
				}
				// there is no factory method so just
				// switch to constructing the base class
				return new FjUnqualifiedInstanceCreation(
					getTokenReference(),
					new CClassNameType( FjConstants.toImplName( classOrInterface.getIdent() ) ),
					cachedParams ).analyse( context );
			}
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
			
		return super.analyse(context);
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		JExpression analysed = analyse( context );
		if( analysed instanceof FjCastExpression )
			// -> translated to factory method
			return analysed.getFamily( context );
			
		return super.getFamily(context);
	}
	
	protected JExpression[] cachedParams;
}
