package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;

/**
 * AST element for a provided method declaration.
 * Created by the parser when it finds a method defined
 * with the modifier <b>provided</b>, and the method is
 * <b>public</b>, and it is <i>not</i> <b>static</b>.
 * 
 * An provided method can be defined only in collaboration
 * interfaces.
 * 
 * @author Walter Augusto Werner
 */
public class CciProvidedMethodDeclaration 
	extends CciMethodDeclaration
{

	/**
	 * @param where
	 * @param modifiers
	 * @param typeVariables
	 * @param returnType
	 * @param ident
	 * @param parameters
	 * @param exceptions
	 * @param body
	 * @param javadoc
	 * @param comments
	 */
	public CciProvidedMethodDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JavadocComment javadoc,
		JavaStyleComment[] comments)
	{
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			exceptions,
			body,
			javadoc,
			comments);
	}

	/**
	 * Checks the interface of a method declaration.
	 * It checks if:
	 * <li> the method is defined with the right modifiers: provided, 
	 * public and abstract. </li>
	 * <li> the method is defined in a collaboration interface.</li>
	 *  
	 * @see at.dms.kjc.JMethodDeclaration#checkInterface(at.dms.kjc.CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError
	{
		//Provided methods must be defined as public, abstract and expected
		check(
			context,
			CModifier.isSubsetOf(modifiers, ACC_PUBLIC 
										  | ACC_ABSTRACT
										  | CCI_PROVIDED),
			CaesarMessages.PROVIDED_METHOD_FLAGS,
			this.ident);

		//Provided method must be defined in collaboration interfaces
		check(
			context,
			isInCollaborationInterface(context.getCClass()),
			CaesarMessages.PROVIDED_METHOD_OUT_CI,
			this.ident);
			
		return super.checkInterface(context);
	}
	
	public boolean isProvided()
	{
		return true;
	}
	
	/**
	* Returns the self context method of this method. Since it is a provided
	* method, the self context method  will also be a provided method.
	*/
	public CciMethodDeclaration getSelfContextMethod(CType forwardClassType)
	{
		return new CciProvidedMethodDeclaration(
			getTokenReference(), 
			modifiers, 
			typeVariables, 
			returnType, 
			FjConstants.selfContextMethodName(ident), 
			createParametersToSelfContextMethod(forwardClassType), 
			exceptions, 
			null, 
			null, 
			null);
	}
}
