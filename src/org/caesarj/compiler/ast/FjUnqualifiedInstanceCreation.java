package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class FjUnqualifiedInstanceCreation extends JUnqualifiedInstanceCreation
{

	private JExpression analysedSelf;

	public FjUnqualifiedInstanceCreation(
		TokenReference where,
		CReferenceType type,
		JExpression[] params)
	{
		super(where, type, params);
		cachedParams = params;
		analysedSelf = null;
	}

	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{

		if (analysedSelf == null)
		{
			analysedSelf = internalAnalyse(context);
		}

		return analysedSelf;
	}

	protected JExpression internalAnalyse(CExpressionContext context)
		throws PositionedError
	{
		CClass classOrInterface = null;
		FjTypeSystem fjts = new FjTypeSystem();

		try
		{
			type = (CReferenceType) type.checkType(context);
		}
		catch (UnpositionedError e)
		{
			//If the error is CLASS_AMBIGUOUS and 
			//the type is a clean class take the first found
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.CLASS_AMBIGUOUS)
			{
				CClass[] candidates =
					(CClass[]) e.getFormattedMessage().getParams()[1];
				if (fjts.isCleanIfc(context, candidates[0]))
					type = candidates[0].getAbstractType();
				else
					throw e.addPosition(getTokenReference());
			}
			else
				throw e.addPosition(getTokenReference());
		}
		
		classOrInterface = type.getCClass();
		if (fjts.isCleanIfc(context, classOrInterface))
		{

			// look for a factory method
			String factoryMethodName =
				FjConstants.factoryMethodName(type.getIdent());
			if (fjts
				.hasMethod(
					context.getClassContext().getCClass(),
					factoryMethodName)
				|| context.getClassContext().getCClass().getOwner() != null
				&& fjts.hasMethod(
					context.getClassContext().getCClass().getOwner(),
					factoryMethodName))
			{
				// check if it is creating an allowed class.
				checkVirtualClassCreation(context, classOrInterface);

				CReferenceType lowerBoundType =
					fjts.lowerBound(context, type.getIdent());
				return new FjCastExpression(
					getTokenReference(),
					new FjMethodCallExpression(
						getTokenReference(),
						null,
						factoryMethodName,
						cachedParams),
					lowerBoundType).analyse(
					context);
			}
			//check if it is creating an allowed class.
			checkCleanClassCreation(context, classOrInterface);
			// there is no factory method so just
			// switch to constructing the base class
			return new CciInternalUnqualifiedInstanceCreation(
				getTokenReference(),
				new CClassNameType(
					FjConstants.toImplName(classOrInterface.getIdent())),
				cachedParams).analyse(
				context);
		}

		return super.analyse(context);
	}
	
	/**
	 * Chacks if the virtual class is a providing or a collaboration class.
	 * @param context
	 * @param clazz
	 * @throws PositionedError
	 */
	protected void checkVirtualClassCreation(CExpressionContext context, 
		CClass clazz) 
		throws PositionedError
	{
		check(
			context,
			FjConstants.isFactoryMethodName(
				context.getMethodContext().getCMethod().getIdent())
				|| FjTypeSystem.getClassInHierarchy(
					clazz,
					(CCI_COLLABORATION | CCI_PROVIDING))
					== null
				|| FjTypeSystem.getClassInHierarchy(
					context.getClassContext().getCClass(),
					CCI_WEAVELET) != null,
			CaesarMessages.BINDING_PROVIDING_DIRECT_CREATION,
			clazz.getQualifiedName());		
	}
	/**
	 * Checks if the clean class is a providing, a collaboration 
	 * or a binding class.
	 * @param context
	 * @param clazz
	 * @throws PositionedError
	 */
	protected void checkCleanClassCreation(CExpressionContext context, 
		CClass clazz) 
		throws PositionedError
	{
		check(
			context,
				FjTypeSystem.getClassInHierarchy(
					clazz,
					(CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING),
					CCI_WEAVELET)
					== null,
			CaesarMessages.BINDING_PROVIDING_DIRECT_CREATION,
			clazz.getQualifiedName());		
	}

	public FjFamily getFamily(CExpressionContext context)
		throws PositionedError
	{
		JExpression analysed = analyse(context);
		if (analysed instanceof FjCastExpression)
			// -> translated to factory method
			return analysed.getFamily(context);

		return super.getFamily(context);
	}

	protected JExpression[] cachedParams;
}
