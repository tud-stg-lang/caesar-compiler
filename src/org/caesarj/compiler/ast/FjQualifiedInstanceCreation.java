package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class FjQualifiedInstanceCreation
	extends JQualifiedInstanceCreation {

	public FjQualifiedInstanceCreation(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] params) {
		super(where, prefix, ident, params);
		cachedPrefix = prefix;
		cachedParams = params;
		cachedIdent = ident;
	}

	public JExpression getPrefix() {
		return cachedPrefix;
	}

	protected CReferenceType prefixType;
	public CReferenceType getPrefixType( CExpressionContext context ) throws PositionedError {
		if( prefixType == null )
			prefixType = (CReferenceType)
				getPrefix().analyse( context ).getType( context.getTypeFactory() );
		return prefixType;
	}

	protected CReferenceType ownType;
	public CReferenceType getOwnType( CExpressionContext context ) throws PositionedError {
		if( ownType == null )
			try {
				ownType = (CReferenceType) new FjTypeSystem().
					lowerBound( context, getPrefixType( context ).getCClass(), cachedIdent).
						checkType( context );
			} catch( UnpositionedError e ) {
				throw e.addPosition( getTokenReference() );
			}
		return ownType;
	}
	
	public JExpression analyse(CExpressionContext context)
		throws PositionedError {

		String factoryMethodName = FjConstants.factoryMethodName( cachedIdent );
		FjTypeSystem fjts = new FjTypeSystem();
		
		// in the following lines we assume that a type
		// is virtual and be instantiated by a factory
		// method iff a matching method exists.
		if( fjts.hasMethod( getPrefixType( context ).getCClass(), factoryMethodName ) ) {
			CReferenceType ownType = getOwnType( context );
			//It cannot create a collaboration or a providing out of the 
			//factory method, but binding it can.
			check(
				context,
				FjConstants.isFactoryMethodName(
					context.getMethodContext().getCMethod().getIdent())
					|| FjTypeSystem.getClassInHierarchy(
						ownType.getCClass(),
						(CCI_COLLABORATION | CCI_PROVIDING),
						CCI_BINDING) == null,
				CaesarMessages.BINDING_PROVIDING_DIRECT_CREATION,
				ownType.getQualifiedName());

			return new FjCastExpression (
				getTokenReference(),
				new FjMethodCallExpression(
					getTokenReference(),
					cachedPrefix,
					factoryMethodName,
					cachedParams ),
				ownType,
				false ).analyse( context );
			

		}
		return super.analyse(context);
	}	
	
	public String toString() {
		return cachedIdent;
	}
	
	private JExpression cachedPrefix;
	private String cachedIdent;
	private JExpression[] cachedParams;

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		FjFamilyContext fc = FjFamilyContext.getInstance();
		FjFamily prefixFamily = getPrefix().toFamily( context.getBlockContext() );
		return fc.addTypesFamilies( (prefixFamily!=null)? prefixFamily.first() : null,
			getOwnType( context ) );
	}

}
