package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CMethodContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JStringLiteral;
import org.caesarj.kjc.JThrowStatement;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;
import org.caesarj.kjc.TypeFactory;

/**
 * This is the representation of the method that won't be called during the
 * execution. These methods are the provided methods in the binding classes
 * and the expected methods in the implementation classes.  
 * 
 * @author Walter Augusto Werner
 */
public class CciSourceUncallableMethod 
	extends CSourceMethod
{

	/**
	 * @param factory
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 */
	public CciSourceUncallableMethod(
		TypeFactory factory,
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		CType[] paramTypes,
		CReferenceType[] exceptions,
		CTypeVariable[] typeVariables)
	{
		super(
			owner,
			modifiers,
			ident,
			returnType,
			paramTypes,
			exceptions,
			typeVariables,
			false,
			false,
			getDefaultBody(factory));

	}
	
	/**
	 * Creates a new body. This body will be a throw statement. The 
	 * exception it throws is AbstractMethodError.
	 * 
	 * @param factory
	 * @return the default body
	 */
	protected static JBlock getDefaultBody(TypeFactory factory)
	{
		TokenReference ref = TokenReference.NO_REF;
		JStatement[] body =
			new JStatement[] {
				 new JThrowStatement(
					ref,
					new JUnqualifiedInstanceCreation(
						ref,
						factory.createType(
							"java/lang/AbstractMethodError",
							true),
						new JExpression[] {
							 new JStringLiteral(
								ref,
								"This method cannot be "
									+ "called in this context.")}),
					null)
		};
		return new JBlock(ref, body, null);
	}
	
	/**
	 * It analises the body of the method. Actually it is done only
	 * for initializate the body's internal structures.
	 * 
	 * @param context
	 * @throws PositionedError
	 */
	public void analiseBody(CClassContext context) throws PositionedError
	{
		CMethodContext self =
			new CMethodContext(
				context,
				context.getEnvironment(),
				getMethod(),
				asJFormalParameter(getParameters()));

		CBlockContext block =
			new CBlockContext(
				self,
				context.getEnvironment(),
				getParameters().length);

		body.analyse(block);

	}
	
	/**
	 * Return the parameters as <code>JFormalParameter</code>.
	 * The paramenter names will be "param" + count, where count is the 
	 * paramenter counter.
	 * @param parameters
	 * @return
	 */
	protected JFormalParameter[] asJFormalParameter(CType[] parameters)
	{
		TokenReference ref = TokenReference.NO_REF;
		JFormalParameter[] formalParameters =
			new JFormalParameter[parameters.length];

		for (int i = 0; i < parameters.length; i++)
			formalParameters[i] =
				new JFormalParameter(
					ref,
					JFormalParameter.DES_PARAMETER,
					parameters[i],
					"param" + i,
					false);

		return formalParameters;
	}

}
