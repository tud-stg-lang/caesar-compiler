package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CVariableInfo;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.util.MessageDescription;

/**
 * Class context for binding and implementation classes. This class is 
 * responsable to insert the expected and provided methods.
 * 
 * @author Walter Augusto Werner
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
			
			checkCIMethods(cciSelf.getBindings(), CCI_PROVIDED, 
				CaesarMessages.PROVIDED_METHOD_IN_BINDING);
			checkCIMethods(cciSelf.getImplementations(), CCI_EXPECTED, 
				CaesarMessages.EXPECTED_METHOD_IN_IMPLEMENTATION);

			addUncallableMethods(cciSelf.getBindings(), CCI_PROVIDED);
			addUncallableMethods(cciSelf.getImplementations(), CCI_EXPECTED);

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
	protected void addUncallableMethods(
		CReferenceType[] collaborationInterfaces,
		int modifier)
		throws UnpositionedError
	{
		//CMethod[] methods = getCClass().getAbstractMethods(this, false);

		for (int i = 0; i < collaborationInterfaces.length; i++)
		{
			CMethod[] methods =
				collaborationInterfaces[i].getCClass().getAbstractMethods(this, 
					false);
			for (int j = 0; j < methods.length; j++)
			{
				if (CModifier.contains(methods[j].getModifiers(), modifier)
					&& !isDefined(methods[j]))
				{
					CciSourceUncallableMethod method =
						new CciSourceUncallableMethod(
							getTypeFactory(),
							self,
							ACC_PUBLIC | modifier,
							methods[j].getIdent(),
							methods[j].getReturnType(),
							methods[j].getParameters(),
							methods[j].getThrowables(),
							methods[j].getTypeVariables());

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
		CReferenceType[] collaborationInterfaces,
		int modifier,
		MessageDescription message)
		throws UnpositionedError
	{
		CMethod[] abstractMethods = self.getAbstractMethods(this, false);
		for (int i = 0; i < collaborationInterfaces.length; i++)
		{
			CMethod[] implementMethods =
				collaborationInterfaces[i].getCClass().getMethods();

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
