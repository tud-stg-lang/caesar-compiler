package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents instance creation that are created by the compiler.
 * The only diference between it and FjUnqualifiedInstanceCreation is that
 * it relaxes the checkings. It does not check if the class is providing, 
 * binding or collaboration.
 * 
 * @author Walter Augusto Werner
 */
public class CciInternalUnqualifiedInstanceCreation
	extends FjUnqualifiedInstanceCreation
{

	/**
	 * @param where
	 * @param type
	 * @param params
	 */
	public CciInternalUnqualifiedInstanceCreation(
		TokenReference where,
		CReferenceType type,
		JExpression[] params)
	{
		super(where, type, params);
	}

	/**
	 * It does not need to check it here. So do nothing! :)
	 */
	protected void checkCleanClassCreation(
		CExpressionContext context,
		CClass clazz)
		throws PositionedError
	{
	}

	/**
	 * It does not need to check it here. So do nothing! :)
	 */
	protected void checkVirtualClassCreation(
		CExpressionContext context,
		CClass clazz)
		throws PositionedError
	{
	}

}
