package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CMethodContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;

/**
 * These methods are the methods that are not defined by the prrogrammer.
 * For example, the provided methods in the binding classes and the expected 
 * methods in the implementation classes.
 * 
 * @author Walter Augusto Werner
 */
public class CciSourceUndefinedMethod 
	extends CSourceMethod
{
	/**
	 * The formal parameters.
	 */
	private JFormalParameter[] formalParameters;
	
	/**
	 * 
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param formalParameters
	 * @param exceptions
	 * @param typeVariables
	 * @param body
	 */
	public CciSourceUndefinedMethod(
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		CType[] paramTypes,
		JFormalParameter[] formalParameters,
		CReferenceType[] exceptions,
		CTypeVariable[] typeVariables,
		JBlock body)
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
			body);
			
		this.formalParameters = formalParameters;

	}
	

	
	/**
	 * It analises the body of the method. Actually it is done only
	 * for initializate the body's and parameters' internal structures.
	 * 
	 * @param context
	 * @throws PositionedError
	 */
	public void analiseBody(CClassContext context) 
		throws PositionedError
	{
		CMethodContext self =
			new CMethodContext(
				context,
				context.getEnvironment(),
				getMethod(),
				formalParameters);

		CBlockContext block =
			new CBlockContext(
				self,
				context.getEnvironment(),
				getParameters().length);

		for (int i = 0; i < parameters.length; i++)
		{
			try
			{
				((FjFormalParameter)formalParameters[i]).addFamily(context);
			}
			catch (UnpositionedError e)
			{
				throw e.addPosition(TokenReference.NO_REF);
			}
			formalParameters[i].analyse(block);
		}

		body.analyse(block);

	}
}
