package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.JExpression;

/**
 * @author Walter Augusto Werner
 */
public class CciWrappeeExpression extends FjNameExpression
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
		super(where, prefix, CciConstants.WRAPPEE_FIELD_NAME);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param where
	 * @param ident
	 */
	public CciWrappeeExpression(TokenReference where)
	{
		super(where, CciConstants.WRAPPEE_FIELD_NAME);
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * 
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{
		CClass clazz = context.getClassContext().getCClass();
		check(context, 
			clazz.getField(CciConstants.WRAPPEE_FIELD_NAME) != null, 
			CaesarMessages.CLASS_DOES_NOT_WRAP,
			clazz.getQualifiedName());
		return super.analyse(context);
	}

}
