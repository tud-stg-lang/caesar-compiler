package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CStdType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.CVoidType;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFieldAccessExpression;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;

/**
 * AST element for a method declared in the collaboration 
 * interfaces. This class has two known extentions: 
 * <code>CciProvidedMethodDeclaration</code> and
 * <code>CciExpectedMethodDeclaration</code>. 
 * 
 * @author Walter Augusto Werner
 */
public abstract class CciMethodDeclaration 
	extends FjCleanMethodDeclaration
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
	public CciMethodDeclaration(
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
			ident.intern(),
			parameters,
			exceptions,
			body,
			javadoc,
			comments);
	}
	
	/**
	 * This method is used for know if the method is defined 
	 * inside a collaboration interface context. If any the interface
	 * "parent" is a collaboration interface means that the current method
	 * is in a collaboration interface context.
	 * @param clazz
	 * @return true if the method is in a collaboration interface context,
	 *         false otherwise.
	 */
	protected boolean isInCollaborationInterface(CClass clazz)
	{
		int mask = ACC_INTERFACE | CCI_COLLABORATION;
		return clazz != null 
		      && (CModifier.getSubsetOf(clazz.getModifiers(), mask) ==  mask
		          || isInCollaborationInterface(clazz.getOwner()));
	}
	
	/**
	 * @return Is it a provided method?
	 */
	public boolean isProvided()
	{
		return false;
	}
	
	/**
	 * @return Is it an expected method?
	 */
	public boolean isExpected()
	{
		return false;
	}
	
	/**
	 * Creates the source method representation of this method.
	 */
	protected FjSourceMethod createSourceMethod(
		CSourceMethod oldExport,
		FjFamily[] families)
	{
		return new CciSourceMethod(
			oldExport.getOwner(),
			oldExport.getModifiers(),
			oldExport.getIdent().intern(),
			oldExport.getReturnType(),
			oldExport.getParameters(),
			oldExport.getThrowables(),
			oldExport.getTypeVariables(),
			oldExport.isDeprecated(),
			oldExport.isSynthetic(),
			body,
			families);
	}
	
	/**
	 * It creates a method implementation that delegates the execution for the
	 * field that is passed as parameter. It is used just for methods defined
	 * in collaboration interfaces and their nested interfaces.
	 * @param fieldNameToDelegate The name of the field that it shall delegate.   
	 */
	public JMethodDeclaration createMethodImplementation(
		String fieldNameToDelegate)
	{
		//New parameters to the new method definition
		JFormalParameter[] ownParameters =
			new JFormalParameter[parameters == null 
				? 0 
				: parameters.length];
		
		//my own parameters: the paramters have to be cloned!
		FjFormalParameter[] myParameters = getParameters();
		for (int i = 0; i < ownParameters.length; i++) 
			ownParameters[i] = (FjFormalParameter) myParameters[i].clone();
			
		//Creates the expressions that are the arguments for the method call
		JExpression[] arguments = new JExpression[ownParameters.length];

		//The others are accesses to the received parameters.
		for (int i = 0; i < ownParameters.length; i++)
		{ 
			arguments[i] =
				new FjNameExpression(
					getTokenReference(),
					ownParameters[i].getIdent());
		}
		//A field (implementation or binding) access is the prefix of the 
		//method call.
		JFieldAccessExpression prefix = 
			new JFieldAccessExpression(
				getTokenReference(), 
				fieldNameToDelegate);

		JMethodCallExpression methodCall =
			new JMethodCallExpression(
				getTokenReference(),
				prefix,
				ident,
				arguments);
				
		//If the return type is other than void it must return the 
		//result of the method call. 
		JStatement[] statements = new JStatement[]
		{
			returnType instanceof CVoidType
				? (JStatement) new JExpressionStatement(
					getTokenReference(),
					methodCall,
					null)
				: (JStatement) new JReturnStatement(
					getTokenReference(),
					methodCall,
					null)
		};

		JBlock body =
				new JBlock(
					getTokenReference(),
					statements,
					null);

		return new FjMethodDeclaration(
			getTokenReference(),
			~ACC_ABSTRACT & modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			exceptions,
			body,
			null,
			null);
	}
	
	/**
	 * It creates a self context method of this method.
	 */
	public abstract CciMethodDeclaration getSelfContextMethod(
		CType forwardClassType);

	/**
	 * Creates the parameters for the self context methods. 
	 */
	protected JFormalParameter[] createParametersToSelfContextMethod(
		CType forwardClassType)
	{
		JFormalParameter[] newParameters = new JFormalParameter[
			parameters == null 
				? 1
				: parameters.length + 1];
				
		FjFormalParameter[] myParameters = getParameters();
		
		
		newParameters[0] = new FjFormalParameter(
			getTokenReference(),
			JLocalVariable.DES_PARAMETER,
			CStdType.Object,
			FjConstants.SELF_NAME,
			true);
		
		for (int i = 0; i < myParameters.length; i++)
			newParameters[i + 1] = (JFormalParameter)myParameters[i].clone(); 
			
		return newParameters;
	}
}
