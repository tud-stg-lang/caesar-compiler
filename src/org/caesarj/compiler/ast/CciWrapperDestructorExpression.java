package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Represents a wrapper destructor expression in the source code. For all 
 * #<Type>(..) found in the source code, an instance of this class  will be 
 * created in the AST.
 * 
 * @author Walter Augusto Werner
 */
public class CciWrapperDestructorExpression extends JMethodCallExpression
{
	/**
	 * The wrapper type to destruct.
	 */
	private CReferenceType type;
	/**
	 * @param where
	 */
	public CciWrapperDestructorExpression(
		TokenReference where, 
		JExpression prefix,
		CReferenceType type, 
		JExpression[] args)
	{
		super(
			where, 
			prefix, 
			CciConstants.toWrapperMethodDestructionName(type.getQualifiedName()),
			args);
		this.type = type;
	}

	/**
	 * Analyse the method call to the destructor method. If it is not found,
	 * the type is not a wrapper, or it has not been defined.
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{
		try
		{
			return super.analyse(context);
		}
		catch(PositionedError e)
		{
			throw new PositionedError(
				getTokenReference(), 
				CaesarMessages.WRAPPER_DESTRUCTOR_NOT_FOUND,
				type.getQualifiedName());
		}
	}
}
