package org.caesarj.compiler.ast;

import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class FjVariableDefinition extends JVariableDefinition {

	public FjVariableDefinition(
		TokenReference where,
		int modifiers,
		CType type,
		String ident,
		JExpression initializer) {
		super(where, modifiers, type, ident, initializer);
		cachedIdent = ident;
		cachedInitializer = initializer;
	}

	protected String cachedIdent;
	protected JExpression cachedInitializer;


	/**
	 * Initializes the family of the variable. Or if the type has not been
	 * checked, tries to find the type in the current context.
	 * 
	 * @param context
	 */
	public void initFamily(CClassContext context) 
	{
		try 
		{		
			FjFieldDeclaration field =
				(FjFieldDeclaration) 
					((FjAdditionalContext) context).peekContextInfo();
			CClass clazz =
				((FjClassDeclaration) 
					((FjAdditionalContext) 
						context).peekContextInfo(1)).getCClass();
	
			FjTypeSystem fjts = new FjTypeSystem();
			String[] split = fjts.splitQualifier(type.toString());
			FjFamily family = null;
			if (split != null ) 
			{
				String qualifier = split[0];
				String remainder = split[1];
				
				CField referedField = clazz.getField(qualifier);	

				family = fjts.resolveFamily(context, clazz, 
						qualifier, remainder);

				if (family != null)
				{
					FjFamilyContext.getInstance().setFamilyOf(this, family);
					field.setFamily(family);
					type = family.getInnerType();
				}
			}
			//It can be a type that must be lower bound
			else if (! type.checked())
			{
				CClass owner = clazz;
				CType lowerType = null;
				while (owner != null)
				{
					try
					{
						type = fjts.lowerBound(
							context, owner, type.toString());
						break;
					}
					catch (UnpositionedError e)
					{
						owner = owner.getOwner();
					}
				}
			}
		} 
		catch (ClassCastException e) 
		{// we are not in a class => continue 
		}
		catch (UnpositionedError e) 
		{
			context.reportTrouble(e.addPosition(getTokenReference()));
		}
	}

	public void analyse(CBodyContext context) throws PositionedError {

		FjTypeSystem fjts = new FjTypeSystem();		
		try {
			//Walter: inserted the sencod param in this method call
			FjFamily family = fjts.resolveFamily( context, 
				context.getClassContext().getCClass(), type );
			// if the typename is qualified by a variable
			// the qualifier has to be resolved to its type
			if( family != null ) {
				FjFamilyContext.getInstance().setFamilyOf( this, family );
				type = family.getInnerType();
				
				// if there is an initializer check its family
				if( cachedInitializer != null ) {
					fjts.checkFamilies(
						new CExpressionContext(
							context,
							context.getEnvironment() ),
						family,
						cachedInitializer );
				}
			}
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
		super.analyse( context );
	}


}
