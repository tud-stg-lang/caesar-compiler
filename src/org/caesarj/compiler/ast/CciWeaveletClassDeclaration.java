package org.caesarj.compiler.ast;

import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorCall;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;
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
			updateConstructors();
			addAccessors();
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
		//TODO: Deal with constructor parameters
		JExpression[] arguments = new JExpression[]
		{
			new JUnqualifiedInstanceCreation(getTokenReference(),
				new CClassNameType(getImplementationTypeName()), 
				new JExpression[0]),
			new JUnqualifiedInstanceCreation(getTokenReference(),
				new CClassNameType(getBindingTypeName()), 
				new JExpression[0]),
		};
		JConstructorCall constructorCall = new FjConstructorCall(
			getTokenReference(), 
			false, 
			arguments);
		
		JConstructorBlock constructorBody = new FjConstructorBlock(
			getTokenReference(), 
			constructorCall, 
			new JStatement[0]);
		
		return new FjConstructorDeclaration(
			getTokenReference(), 
			ACC_PUBLIC, 
			ident, 
			new JFormalParameter[0], 
			new CReferenceType[0], 
			constructorBody, 
			null, 
			null, 
			typeFactory);
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
				getImplementationTypeName()));
		
		addMethod(
			createAccessor(
				CciConstants.BINDING_FIELD_NAME, 
				getBindingTypeName()));
	}
	/**
	 * Creates an accessor method.
	 * @param name the name of the field.
	 * @param typeName the type of the field.
	 * @return
	 */
	protected JMethodDeclaration createAccessor(String name, String typeName)
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
						new CClassNameType(typeName)),
					null)
			};
		JBlock body = new JBlock(getTokenReference(), statements, null);

		return new FjMethodDeclaration(
			getTokenReference(), 
			ACC_PUBLIC, 
			new CTypeVariable[0],
			new CClassNameType(typeName),
			CciConstants.toAccessorMethodName(name),
			new JFormalParameter[0],
			new CReferenceType[0],
			body,
			null, null);
	}

}
