package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.FjFamilyContext;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.FjFamily;
import org.caesarj.compiler.family.FjLinkedFamily;
import org.caesarj.compiler.family.FjTypeSystem;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

// FJTODO
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
		return new JFieldAccessExpression(ref, prefix, ident);
	}

	protected JFieldAccessExpression createClassField(
		TokenReference ref,
		String ident) {
		return new JFieldAccessExpression(ref, ident);
	}
	
	
	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		if( getPrefix() == null ) {
			String name = toString().intern();
			JLocalVariable	var = context.lookupLocalVariable( name );
			if( name == FjConstants.SELF_NAME ) {
				return new JThisExpression( getTokenReference() ).getFamily( context );
			} else	if( var != null ) {
				// return the variable's family
				return FjFamilyContext.getInstance().lookupFamily( var );
			} else {
				CClass currentClass = context.getClassContext().getCClass();
				try {
					CField field = context.lookupField( currentClass, currentClass, name.intern() );
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
