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

// FJTODO 
// it will be best to keep this one and JMethodCallExpression separated  
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
		// FJRM cachedWhere = where;
	}

	public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		CMethod method,
		JExpression[] args,
		JExpression[] unanalysedArgs) {
		super(where, prefix, method, args);
		// FJRM cachedWhere = where;
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
		/* FJRM
		if (isWithinImplementationMethod(context) && ! isWrapperRecycling() 
			&& isSuperMethodCall())
			expression = handleSuperCall(context);
		else if (isWithinImplementationMethod(context) && ! isWrapperRecycling()
				&& isThisMethodCall(context))
			expression = handleThisCall(context);
		else
		*/ 
		{	
			/* FJRM		
			assertPrefixIsSet(context);
			checkFamilies(context);
			resetPrefix();
			*/
			//If wrapper is not null, it is a wrapper recycling operator
			if (isWrapperRecycling())
				expression = handleWrapperRecyclingCall(context);
			else
				expression = analyseExpression(context);			
		}
		
		/* FJRM
		//Walter: Cast the type for the most specific one.
		//Walter: It is because the type may be overridden in this context.
		if (! typeBound)
		{
			CReferenceType returnType = getOverriddenReturnType(context);
			if (returnType != null)
			{
				expression = new JCastExpression(getTokenReference(), 
					expression, returnType);
				typeBound = true;
			}
		}
		*/

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

	/* FJRM
	protected void assertPrefixIsSet(CExpressionContext context)
		throws PositionedError 
	{
		setMethod(context);

	
		if (prefix == null)
		{
			if (isThisMethodCall(context))
				prefix = new FjThisExpression(getTokenReference());
			else
				prefix = new OuterThisDummyPrefix(getTokenReference());
		}		
	}
	*/
	

	/**
	 * Returns the return type of the method that it calls if the type is 
	 * overridden in the context, otherwise it returns null. 
	 * The return type is bound to the most "specific" type, for example, 
	 * if in this context the return type of the method is overridden, 
	 * the overriden type is returned.
	 * 
	 * @param context
	 * @return the overriden type if it exists and <b>null</b> otherwise.
	 * @author Walter Augusto Werner
	 */
	/* FJRM
	private CReferenceType getOverriddenReturnType(CExpressionContext context) 
		throws PositionedError
	{
		CType returnType = getMethod(context).getReturnType();
			
		if (returnType.isReference()) 
		{
			CReferenceType bound = new FjTypeSystem().lowerBound(
				context, returnType.getCClass().getIdent());
				
			if (bound != null && ! bound.equals(returnType) 
				&& CModifier.contains(bound.getCClass().getModifiers(), 
					FJC_OVERRIDE))
				return bound;
		}
		return null;
	}

	protected void resetPrefix() {
		if (prefix instanceof OuterThisDummyPrefix)
			prefix = null;
	}

	
	protected void checkFamilies(CExpressionContext context)
		throws PositionedError 
	{
		if (getMethod(context) instanceof FjSourceMethod) 
		{
			FjFamily[] expectedFamilies =
				((FjSourceMethod) method).getFamilies();
			boolean thisFound = false;
			boolean outerThisFound = false;
			for (int i = 0; i < expectedFamilies.length; i++) {
				if (expectedFamilies[i] != null) {
					if (expectedFamilies[i].isThis())
						thisFound = true;
					else if (expectedFamilies[i].isOuterThis())
						outerThisFound = true;
				}
			}

			if (thisFound)
				check(
					context,
					prefix.toFamily(context.getBlockContext()) != null,
					CaesarMessages.NO_THIS_FAMILY,
					ident);

			if (outerThisFound)
				check(
					context,
					prefix.getFamily(context) != null,
					CaesarMessages.NO_OUTERTHIS_FAMILY,
					ident);

			FjTypeSystem fjts = new FjTypeSystem();
			for (int i = 0; i < args.length; i++) {
				if (expectedFamilies[i] != null
					&& expectedFamilies[i].isParameter())
					fjts.checkInFamily(
						context,
						unanalysedArgs[expectedFamilies[i].getParameterIndex()],
						unanalysedArgs[i]);
				else if (
					expectedFamilies[i] != null
						&& expectedFamilies[i].isThis())
					fjts.checkInFamily(context, prefix, unanalysedArgs[i]);
				else if (
					expectedFamilies[i] != null
						&& expectedFamilies[i].isOuterThis()) {
					fjts.checkFamilies(context, prefix, unanalysedArgs[i]);
				} else
					fjts.checkFamilies(
						context,
						expectedFamilies[i],
						unanalysedArgs[i]);
			}
		}
	}	

	protected String isCallToPrivateNonThis(
		CExpressionContext context,
		PositionedError e) {
		if (e.getFormattedMessage().getDescription()
			== KjcMessages.METHOD_NOT_FOUND) {
			String call = e.getFormattedMessage().getParams()[0].toString();
			int dotPosition = call.indexOf('.');
			if (!FjConstants.isPrivateAccessorId(ident) && dotPosition >= 0) {
				String typeName = call.substring(0, dotPosition);
				CClass cleanIfcOfContext =
					new FjTypeSystem().cleanInterface(
						context.getClassContext().getCClass());
				if (typeName.equals(cleanIfcOfContext.getIdent()))
					return typeName;
			}
		}
		return null;
	}
	
	protected JExpression handleSuperCall(CExpressionContext context)
		throws PositionedError {
		CClass clazz = context.getClassContext().getCClass();
		CReferenceType parentType = clazz.getSuperClass().getInterfaces()[0];
		//If it is in a binding or providing class it cannot call super
		if (! FjConstants.isIfcImplName(clazz.getIdent())
			&& ! FjConstants.isFactoryMethodName(ident)
			&& ! CciConstants.isAdaptMethodName(ident)
			&& CModifier.contains(clazz.getModifiers(), 
				(CCI_BINDING | CCI_PROVIDING)))
			throw 
				new CMethodNotFoundError(
					getTokenReference(), 
					this, 
					parentType.getQualifiedName() + 
						"." + ident, 
					getArgumentTypes(context, args, context.getTypeFactory()));



		// super is replaced by parent's target					
		JExpression parentGetTarget =
			new FjMethodCallExpression(
				getTokenReference(),
				new FjFieldAccessExpression(
					FjConstants.STD_TOKEN_REFERENCE,
					FjConstants.PARENT_NAME),
				FjConstants.GET_TARGET_METHOD_NAME,
				JExpression.EMPTY);

		JExpression parentPrefix =
			new FjCastExpression(
				getTokenReference(),
				parentGetTarget,
				parentType,
				false);

		// selfContext-method is called
		String parentMethod = FjConstants.selfContextMethodName(ident);

		// a dispatcher is passed as first parameter
		JExpression[] parentArgs = new JExpression[args.length + 1];
		for (int i = 0; i < unanalysedArgs.length; i++) {
			parentArgs[i + 1] = unanalysedArgs[i];
		}
		parentArgs[0] =
			new JMethodCallExpression(
				getTokenReference(),
				null,
				FjConstants.GET_DISPATCHER_METHOD_NAME,
				new JExpression[] {
					 new FjNameExpression(
						getTokenReference(),
						FjConstants.SELF_NAME)});

		// replace this call
		JMethodCallExpression parentCall =
			new FjMethodCallExpression(
				getTokenReference(),
				parentPrefix,
				parentMethod,
				parentArgs);

		if (isObjectsMethod(context)) {
			return new JConditionalExpression(
				getTokenReference(),
				new JEqualityExpression(
					getTokenReference(),
					true,
					parentGetTarget,
					new JThisExpression(getTokenReference())),
				new JMethodCallExpression(
					getTokenReference(),
					prefix,
					ident,
					args),
				parentCall).analyse(
				context);
		} else {
			return parentCall.analyse(context);
		}
	}	

	protected boolean isObjectsMethod(CExpressionContext context)
		throws PositionedError {
		String methodId =
			FjConstants.uniqueMethodId(ident, getArgumentTypes(context, args, 
				context.getTypeFactory()));
		return methodId.equals("toString")
			|| methodId.equals("hashCode")
			|| methodId.equals("finalize")
			|| methodId.equals("clone");
	}
	
	protected JExpression handleThisCall(CExpressionContext context)
		throws PositionedError {
		// use _self instead of this
		// if the method is not private
		JExpression newPrefix = null;
		JExpression[] newArgs = unanalysedArgs;
		String newIdent = ident;

		if ((getMethod(context).getModifiers() & ACC_PRIVATE) == 0)
			newPrefix =
				new FjNameExpression(
					getTokenReference(),
					FjConstants.SELF_NAME);

		// if it is private insert a cast in order
		// not to loop when returning here in recursion
		else {
			newPrefix =
				new FjCastExpression(
					getTokenReference(),
					new FjThisExpression(getTokenReference(), false),
					context.getClassContext().getCClass().getAbstractType());
			newArgs = new JExpression[unanalysedArgs.length + 1];
			for (int i = 0; i < unanalysedArgs.length; i++) {
				newArgs[i + 1] = unanalysedArgs[i];
			}
			newArgs[0] =
				new FjNameExpression(
					getTokenReference(),
					FjConstants.SELF_NAME);
			newIdent = FjConstants.implementationMethodName(ident);
		}

		// replace this call (it is inconsistent
		// now because we used findMethod)
		JExpression newExpression =
			new FjMethodCallExpression(
				getTokenReference(),
				newPrefix,
				newIdent,
				newArgs);

		//Walter if it is a wrapper recycling insert the cast before!
		if (isWrapperRecycling())
		{
			newExpression = 
				new FjCastExpression(
					getTokenReference(),
					newExpression,
					wrapperType);
		}
		return newExpression.analyse(context);
	}

	protected JExpression handlePrivateNonThisCall(
		CExpressionContext context,
		String typeName)
		throws PositionedError {

		return new FjMethodCallExpression(
			getTokenReference(),
			prefix,
			FjConstants.privateAccessorId(
				ident,
				typeName,
				FjConstants.uniqueMethodId(
					ident,
					getArgumentTypes(context, args, context.getTypeFactory()))),
			args).analyse(
			context);
	}

	protected boolean isThisMethodCall(CExpressionContext context)
		throws PositionedError {
		
		CClass local = context.getClassContext().getCClass();
		
		if (prefix != null && prefix instanceof JThisExpression 
			&& ((JThisExpression)prefix).getPrefix() == null)
			return true;
		else if (prefix != null)
			return false;
		else {
			CClass currentClass = context.getClassContext().getCClass();
			CClass currentCleanIfc =
				new FjTypeSystem().cleanInterface(currentClass);
			CClass methodOwner = getMethod(context).getOwner();
			return (
				currentClass.descendsFrom(methodOwner)
					|| currentCleanIfc.descendsFrom(methodOwner))
				&& !getMethod(context).isStatic();
		}
	}

	protected CMethod privateToBeAccessed(CExpressionContext context)
		throws PositionedError {
		if ((getMethod(context).getModifiers() & ClassfileConstants2.ACC_PRIVATE) != 0) {
			if (prefix != null) {
				CClass expectedPrefix = getPrefixType(context).getCClass();
				CReferenceType actualPrefix =
					(CReferenceType) prefix.analyse(context).getType(
						context.getTypeFactory());
				FjTypeSystem fjts = new FjTypeSystem();
				if (fjts.cleanInterface(expectedPrefix).equals(actualPrefix))
					return getMethod(context);
			}
		}
		return null;
	}
	*/
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
								
/* FJRM wrappee								
			while (wrapperType == null)
			{
				try
				{				
					wrapperType = 
						(CReferenceType) 
							new FjTypeSystem()
								.lowerBound(
									context.getClassContext(),
									owner, 
									wrapperTypeName);
				}
				catch (UnpositionedError e2)
				{
					owner = owner.getOwner();
					if (owner == null)
						throw e2.addPosition(getTokenReference());
				}
			}
*/	
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
			/* FJRM
			CClass cleanLocal = new FjTypeSystem().cleanInterface(methodOwner);
			*/
			CClass cleanLocal = methodOwner; // FJADDED
			try 
			{
				method = cleanLocal.lookupMethod(
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

	/* FJRM
	protected boolean returnTypeIsPrefixVirtualInner(CExpressionContext context)
		throws PositionedError {

		if (!getMethod(context).getReturnType().isReference())
			return false;

		CClass methodReturn = getMethod(context).getReturnType().getCClass();
		CClass prefixClass = getPrefixType(context).getCClass();
		if ((methodReturn.getModifiers() & ClassfileConstants2.FJC_VIRTUAL) != 0
			&& methodReturn.getOwner().equals(prefixClass))
			return true;
		else if (FjConstants.isFactoryMethodName(ident))
			return true;
		else if (isWrapperRecycling())
			return true;			
		return false;
	}

	protected boolean returnTypeIsPrefixsOwnerVirtualInner(CExpressionContext context)
		throws PositionedError {

		if (!getMethod(context).getReturnType().isReference())
			return false;

		if (!getPrefixType(context).getCClass().isNested())
			return false;

		if ((getMethod(context).getReturnType().getCClass().getModifiers()
			& ClassfileConstants2.FJC_VIRTUAL)
			!= 0
			&& getMethod(context).getReturnType().getCClass().getOwner().equals(
				getPrefixType(context).getCClass().getOwner()))
			return true;

		return false;
	}

	protected boolean isWithinImplementationMethod(CExpressionContext context) {
		return FjConstants.isImplementationMethodName(
			context.getMethodContext().getCMethod().getIdent());
	}

	protected boolean isSuperMethodCall() {
		return ((prefix != null) && (prefix instanceof JSuperExpression) 
			&& ((JSuperExpression)prefix).getPrefix() == null);
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError {
		FjFamily result = null;
		FjFamilyContext fc = FjFamilyContext.getInstance();
		assertPrefixIsSet(context);
		if (ident == FjConstants.GET_PARENT_METHOD_NAME)
			result =
				new FjSuperExpression(getTokenReference()).getFamily(context);
		else if (ident == FjConstants.GET_TARGET_METHOD_NAME)
			result =
				new FjThisExpression(getTokenReference()).getFamily(context);
		else if (returnTypeIsPrefixVirtualInner(context)) {
			result = prefix.toFamily(context.getBlockContext());
			result =
				fc.addTypesFamilies(
					(result != null) ? result.first() : null,
					(CReferenceType) getMethod(context).getReturnType());
		} else if (returnTypeIsPrefixsOwnerVirtualInner(context)) {
			result = prefix.getFamily(context);
			result =
				fc.addTypesFamilies(
					(result != null) ? result.first() : null,
					(CReferenceType) getMethod(context).getReturnType());
		}
		resetPrefix();
		return result;
	}

	public FjFamily toFamily(CBlockContext context) throws PositionedError {
		if (ident == FjConstants.GET_PARENT_METHOD_NAME 
			|| ident == FjConstants.GET_TARGET_METHOD_NAME)
			return new FjSuperExpression(getTokenReference()).toFamily(context);
		return null;
	}

	protected TokenReference cachedWhere;

	protected class OuterThisDummyPrefix extends JExpression {
		public OuterThisDummyPrefix(TokenReference where) {
			super(where);
		}
		public void accept(KjcVisitor p) {
		}
		public JExpression analyse(CExpressionContext context)
			throws PositionedError {
			return null;
		}
		public void genCode(GenerationContext context, boolean discardValue) {
		}
		public CType getType(TypeFactory factory) {
			return null;
		}
		public FjFamily toFamily(CBlockContext context)
			throws PositionedError {
			try {
				return new FjTypeSystem().resolveFamily(
					context,
					FjConstants.OUTER_THIS_NAME,
					false);
			} catch (UnpositionedError e) {
				throw e.addPosition(getTokenReference());
			}
		}
	}
	*/

	public void setArgs(JExpression[] args) {
		this.args = args;
	}
}
