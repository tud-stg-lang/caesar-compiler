package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;

/**
 * AST element for a standard (neither expected nor provided)
 *  method declaration.
 * 
 * @author Walter Augusto Werner
 */
public class CciStandardMethodDeclaration 
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
	public CciStandardMethodDeclaration(
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
	 * Checks the interface of the method declaration. It checks if:
	 * 
	 * <li> it is not defined in a collaboration interface. </li>
	 * 
	 * @see at.dms.kjc.JMethodDeclaration#checkInterface(at.dms.kjc.CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError
	{
		//Cannot be defined in collaboration interfaces
		check(
			context,
		    ! isInCollaborationInterface(context.getCClass()),
			CaesarMessages.CI_METHOD_FLAGS,
			this.ident);

		return super.checkInterface(context);
	}
	/**
	 * It does not have self context method.
	 * @see org.caesarj.compiler.ast.CciMethodDeclaration#getSelContextMethod(org.caesarj.kjc.CType)
	 */
	public CciMethodDeclaration getSelfContextMethod(CType forwardClassType)
	{
		return null;
	}

}
