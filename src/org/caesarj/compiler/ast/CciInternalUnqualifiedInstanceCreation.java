package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.JExpression;

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
