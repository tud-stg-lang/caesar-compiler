package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CField;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldAccessExpression;
import org.caesarj.kjc.JLocalVariableExpression;
import org.caesarj.kjc.JTypeNameExpression;

public class FjFieldAccessExpression extends JFieldAccessExpression {

	public FjFieldAccessExpression(
		TokenReference where,
		JExpression prefix,
		String ident) {
		super(where, prefix, ident);
	}

	public FjFieldAccessExpression(TokenReference where, String ident) {
		super(where, ident);
	}

	public FjFieldAccessExpression(
		TokenReference where,
		JExpression prefix,
		CField field) {
		super(where, prefix, field);
	}
	
	public JExpression analyse(CExpressionContext context)
		throws PositionedError {

		if( ident == FjConstants.SUPER ) {
			JExpression fjSuper = new FjCastExpression(
				getTokenReference(),
				new FjMethodCallExpression(
					getTokenReference(),
					new JTypeNameExpression(
						getTokenReference(),
						new CClassNameType( FjConstants.CLASS_BASED_DELEGATION_TYPE ) ),
					FjConstants.CLASS_BASED_DELEGATION_SUPER,
					new JExpression[] { prefix } ),
				findSuperType( context ) );
			return fjSuper.analyse( context );
		} else if( ident == FjConstants.SUB ) {
			JExpression fjSub = new FjMethodCallExpression(
				getTokenReference(),
				new JTypeNameExpression(
					getTokenReference(),
					new CClassNameType( FjConstants.CLASS_BASED_DELEGATION_TYPE ) ),
				FjConstants.CLASS_BASED_DELEGATION_SUB,
				new JExpression[] { prefix } );
			return fjSub.analyse( context );
		} else if( (prefix instanceof JLocalVariableExpression) &&
			((JLocalVariableExpression) prefix).getIdent().equals( FjConstants.SELF_NAME ) ) {
			return new FjFieldAccessExpression(
				getTokenReference(),
				new FjThisExpression( getTokenReference(), false ),
				ident).analyse( context );
		} else {
			return super.analyse( context );
		}
	}

	protected CReferenceType findSuperType( CExpressionContext context )
		throws PositionedError {
		try {
			FjTypeSystem fjts = new FjTypeSystem();
			JExpression analysedPrefix = prefix.analyse( context );
			CType prefixType = analysedPrefix.getType( context.getTypeFactory() );
				prefixType = prefixType.checkType( context );
			CClass prefixClass = prefixType.getCClass();
			CClass contextClass = context.getClassContext().getCClass();
			CClass superClass = fjts.superClassOf( prefixClass );
			if( superClass == null ) {
				return (CReferenceType) FjConstants.CHILD_TYPE.checkType( context );
			}
			CReferenceType superType = superClass.getAbstractType();
			if( contextClass.descendsFrom( prefixClass.getOwner() )
				&& !contextClass.equals( prefixClass.getOwner() ) ) {
					superType = fjts.lowerBound(
						context,
						contextClass,
						superClass.getIdent() );
			}
			return superType;
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}
	
	public FjFamily getFamily(CExpressionContext context) 
		throws PositionedError 
	{
		try {
			
			FjFamily myFamily = null;
			if (prefix != null)
				myFamily = prefix.getFamily(context);//toFamily(context.getBlockContext());
				
			CClass currentClass;
			if (myFamily == null)
				currentClass = context.getClassContext().getCClass();
			else
				currentClass = myFamily.getType().getCClass();
				
			CField field = context.lookupField( currentClass, currentClass, 
				getIdent().intern() );
				
			if( field != null && field instanceof FjSourceField )
			{
				FjFamily fieldFamily = ((FjSourceField)field).getFamily();

				if (fieldFamily == null)
					return myFamily;

				if (myFamily != null)
					return new FjLinkedFamily(myFamily, fieldFamily);

				return fieldFamily;
			}
			else
				return myFamily;
		} catch( UnpositionedError err ) {
			throw err.addPosition( getTokenReference() );
		}
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		try {
			if (prefix == null)
				return new FjTypeSystem().resolveFamily( context, 
					getIdent(), false );
			else
			{//Walter start
				FjFamily prefixFamily = prefix.toFamily(context);
				FjFamily myFamily;
				
				if (prefixFamily != null)
				{
					myFamily = new FjTypeSystem().resolveFamily(context, 
									prefixFamily.getType().getCClass(),
									getIdent(), false);
					
					if (myFamily == null)
						return prefixFamily;
														
					return new FjLinkedFamily(prefixFamily, myFamily);
				}
				
				return new FjTypeSystem().resolveFamily(context, 
						getIdent(), false);
					
			}//Walter end
				
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}
}
