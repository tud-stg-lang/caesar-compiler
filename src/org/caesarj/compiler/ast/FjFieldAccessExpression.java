package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

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

     if( (prefix instanceof JLocalVariableExpression) &&
			((JLocalVariableExpression) prefix).getIdent().equals( FjConstants.SELF_NAME ) ) {
			return new FjFieldAccessExpression(
				getTokenReference(),
				new FjThisExpression( getTokenReference(), false ),
				ident).analyse( context );
		} else {
			try
			{
				return super.analyse( context );
			}
			catch(PositionedError e)
			{
				//If it does not find the field this$0 is because 
				//it is a virtual class accessing outer private methods
				//or accessing methods defined in outer outer ... class.
				if (e.getFormattedMessage().getDescription() 
					== KjcMessages.FIELD_UNKNOWN)
				{
					String fieldName = (String) e.getFormattedMessage()
						.getParams()[0];
					if (JAV_OUTER_THIS.equals(fieldName))
					{
						throw new PositionedError(
							getTokenReference(), 
							CaesarMessages.VIRTUAL_ACCESSING_OUTER_PRIVATE,
							context.getClassContext().getCClass()
								.getQualifiedName());
					}
				
				}
				throw e;
			}		
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
