package org.caesarj.compiler.ast.phylum.variable;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.family.FjFamily;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class is temporary, it is here just because we don't know yet
 * what we have to do when introducing a down cast in the methods
 * that are defined in the bindings or providing classes. Once defined, in the
 * FjFormalParamenterDefinition, in the method introduceDownCastVariable this
 * class must be changed to FjFamilyVariableDefinition
 * @author Walter Augusto Werner
 */
public class CciFamilyVariableDefinition 
	extends JVariableDefinition
{

	/* FJRM private JExpression cachedInitializer; */
	public CciFamilyVariableDefinition(
		TokenReference where,
		int modifiers,
		CType type,
		String ident,
		JExpression initializer,
		FjFamily family)
	{
		super(where, modifiers, type, ident, initializer);
		/* FJRM 
		if (family != null)
			FjFamilyContext.getInstance().setFamilyOf(this, family);
		cachedInitializer = initializer; 
		*/
	}
	/**
	 * Method copied from FjVariableDefinition.
	 */
	public void analyse(CBodyContext context) throws PositionedError
	{
		/* FJRM
		FjTypeSystem fjts = new FjTypeSystem();
		try
		{
			//Walter: inserted the sencod param in this method call
			FjFamily family =
				fjts.resolveFamily(
					context,
					context.getClassContext().getCClass(),
					type);
			// if the typename is qualified by a variable
			// the qualifier has to be resolved to its type
			if (family != null)
			{
				FjFamilyContext.getInstance().setFamilyOf(this, family);
				//The line below is the only different statement 
				//comparing with the method defined in FjVariableDefinition
				//type = family.getInnerType();

				// if there is an initializer check its family
				if (cachedInitializer != null)
				{
					fjts.checkFamilies(
						new CExpressionContext(
							context,
							context.getEnvironment()),
						family,
						cachedInitializer);
				}
			}
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition(getTokenReference());
		}
		*/
		super.analyse(context);
	}

}
