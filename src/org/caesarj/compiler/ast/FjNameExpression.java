package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class FjNameExpression extends JNameExpression {

	public FjNameExpression(
		TokenReference where,
		JExpression prefix,
		String ident) {
		super(where, prefix, ident);
	}

	public FjNameExpression(TokenReference where, String ident) {
		super(where, ident);
	}
	
	protected JFieldAccessExpression createClassField(
		TokenReference ref,
		JExpression prefix,
		String ident,
		TypeFactory factory) {
		return new FjFieldAccessExpression(ref, prefix, ident);
	}

	protected JFieldAccessExpression createClassField(
		TokenReference ref,
		String ident) {
		return new FjFieldAccessExpression(ref, ident);
	}
	
	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		if( getPrefix() == null ) {
			String name = toString().intern();
			JLocalVariable	var = context.lookupLocalVariable( name );
			if( name == FjConstants.SELF_NAME ) {
				return new FjThisExpression( getTokenReference() ).getFamily( context );
			} else	if( var != null ) {
				// return the variable's family
				return FjFamilyContext.getInstance().lookupFamily( var );
			} else {
				CClass currentClass = context.getClassContext().getCClass();
				try {
					CField field = context.lookupField( currentClass, currentClass, name.intern() );
					if( field != null && field instanceof FjSourceField )
						return ((FjSourceField)field).getFamily();
					else
						return null;
				} catch( UnpositionedError err ) {
					throw err.addPosition( getTokenReference() );
				}
			}
		} else {
			CClass prefixType = null;
			CClass ownType = null;
			try {
				prefixType = getPrefix().analyse( context ).
					getType( context.getTypeFactory() ).getCClass();
				ownType = analyse( context ).
					getType( context.getTypeFactory() ).getCClass();
			} catch( InconsistencyException e ) {
				return null;
			}

			//Walter: Now it looks for the family of the accessed field.
			CField field = prefixType.getField(ident);
			if (field != null && field instanceof FjSourceField)
			{
				FjFamily fieldFamily = null;
				FjFamily prefixFamily = null;
				if ((ownType.getModifiers() & Constants.FJC_VIRTUAL) != 0)
				{
					fieldFamily = ((FjSourceField) field).getFamily();
					prefixFamily = getPrefix().toFamily(context.getBlockContext());
					if (prefixFamily != null)
					{
						if (fieldFamily != null) 
							return new FjLinkedFamily(prefixFamily, fieldFamily);
						return prefixFamily;
			}
				}
			}

			return null;
		}
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError
	{
		try
		{
			if( getPrefix() == null )
				return new FjTypeSystem().resolveFamily(
					context,
					toString(),
					false);
			else
			{ //Walter: the prefix is relevant now.
				FjFamily prefixFamily = getPrefix().toFamily(context);
				FjFamily myFamily;
				if (prefixFamily != null)
				{
					myFamily =
						new FjTypeSystem().resolveFamily(
							context,
							prefixFamily.getType().getCClass(),
							ident,
							false);
					if (myFamily != null)
						return new FjLinkedFamily(prefixFamily, myFamily);
					
					return prefixFamily;//TODO:Check if the return prefixFamily is right
				}
				else
					myFamily =
						new FjTypeSystem().resolveFamily(context, ident, false);

				return myFamily; //Return my family
			}//Walter end
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition( getTokenReference() );
		}
	}
}
