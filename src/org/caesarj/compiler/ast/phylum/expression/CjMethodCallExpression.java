package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.CMethodNotFoundError;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

// FJKEEP  
public class CjMethodCallExpression extends JMethodCallExpression {

	protected JExpression[] unanalysedArgs;
	
	/**
	 * The name of the type. (It could not be created 
	 * an abstraction for wrapper recycling operator, because it would cause
	 * a non-determinism in the language.
	 */
	protected CReferenceType wrapperType;
	
	/**
	 * The analyse method is called more than once. This flag is used for 
	 * know if the type of the method call has been already bound. 
	 */
	private boolean typeBound;	

	public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args) {
		this(where, prefix, ident, args, args);
	}

	public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args,
		JExpression[] unanalysedArgs) {
		super(where, prefix, ident, args);
		this.unanalysedArgs = new JExpression[unanalysedArgs.length];
		for (int i = 0; i < unanalysedArgs.length; i++) {
			this.unanalysedArgs[i] = unanalysedArgs[i];
		}
	}

	public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		CMethod method,
		JExpression[] args) {
		super(where, prefix, method, args);
	}

	public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		CMethod method,
		JExpression[] args,
		JExpression[] unanalysedArgs) {
		super(where, prefix, method, args);
		this.unanalysedArgs = new JExpression[unanalysedArgs.length];
		for (int i = 0; i < unanalysedArgs.length; i++) {
			this.unanalysedArgs[i] = unanalysedArgs[i];
		}
	}

	public JExpression getPrefix() {
		return prefix;
	}

	public void setPrefix(JExpression prefix) {
		this.prefix = prefix;
	}



	public JExpression analyse(CExpressionContext context)
		throws PositionedError 
	{
		JExpression expression;
		
		//If wrapper is not null, it is a wrapper recycling operator
		if (isWrapperRecycling())
			expression = handleWrapperRecyclingCall(context);
		else
			expression = analyseExpression(context);			
		
		
		return expression;
	}
	
	/**
	 * Returns true if the method is a wrapper recycling call.
	 * @return
	 */
	private boolean isWrapperRecycling()
	{
		return wrapperType != null;
	}

	/**
	 * Transforms a wrapper recycling call into a method call casting it 
	 * to the right type.
	 * @param context
	 * @return
	 */
	protected JExpression handleWrapperRecyclingCall(CExpressionContext context)
		throws PositionedError
	{
	
		TokenReference ref = getTokenReference();

		JCastExpression result = 
			new JCastExpression(
				ref,
				new CjMethodCallExpression(
					ref,
					prefix,
					ident,
					args),
				wrapperType);		

		return result.analyse(context);
	}

	/**
	 * Analyses the expression! :)
	 * If it does not find the field this$0 is because it is a virtual 
	 * class accessing outer private methods or accessing methods defined 
	 * in outer outer ... class, so it generates another error message in 
	 * this cases.
	 * @param context
	 */
	protected JExpression analyseExpression(CExpressionContext context) 
		throws PositionedError
	{
		try
		{
			return super.analyse(context);
		}
		catch(PositionedError e)
		{
			throw handleMethodNotFoundError(context, e);
		}			
	}
	/**
	 * It handles exceptions that can occur when looking for methods
	 * that are in the outer reference. It changes the message if it is the
	 * case.
	 * @param context
	 * @param e
	 * @return
	 */
	protected PositionedError handleMethodNotFoundError(
		CExpressionContext context, 
		PositionedError e)
	{
		//If it does not find the field this$0 is because 
		//it is a virtual class accessing outer private methods
		//or accessing methods defined in outer outer ... class.
		if (e.getFormattedMessage().getDescription() 
			== KjcMessages.FIELD_UNKNOWN)
		{
			String fieldName = (String) e.getFormattedMessage()
				.getParams()[0];
			if (JAV_OUTER_THIS.equals(fieldName))
			{
				if (method != null 
					&& CModifier.contains(method.getModifiers(), 
						ACC_PRIVATE))
					return new PositionedError(
						getTokenReference(), 
						CaesarMessages.VIRTUAL_ACCESSING_OUTER_PRIVATE,
						context.getClassContext().getCClass()
							.getQualifiedName());
				else 
					return new PositionedError(
						getTokenReference(), 
						CaesarMessages.VIRTUAL_CALLING_OUTER_OUTER,
						context.getClassContext().getCClass()
							.getQualifiedName());
			}
						
		}
		else if (e instanceof CMethodNotFoundError)
		{
			CClass local = context.getClassContext().getCClass();
			if (CModifier.contains(local.getModifiers(), FJC_VIRTUAL)
				&& CModifier.contains(
					local.getOwner().getModifiers(), FJC_CLEAN | FJC_VIRTUAL))
				return new PositionedError(
					getTokenReference(), 
					CaesarMessages.METHOD_NOT_FOUND_INSIDE_VIRTUAL,
					e.getFormattedMessage().getParams()[0]);

		}
				
		return e;
		
	}


	/**
	 * Overridden for allow the wrapper recycling construction.
	 * If the method is not found, it tries the wrapper recycling method. 
	 *
	 */
	protected void findMethod(
		CExpressionContext context,
		CClass local,
		CType[] argTypes)
		throws PositionedError
	{
		try
		{
			super.findMethod(context, local, argTypes);
		}
		catch (CMethodNotFoundError e)
		{
			
			
			try
			{
				super.findMethod(context, local, argTypes);
			}
			catch (CMethodNotFoundError e1)
			{
				throw handleMethodNotFoundError(context, e);
			}
			CClass owner = prefix == null
							? local 
							: prefix.getType(context.getTypeFactory())
								.getCClass();
								
		}
		catch (PositionedError e)
		{
			throw handleMethodNotFoundError(context, e);
		}
	
	}
	protected void setMethod(CExpressionContext context)
		throws PositionedError {
		CClass local = context.getClassContext().getCClass();
		CType[] argTypes = getArgumentTypes(context, args, 
			context.getTypeFactory());
		findMethod(context, local, argTypes);
		
		//If the method is not private and it is called in a clean 
		//class it must go to the clean interface
		//It is for allow calling outer this methods.
		CClass methodOwner = method.getOwner();
		if (method.isPublic() 
			&& local != methodOwner 
			&& CModifier.contains(methodOwner.getModifiers(), 
				(FJC_CLEAN | FJC_VIRTUAL | FJC_OVERRIDE)))
		{
			try 
			{
				method = methodOwner.lookupMethod(
					context.getClassContext(), 
					local, 
					null, 
					ident, 
					argTypes, 
					null);
			}
			catch(UnpositionedError e)
			{
				throw e.addPosition(getTokenReference());
			}
		}
		
			
	}

	private CType[] argTypes;
	protected CType[] getArgumentTypes(
		CExpressionContext context,
		JExpression[] args,
		TypeFactory factory)
		throws PositionedError {
		if (argTypes == null) 
			argTypes = super.getArgumentTypes(context, args, factory);

		return argTypes;
	}

	protected boolean hasAnUnqualifiedInstanceCreationPrefix() {
		if (prefix instanceof JQualifiedInstanceCreation)
			return true;
		else if (prefix instanceof CjMethodCallExpression)
			return ((CjMethodCallExpression) prefix)
				.hasAnUnqualifiedInstanceCreationPrefix();
		else
			return false;
	}

	protected CType prefixType;
	protected CType getPrefixType(CExpressionContext context)
		throws PositionedError {
		if (prefixType == null)
			prefixType = getMethod(context).getOwner().getAbstractType();
		return prefixType;
	}

	protected CMethod getMethod(CExpressionContext context)
		throws PositionedError {
		if (method == null)
			setMethod(context);
		return method;
	}


	public void setArgs(JExpression[] args) {
		this.args = args;
	}
}
