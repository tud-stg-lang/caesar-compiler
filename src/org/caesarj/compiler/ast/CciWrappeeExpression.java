package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CMethodNotFoundError;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.JExpression;

/**
 * @author Walter Augusto Werner
 */
public class CciWrappeeExpression extends FjMethodCallExpression
{

	/**
	 * @param where
	 * @param prefix
	 * @param ident
	 */
	public CciWrappeeExpression(
		TokenReference where,
		JExpression prefix)
	{
		super(
			where, 
			prefix, 
			CciConstants.WRAPPEE_METHOD_NAME,
			JExpression.EMPTY);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param where
	 * @param ident
	 */
	public CciWrappeeExpression(TokenReference where)
	{
		this(where, null);
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * 
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{
		CClass clazz = context.getClassContext().getCClass();
		try
		{
			//Is it in the wrapper context?
			findMethod(context, clazz, CReferenceType.EMPTY);
		}
		catch(CMethodNotFoundError e)
		{
			throw new PositionedError(
				getTokenReference(),
				CaesarMessages.CLASS_DOES_NOT_WRAP,
				clazz.getQualifiedName());
		}
		return super.analyse(context);
	}

}
