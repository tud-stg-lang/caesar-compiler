package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.ParseClassContext;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CVariableInfo;
import org.caesarj.kjc.CVoidType;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.util.MessageDescription;

/**
 * Class context for binding and implementation classes. This class is 
 * responsable to insert the expected and provided methods.
 * 
 * @author Walter Augusto Werner
 * 
 * ::::::THIS CLASS IS NOT BEING USED ANYMORE:::::
 * 
 */
public class CciClassContext 
	extends FjClassContext
{

	/**
	 * @param parent
	 * @param environment
	 * @param clazz
	 * @param decl
	 */
	public CciClassContext(
		CContext parent,
		KjcEnvironment environment,
		CSourceClass clazz,
		JTypeDeclaration decl)
	{
		super(parent, environment, clazz, decl);
	}

	/**
	 * This method is overriden for insert the methods that should not be 
	 * written by the programmer. The methods are the provided methods of the 
	 * collaboration interfaces for binding classes and expected for 
	 * implementation classes.
	 *  
	 * @see at.dms.kjc.CClassContext#close(at.dms.kjc.JTypeDeclaration, 
	 * at.dms.kjc.CVariableInfo, at.dms.kjc.CVariableInfo, at.dms.kjc.CVariableInfo[])
	 */
	public void close(
		JTypeDeclaration decl,
		CVariableInfo staticC,
		CVariableInfo instanceC,
		CVariableInfo[] constructorsC)
		throws UnpositionedError
	{

		if (! self.isInterface() && 
			! (decl instanceof CciCollaborationInterfaceProxyDeclaration))
		{
			CciSourceClass cciSelf = (CciSourceClass) self;
			
			checkCIMethods(cciSelf.getBinding(), CCI_PROVIDED, 
				CaesarMessages.PROVIDED_METHOD_IN_BINDING);
			checkCIMethods(cciSelf.getImplementation(), CCI_EXPECTED, 
				CaesarMessages.EXPECTED_METHOD_IN_IMPLEMENTATION);

			addUndefinedMethods(cciSelf.getBinding(), CCI_PROVIDED, 
				CciConstants.IMPLEMENTATION_FIELD_NAME);
			addUndefinedMethods(cciSelf.getImplementation(), CCI_EXPECTED, 
				CciConstants.IMPLEMENTATION_FIELD_NAME);


		}
		super.close(decl, staticC, instanceC, constructorsC);
	}

	/**
	 * This method adds the methods to the class, this methods are the methods 
	 * that should not be defined in the class. For example, the provided 
	 * methods should not be defined in the binding classes.
	 * 
	 * @param collaborationInterfaces the collaboration interfaces that the 
	 * class implements or binds.
	 * @param modifier the modifier of the method to insert. I must be provided 
	 * or expected.
	 * @throws UnpositionedError
	 * @throws PositionedError
	 */
	protected void addUndefinedMethods(
		CReferenceType collaborationInterface,
		int modifier,
		String fieldName)
		throws UnpositionedError
	{
		if (collaborationInterface != null)
		{
			CMethod[] methods =
				collaborationInterface.getCClass().getAbstractMethods(this, 
					false);
			for (int i = 0; i < methods.length; i++)
			{
				if (CModifier.contains(methods[i].getModifiers(), modifier)
					&& !isDefined(methods[i]))
				{
					CType[] ciMethodParameters = methods[i].getParameters();
					FjFormalParameter[] parameters = new FjFormalParameter[
						ciMethodParameters.length];
					JExpression[] args = new JExpression[
						ciMethodParameters.length];
				
					for (int j = 0; j < parameters.length; j++)
					{
						String paramName = "param" + j; 
						parameters[j] = new FjFormalParameter(
							TokenReference.NO_REF, 
							JFormalParameter.DES_PARAMETER, 
							ciMethodParameters[j],
							paramName,
							false);
						
						args[j] = 
							new FjLocalVariableExpression(
								TokenReference.NO_REF, 
								parameters[j]);
					}
										
					CciSourceUndefinedMethod method =
						new CciSourceUndefinedMethod(
							self,
							ACC_PUBLIC | modifier,
							methods[i].getIdent(),
							methods[i].getReturnType(),
							methods[i].getParameters(),
							parameters,
							methods[i].getThrowables(),
							methods[i].getTypeVariables(),
							createUndefinedMethodBody(fieldName, 
								methods[i], args));

					//It is for the internal data be initializated in the method.
					try
					{
						method.analiseBody(this);
					}
					catch (PositionedError e)
					{
						// It should never occur.
						e.printStackTrace();
					}

					self.addMethod(method);
				}
			}
		}
	}

	protected JBlock createUndefinedMethodBody(
		String fieldName,
		CMethod method,
		JExpression[] args)
	{
		TokenReference ref = TokenReference.NO_REF;
		JExpression expr = 
			new FjMethodCallExpression(
				ref, 
				new FjFieldAccessExpression(
					ref, 
					new FjThisExpression(ref), 
					fieldName),
				method.getIdent(),
				args);
				
		boolean returns = 
			! (method.getReturnType() instanceof CVoidType);
		
		JStatement[] body =
			new JStatement[] 
			{
				returns
					? (JStatement) 
						new FjReturnStatement(ref, expr, null)
					: (JStatement) 
						new JExpressionStatement(ref, expr, null)
			};
		return new JBlock(ref, body, null);
	}
	
	/**
	 * This method is used to check if the method is defined in the class. 
	 * It can be used when adding methods and the inserted methods are not 
	 * supposed to chenge the result of the result of this method. It is 
	 * because the abstract methods can be found before start to add the 
	 * methods. It is done using the abstract methods because often there 
	 * are less abstract methods than non abstract ones. 
	 * 
	 * @param method the method to look for the implementation.
	 * @param abstractClassMethods the abstract methods of the class.
	 * @return true if the method is already defined, false otherwise.
	 */
	protected boolean isDefined(CMethod method, CMethod[] abstractClassMethods)
	{
		for (int i = 0; i < abstractClassMethods.length; i++)
			if (method.getIdent().equals(abstractClassMethods[i].getIdent())
				&& method.hasSameSignature(abstractClassMethods[i], null))
				return false;
		return true;
	}

	/**
	 * This method is used to check if the method is defined in the class.
	 * With this method the abstract methods are found always it is called. 
	 * 
	 * @param method the method to look for the implementation.
	 * @return true if the method is already defined, false otherwise.
	 */
	protected boolean isDefined(CMethod method) throws UnpositionedError
	{
		return isDefined(method, self.getAbstractMethods(this, false));
	}

	/**
	 * Checks if the method is in the right place. For example,
	 * expected methods must be implemented <b>only</b> in binding classes 
	 * while provided methods in implemention classes.
	 * 
	 * @param context
	 * @param interfaces
	 * @param modifier
	 * @param message
	 * @throws PositionedError
	 */
	protected void checkCIMethods(
		CReferenceType collaborationInterface,
		int modifier,
		MessageDescription message)
		throws UnpositionedError
	{
		if (collaborationInterface != null)
		{
			CMethod[] abstractMethods = self.getAbstractMethods(this, false);

			CMethod[] implementMethods =
				collaborationInterface.getCClass().getMethods();

			for (int j = 0; j < implementMethods.length; j++)
				if (CModifier
					.contains(implementMethods[j].getModifiers(), modifier))
					check(
						! isDefined(implementMethods[j], abstractMethods),
						message,
						implementMethods[j].getIdent());
		}
	}

}
