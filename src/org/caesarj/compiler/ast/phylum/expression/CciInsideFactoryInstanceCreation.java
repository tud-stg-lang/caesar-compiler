package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents the creation of virtual types inside the 
 * factory method of clean classes. Therefore, it depends of the variable self.
 * 
 * It could be used in other methods inside the class that defines the virtual 
 * class, but only in the implementation methods.
 * 
 * @author Walter Augusto Werner
 */
public class CciInsideFactoryInstanceCreation
	extends JUnqualifiedInstanceCreation
{
	/**
	 * The expression to access the self parameter.
	 */
	private JExpression selfAccess;

	/**
	 * @param where
	 * @param type
	 * @param params
	 */
	public CciInsideFactoryInstanceCreation(
		TokenReference where,
		CReferenceType type,
		JExpression[] params)
	{
		super(where, type, params);
	}

	/**
	 * Creates the self expression and analyse it.
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{
		JExpression returnExpression = super.analyse(context);

		selfAccess = 
			new JNameExpression(
				getTokenReference(), 
				null, 
				FjConstants.SELF_NAME);

		selfAccess = selfAccess.analyse(context);
		
		return returnExpression;
	}
	
	/**
	 * Generates JVM bytecode to evaluate this expression. The byte code
	 * generation here is different of the normal creation. It loads
	 * the self parameter to be passed to the constructors of the nested
	 * class. It is because the inner classes of clean classes must have
	 * the parameter this$0 set with the first receiver of the call.
	 *
	 * @param	context		the bytecode context
	 * @param	discardValue	discard the result of the evaluation?
	 */
	public void genCode(GenerationContext context, boolean discardValue)
	{
		//this is the same as before
		CodeSequence code = context.getCodeSequence();

		setLineNumber(code);

		code.plantClassRefInstruction(
			opc_new,
			type.getCClass().getQualifiedName());

		if (!discardValue)
		{
			code.plantNoArgInstruction(opc_dup);
		}
		
		//Here it changes!
		if (local.getOwner() == null)
		{
			//access self instead of this.
			//code.plantLoadThis();
			selfAccess.genCode(context, discardValue);
		}
		else
		{
			code.plantLoadThis();
			//This is the same as in a normal creation access to $this
			code.plantFieldRefInstruction(
				opc_getfield,
				local.getAbstractType().getSignature().substring(
					1,
					local.getAbstractType().getSignature().length()
						- 1),
				JAV_OUTER_THIS,
				local.getOwnerType().getSignature());
		}
		
		//This is the same as before
		for (int i = 0; i < params.length; i++)
		{
			params[i].genCode(context, false);
		}

		constructor.getOwner().genOuterSyntheticParams(context);

		constructor.genCode(context, true);
	}
}
