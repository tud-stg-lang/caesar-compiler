package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Represents a wrappee expression in the source code. For instance, 
 * wrappee.<MethodCall>() the primary expression will be an instance of
 * this class in the AST.
 * 
 * @author Walter Augusto Werner
 */
public class CciWrappeeExpression 
	extends FjMethodCallExpression
{

	/**
	 * @param where
	 * @param ident
	 */
	public CciWrappeeExpression(TokenReference where)
	{
		super(
			where, 
			null, 
			CciConstants.WRAPPEE_METHOD_NAME,
			JExpression.EMPTY);
	}
	
	

	/**
	 * Tries to analyse the method call to the wrapper method, 
	 * if it is not found, it is not in a Wrapper context.
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
			//Ops, it is not in the context of a Wrapper
			throw new PositionedError(
				getTokenReference(),
				CaesarMessages.CLASS_DOES_NOT_WRAP,
				context.getClassContext().getCClass().getQualifiedName());
		}
		
	}

}
