package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;

/**
 * The AST element that represents the declaration of weavelet classes. 
 * This classes will extend the proxy of the CI that it extends.
 * @author Walter Augusto Werner
 */
public class CciWeaveletClassDeclaration 
	extends FjCleanClassDeclaration
{
	/**
	 * The reference of the binding class.
	 */
	private CReferenceType bindingImplementation;
	/**
	 * The reference of the providing class.
	 */
	private CReferenceType providingImplementation;

	/** 
	 * The collaboration interface with the reference for the implementation
	 * and the binding types.
	 */
	private CciWeaveletReferenceType superCollaborationInterface;

	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param superClass
	 * @param interfaces
	 * @param bindings
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciWeaveletClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CciWeaveletReferenceType superCollaborationInterface,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment)
	{
		super(
			where,
			modifiers | CCI_WEAVELET,
			ident,
			typeVariables,
			null,
			null,
			null,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);

		this.superCollaborationInterface = superCollaborationInterface;
	}

	/**
	 * @return A string that represents the providing type.
	 */
	public String getProvidingTypeName()
	{
		return superCollaborationInterface.getProvidingQualifiedName();
	}
	/**
	 * @return A string that represents the binding type.
	 */
	public String getBindingTypeName()
	{
		return superCollaborationInterface.getBindingQualifiedName();
	}
	/**
	 * Returns the reference of the super collaboration interface.
	 * @return CciWeaveletReferenceType
	 */
	public CciWeaveletReferenceType getSuperCollaborationInterface()
	{
		return superCollaborationInterface;
	}

	/**
	 * Resolves the superCollaborationInterface reference.
	 *
	 */
	public void resolveInterfaces(CContext context) throws PositionedError
	{
		try
		{
			superCollaborationInterface =
				(
					CciWeaveletReferenceType) superCollaborationInterface
						.checkType(
					context);

			bindingImplementation =
				(CReferenceType) new CClassNameType(
					FjConstants.toImplName(
						superCollaborationInterface
							.getBindingQualifiedName()))
							.checkType(
					context);

			providingImplementation =
				(CReferenceType) new CClassNameType(
					FjConstants.toImplName(
						superCollaborationInterface
							.getProvidingQualifiedName()))
							.checkType(
					context);
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition(getTokenReference());
		}

		super.resolveInterfaces(context);
	}

	/**
	 * Adds the methods to access the binding and providing references.
	 * The method's bodies
	 * _getBinding:
	 *      return (<BindingType>)_getParent();
	 * _getProviding:
	 * 		return (<ProvidingType>)_getProvidingReference();
	 */
	public void addAccessors()
	{
		TokenReference ref = getTokenReference();
		//Adds the binding accessor
		CReferenceType bindingType = 
			superCollaborationInterface.getBindingType();
		addMethod(
			createAccessor(
				CciConstants.BINDING_NAME,
				new FjCastExpression(
					ref,
					new FjFieldAccessExpression(
						ref,
						FjConstants.PARENT_NAME),
					bindingType),
				bindingType));

		//Adds the providing accessor
		CReferenceType providingType = 
			superCollaborationInterface.getProvidingType();
		addMethod(
			createAccessor(
				CciConstants.PROVIDING_NAME,
				new FjCastExpression(
					ref,
					new JMethodCallExpression(
							ref, 
							new JThisExpression(ref),
							CciConstants.toAccessorMethodName(
								CciConstants.PROVIDING_REFERENCE_NAME),
							JExpression.EMPTY),
					providingType),
				providingType));				
		


	}
	/**
	 * Checks if the weavelet extends a Collaboration Interface, and if the 
	 * binding reference really binds this collaboration interface, and 
	 * the providing provides the same collaboration interface as well.
	 */
	public void checkInterface(CContext context) 
		throws PositionedError
	{
		String superCiIdent = 
			superCollaborationInterface.getCClass().getIdent();
		
		//Is the super ci a collaboration interface?	
		check(context, 
			CModifier.contains(
				superCollaborationInterface.getCClass().getModifiers(), 
				CCI_COLLABORATION),
				CaesarMessages.NON_CI,
				superCiIdent);
		
		//Check if it is weaving binding and providing classes.
		CClass binding = getSuperCollaborationInterfaceClass(
			superCollaborationInterface.getBindingType().getCClass(), 
			CCI_BINDING);
			
		CClass providing = getSuperCollaborationInterfaceClass(
			superCollaborationInterface.getProvidingType().getCClass(), 
			CCI_PROVIDING);
		
		//Does the binding reference bind something?
		check(context, 
			binding != null, 
			CaesarMessages.REFERENCE_IS_NOT_BINDING,
			getBindingTypeName());
			
		//Does the providing reference provide something?
		check(context, 
			providing != null, 
			CaesarMessages.REFERENCE_IS_NOT_PROVIDING,
			getProvidingTypeName());
		
		//Do they use the same ci?  		
		//And do they use the same CI as 
		//that one defined at the extends clause?
		check(context, 
			providing.getIdent().equals(superCiIdent), 
			CaesarMessages.PROVIDING_OTHER_CI,
			getProvidingTypeName(), 
			superCiIdent);
		
		check(context, 
			binding.getIdent().equals(superCiIdent), 
			CaesarMessages.BINDING_OTHER_CI,
			getBindingTypeName(),
			superCiIdent);		
		
		super.checkInterface(context);
	}
	
	/**
	 * Generates a new argument expression for generate the constructors.
	 * The expression will be:
	 * 
	 * new <BindingType>(new <ProvidingType>(new <CIType>), ..)
	 * or new <BindingType>(new <ProvidingType>(_parent), ..)
	 */
	protected void setSuperConstructorArgument(
		FjConstructorDeclaration constructor,
		JExpression superArg)
	{
		FjConstructorCall constructorCall = 
			((FjConstructorBlock) constructor.getBody()).getConstructorCall();
		
		if (constructorCall == null || ! constructorCall.isThis())
		{
			JExpression[] oldArgs;
			if (constructorCall == null)
				oldArgs = JExpression.EMPTY;
			else
				oldArgs = constructorCall.getArguments();
			JExpression[] args = new JExpression[oldArgs.length + 1];
			System.arraycopy(oldArgs, 0, args, 1, oldArgs.length);
	
			args[0] = 
				new JUnqualifiedInstanceCreation(
					FjConstants.STD_TOKEN_REFERENCE,
					new CClassNameType(
						FjConstants.toImplName(
							superCollaborationInterface
								.getProvidingQualifiedName())),
					new JExpression[]{superArg});
					
			superArg =
				new JUnqualifiedInstanceCreation(
					FjConstants.STD_TOKEN_REFERENCE,
					new CClassNameType(
						FjConstants.toImplName(
							superCollaborationInterface
								.getBindingQualifiedName())),
					args);
		}


		super.setSuperConstructorArgument(constructor, superArg);
	}
	
	/**
	 * The standard base class constructors are different for weavelets.
	 * It does not call the constructor defined in the same class, but instead
	 * it calls the super constructor, building all the delegation chain 
	 * for allow the execution of the components. 
	 *
	 */
	protected FjConstructorDeclaration createStandardBaseClassConstructor(
		FjConstructorDeclaration constructor,
		CReferenceType superType)
	{
		return constructor.getStandardWeaveletClassConstructor(
			superCollaborationInterface);
	}
	
	/**
	 * Constructs the source class which represents the weavelet.
	 *
	 */
	protected CSourceClass constructSourceClass(CClass owner, String prefix)
	{
		return new CciWeaveletSourceClass(
			owner,
			getTokenReference(),
			modifiers,
			ident,
			prefix + ident,
			superCollaborationInterface,
			typeVariables,
			isDeprecated(),
			false,
			this);
	}

}
