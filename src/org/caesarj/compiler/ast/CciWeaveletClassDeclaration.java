package org.caesarj.compiler.ast;

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
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.TypeFactory;

/**
 * The AST element that represents the declaration of weavelet classes. 
 * This classes will extend the proxy of the CI that it extends.
 * @author Walter Augusto Werner
 */
public class CciWeaveletClassDeclaration 
	extends FjClassDeclaration
{
	/** The type factory used to create the constructor. */
	private TypeFactory typeFactory;
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
		JavaStyleComment[] comment,
		TypeFactory typeFactory)
	{
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			new CClassNameType(
				CciConstants.toCollaborationInterfaceImplName(
					superCollaborationInterface.getQualifiedName())),
			interfaces,
			null,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
			
			this.typeFactory = typeFactory;
			this.superCollaborationInterface = superCollaborationInterface;
	}
	
	/**
	 * @return A string that represents the implementation type.
	 */
	public String getImplementationTypeName()
	{
		return superCollaborationInterface.getImplementationQualifiedName();
	}
	/**
	 * @return A string that represents the binding type.
	 */
	public String getBindingTypeName()
	{
		return superCollaborationInterface.getBindingQualifiedName();
	}
	
	/**
	 * Updates the cosntructors for call the super constructors of the proxies.
	 * If it does not have one, it adds a constructor that will call the super
	 * one.
	 */
	protected void updateConstructors()
	{
		boolean hasConstructor = false;
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjConstructorDeclaration)
			{
				((FjConstructorDeclaration)methods[i])
					.updateWeaveletConstructor(this);
				hasConstructor = true;
			}
		}
		
		if (! hasConstructor)
			addMethod(createConstructor());

	}
	
	/**
	 * Creates a constructor without parameters that calls the super type 
	 * constructor. The super constructor has to receive two parameters: the
	 * implementation and the binding, so it creates two fresh objects and 
	 * passes for the super constructor.
	 */
	protected JMethodDeclaration createConstructor()
	{
		TokenReference ref = getTokenReference();
		//TODO: Deal with constructor parameters
		JConstructorBlock constructorBody = new FjConstructorBlock(
			ref, 
			null, 
			new JStatement[0]);
		
		FjConstructorDeclaration constructor = new FjConstructorDeclaration(
			ref, 
			ACC_PUBLIC, 
			ident, 
			new JFormalParameter[0], 
			new CReferenceType[0], 
			constructorBody, 
			null, 
			null, 
			typeFactory);
		
		constructor.updateWeaveletConstructor(this);
		return constructor;
	}
	
	/**
	 * Adds the methods to access the binding and implementation references.
	 *
	 */
	protected void addAccessors()
	{
		addMethod(
			createAccessor(
				CciConstants.IMPLEMENTATION_FIELD_NAME, 
				superCollaborationInterface.getImplementationType()));
		
		addMethod(
			createAccessor(
				CciConstants.BINDING_FIELD_NAME, 
				superCollaborationInterface.getBindingType()));
	}
	/**
	 * Creates an accessor method.
	 * @param name the name of the field.
	 * @param typeName the type of the field.
	 * @return
	 */
	protected JMethodDeclaration createAccessor(String name, 
		CReferenceType returnType)
	{
		JStatement[] statements =
			new JStatement[] 
			{
				new JReturnStatement(
					getTokenReference(),
					new FjCastExpression(
						getTokenReference(), 
						new FjFieldAccessExpression(
							getTokenReference(), 
							new JThisExpression(getTokenReference()), 
							name),
						returnType),
					null)
			};
		JBlock body = new JBlock(getTokenReference(), statements, null);

		return new FjMethodDeclaration(
			getTokenReference(), 
			ACC_PUBLIC, 
			new CTypeVariable[0],
			returnType,
			CciConstants.toAccessorMethodName(name),
			new JFormalParameter[0],
			new CReferenceType[0],
			body,
			null, null);
	}

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.JTypeDeclaration#resolveInterfaces(org.caesarj.kjc.CContext)
	 */
	protected void resolveInterfaces(CContext context) 
		throws PositionedError
	{
		
		if (ownerDecl != null 
			&& ownerDecl instanceof CciWeaveletClassDeclaration)
		{
			CciWeaveletClassDeclaration weavelet = 
				(CciWeaveletClassDeclaration) ownerDecl;
			superCollaborationInterface.setBindingType(
				new CClassNameType(
					weavelet.getBindingTypeName() 
					+ "$" +
					superCollaborationInterface.getBindingQualifiedName()));
			superCollaborationInterface.setImplementationType(
				new CClassNameType(
					weavelet.getImplementationTypeName() 
					+ "$" +
					superCollaborationInterface
						.getImplementationQualifiedName()));

			superCollaborationInterface.setCollaborationInterfaceType(
				new CClassNameType(
					weavelet.getSuperCollaborationInterface().getQualifiedName() 
					+ "$" +
					superCollaborationInterface.getQualifiedName()));
		}
		try
		{
			superCollaborationInterface = 
				(CciWeaveletReferenceType) 
					superCollaborationInterface.checkType(context);
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition(getTokenReference());
		}
		
		super.resolveInterfaces(context);
	}

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.JTypeDeclaration#join(org.caesarj.kjc.CContext)
	 */
	public void join(CContext context) throws PositionedError
	{
		super.join(context);
		addAccessors();
		updateConstructors();
		addCrossReferenceField(getBindingTypeName(), 
			CciConstants.BINDING_FIELD_NAME, false);
		addCrossReferenceField(getImplementationTypeName(), 
			CciConstants.IMPLEMENTATION_FIELD_NAME, false);	
	}
	
	public void addFactoryMethods(
		CciWeaveletClassDeclaration innerClazz, 
		CReferenceType collaborationInterface,
		String fieldName)
	{
		
		CType returnType = new CClassNameType(
			org.caesarj.kjc.Constants.JAV_OBJECT);
		CMethod[] ciMethods = collaborationInterface.getCClass().getMethods();
		boolean factoryCreated = false;
		for (int i = 0; i < ciMethods.length; i++)
		{
			if (ciMethods[i].isConstructor())
			{
				addMethod(
					createDelagationMethod(
						fieldName, 
						FjConstants.factoryMethodName(
							collaborationInterface.getIdent()), 
						ciMethods[i].getParameters(),
						ciMethods[i].getTypeVariables(),
						returnType,
						ciMethods[i].getThrowables()));
				factoryCreated = true;
			}
		}
		if (! factoryCreated)
		{
			addMethod(
				createDelagationMethod(
					fieldName, 
					FjConstants.factoryMethodName(collaborationInterface.getIdent()), 
					new CType[0],
					collaborationInterface.getCClass().getTypeVariables(),
					returnType,
					new CReferenceType[0]));
		}
	}

	/**
	 * @return
	 */
	public CciWeaveletReferenceType getSuperCollaborationInterface()
	{
		return superCollaborationInterface;
	}

	/* (non-Javadoc)
	 * @see org.caesarj.compiler.ast.CciClassDeclaration#constructSourceClass(org.caesarj.kjc.CClass, java.lang.String)
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
