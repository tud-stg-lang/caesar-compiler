package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;

import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.TypeFactory;

/**
 * AST element for interface declaration.
 * 
 * @author Walter Augusto Werner
 */
public class CciInterfaceDeclaration 
	extends FjInterfaceDeclaration
{

	/** The proxy declaration of this interface. */
	protected CciCollaborationInterfaceProxyDeclaration proxyDeclaration;

	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciInterfaceDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
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
			modifiers,
			ident,
			typeVariables,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}
	/**
	 * @return true if it is a collaboration interface, false otherwise.
	 */
	public boolean isCollaborationInterface()
	{
		return false;
	}
	

//	/**
//	 * Adds the self context methods to the interface. It is done only
//	 * for collaboration interfaces and their inner interfaces. 
//	 * @param factory
//	 */
//	public void addSelfContextMethods(TypeFactory factory)
//	{
//		ArrayList allMethods = new ArrayList(methods.length * 2);
//		
//		allMethods.addAll(Arrays.asList(methods));
//
//		CReferenceType type = factory.createType(ident, false);
//		
//		for (int i = 0; i < methods.length; i++)
//		{
//			if (methods[i] instanceof CciMethodDeclaration)
//				allMethods.add(((CciMethodDeclaration)methods[i])
//					.getSelfContextMethod(type));
//		}
//		methods = (JMethodDeclaration[])allMethods.toArray(
//			new JMethodDeclaration[allMethods.size()]);
//		
//		for (int i = 0; i < inners.length; i++)
//			if (inners[i] instanceof CciInterfaceDeclaration)
//				((CciInterfaceDeclaration)inners[i]).addSelfContextMethods(
//					factory);
//	}
	
//	/**
//	 * Adds the modifier CCI_COLLABORATION and FJC_VIRTUAL to inner interfaces. 
//	 * This is added for interfaces that are defined inside collaboration 
//	 * interfaces. 
//	 */
//	public void initInnersAsCollaboration()
//	{
//		for (int i = 0; i < inners.length; i++)
//		{
//			if (inners[i] instanceof CciInterfaceDeclaration)
//			{
//				inners[i].setModifiers(inners[i].getModifiers() 
//					| CCI_COLLABORATION | FJC_VIRTUAL);
//				((CciInterfaceDeclaration)inners[i]).initInnersAsCollaboration();
//			}
//		}
//	}
	
	/**
	 * Returns the modifiers that are allowed for interfaces.
	 */
	protected int getAllowedModifiers()
	{
		return 	super.getAllowedModifiers()	
			| CCI_COLLABORATION;
	}
	
	/**
	 * Sets its own interfaces and the source class' interfaces.
	 * @param interfaces
	 */
	public void setInterfaces(CReferenceType[] interfaces)
	{
		this.interfaces = interfaces;
		sourceClass.setInterfaces(interfaces);
	}
	
	/**
	 * @return The proxy declaration of this interface. 
	 */
	public CciCollaborationInterfaceProxyDeclaration getProxyDeclaration(
		TypeFactory typeFactory)
	{
		return getProxyDeclaration(null, hasSuper(), typeFactory);
	}
	
	/**
	 * @param ownerName The name of the owner of the interface.
	 * @return The proxy declaration of this interface.
	 */
	public CciCollaborationInterfaceProxyDeclaration getProxyDeclaration(
		String ownerName, boolean ownerHasSuper, TypeFactory typeFactory)
	{
		if (proxyDeclaration == null)
		{
			proxyDeclaration = 
				new CciCollaborationInterfaceProxyDeclaration(
					getTokenReference(), 
					ACC_PUBLIC,
					CciConstants.toCollaborationInterfaceImplName(ident),
					typeVariables,
					createProxySuperType(ownerName),
					new CReferenceType[]{
						new CClassNameType(ident)},
					createProxyFields(ownerName),
					createProxyMethods(),
					new JTypeDeclaration[0],
					ident);
					
		}
		return proxyDeclaration;
	}
	
	public boolean hasSuper()
	{
		return interfaces.length > 0;		
	}

	/**
	 * Creates the super type of the proxy declaration. It will be the proxy
	 * of the super interface.
	 * 
	 * @param ownerName
	 * @return null if it does not have super interfaces, otherwise it returns
	 * the class type of the proxy declaration of the super interface. 
	 */
	protected CReferenceType createProxySuperType(String ownerName)
	{
		return hasSuper()
			? ownerName == null 
				? new CClassNameType(
					CciConstants.toCollaborationInterfaceImplName(
						interfaces[0].toString()))
				: new CClassNameType(ownerName + "$" + 
					CciConstants.toCollaborationInterfaceImplName(
						interfaces[0].toString()))
			: null;
	}
	
	/**
	 * Creates the proxies of the inner classes of this interface.
	 * @param ownerName
	 * @return if it does not have inners it returns an empty array, 
	 * otherwise it returns the proxy declaration of the its nested types.
	 */
	public JTypeDeclaration[] transformInnerInterfaces()
	{
		ArrayList innerProxies = new ArrayList(inners.length);
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof CciInterfaceDeclaration)
				inners[i] =
					((CciInterfaceDeclaration)inners[i])
						.createEmptyVirtualDeclaration(hasSuper());
		}

		return inners;
		
	}
	
	public FjVirtualClassDeclaration createEmptyVirtualDeclaration(
		boolean hasSuper)
	{
		if (hasSuper)
			return 
				new FjOverrideClassDeclaration(
					getTokenReference(), 
					~ACC_ABSTRACT & ~ACC_INTERFACE & modifiers,
					ident,
					typeVariables,
					null,
					CReferenceType.EMPTY,
					null,
					new JFieldDeclaration[0],
					createEmptyMethods(),
					transformInnerInterfaces(),
					new JPhylum[0],
					null,
					null);
		else
			return 
				new FjVirtualClassDeclaration(
					getTokenReference(), 
					~ACC_ABSTRACT & ~ACC_INTERFACE & modifiers,
					ident,
					typeVariables,
					null,
					CReferenceType.EMPTY,
					null,
					new JFieldDeclaration[0],
					createEmptyMethods(),
					transformInnerInterfaces(),
					new JPhylum[0],
					null,
					null);
		
	}
	
	protected JMethodDeclaration[] createEmptyMethods()
	{
		ArrayList returnMethods = new ArrayList(methods.length);
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof CciMethodDeclaration)
				returnMethods.add(
					((CciMethodDeclaration)methods[i]).createEmptyMethod());
		}
		return (JMethodDeclaration[])returnMethods.toArray(
			new JMethodDeclaration[returnMethods.size()]);
	}
	/**
	 * It creates the two fields that are needed in the proxies: implementation
	 * and binding. They are the references which it delegates the execution of
	 * all methods.
	 * @param ownerName
	 * @return An array with the two fields.
	 */
	protected JFieldDeclaration[] createProxyFields(String ownerName)
	{
		
		JFieldDeclaration implementation = createField(ownerName,
			CciConstants.IMPLEMENTATION_FIELD_NAME);
		JFieldDeclaration binding = createField(ownerName,
			CciConstants.BINDING_FIELD_NAME);
		 
		return new JFieldDeclaration[]{implementation, binding};
		
	}
	
	/**
	 * Creates a field that is the reference for the implementation 
	 * or the binding.
	 * @param ownerName
	 * @param fieldName
	 * @return
	 */
	protected JFieldDeclaration createField(String ownerName, String fieldName)
	{
		String typeName = ownerName == null 
			? ident
			: ownerName + "$" + ident;

		return new FjFieldDeclaration(
					getTokenReference(), 
					new FjVariableDefinition(
						getTokenReference(), 
						ACC_PROTECTED, 
						new CClassNameType(typeName), 
						fieldName, 
						null),
					false, 
					null, 
					null);		
	}		
	/**
	 * Creates the method declaration for the proxy.
	 * @return An array with the proxy methods.
	 */
	protected JMethodDeclaration[] createProxyMethods()
	{
		ArrayList proxyMethods = new ArrayList(methods.length);
		
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof CciProvidedMethodDeclaration)
				proxyMethods.add(((CciMethodDeclaration)methods[i])
					.createDelegationMethod(
						CciConstants.IMPLEMENTATION_FIELD_NAME));
			else if (methods[i] instanceof CciExpectedMethodDeclaration)
				proxyMethods.add(((CciMethodDeclaration)methods[i])
					.createDelegationMethod(
						CciConstants.BINDING_FIELD_NAME));
			
		}
		return (JMethodDeclaration[])
			proxyMethods.toArray(new JMethodDeclaration[proxyMethods.size()]);
	}

//	/**
//	 * Sets the super type of the proxy declaration of this interface.
//	 * @param superClassType
//	 */
//	public void setProxyDeclarationSuperType(CReferenceType superClassType)
//	{
//		proxyDeclaration.setSuperClass(superClassType);
//	}
//	
//	
	
//	/**
//	 * Creates a constructor for the proxy. This constructor has two parameters:
//	 * the implementation and the binding that will be woven by the weavelet
//	 * class that will extend the proxy.
//	 * @param ownerName
//	 * @param ownerHasSuper
//	 * @param typeFactory
//	 * @return
//	 */
//	protected JMethodDeclaration createProxyConstructor(String ownerName, 
//		boolean ownerHasSuper, TypeFactory typeFactory)
//	{
//		JFormalParameter[] parameters = new JFormalParameter[2];
//
//		String parameterTypeName = ownerName == null 
//			? ident
//			: ownerName + "$" + ident;
//		
//		//Two parameters: the implementation and the binding
//		parameters[0] = new FjFormalParameter(
//			getTokenReference(),
//			JLocalVariable.DES_PARAMETER,
//			new CClassNameType(parameterTypeName),
//			CciConstants.IMPLEMENTATION_FIELD_NAME,
//			false);
//
//		parameters[1] = new FjFormalParameter(
//			getTokenReference(),
//			JLocalVariable.DES_PARAMETER,
//			new CClassNameType(parameterTypeName),
//			CciConstants.BINDING_FIELD_NAME,
//			false);
//		
//		//Two assigment expressions to set the fields implementation and
//		//binding.
//		JStatement[] statements = new JStatement[]
//		{
//			new JExpressionStatement(
//				getTokenReference(),
//				new FjAssignmentExpression(
//					getTokenReference(), 
//					new FjFieldAccessExpression(
//						getTokenReference(), 
//						new JThisExpression(getTokenReference()),
//						CciConstants.IMPLEMENTATION_FIELD_NAME),
//					new FjLocalVariableExpression(
//						getTokenReference(), 
//						parameters[0])),
//				null),
//			new JExpressionStatement(
//				getTokenReference(),
//				new FjAssignmentExpression(
//					getTokenReference(), 
//					new FjFieldAccessExpression(
//						getTokenReference(), 
//						new JThisExpression(getTokenReference()),
//						CciConstants.BINDING_FIELD_NAME),
//					new FjLocalVariableExpression(
//						getTokenReference(), 
//						parameters[1])),
//				null)
//		};
//		JConstructorCall constructorCall = null;
//		//if it has a super type calls the super constructor.
//		if (ownerHasSuper)
//		{
//			JExpression[] arguments = new JExpression[]
//			{
//				new FjLocalVariableExpression(
//					getTokenReference(), 
//					parameters[0]),
//				new FjLocalVariableExpression(
//					getTokenReference(), 
//					parameters[1]),
//			};
//			constructorCall = new FjConstructorCall(
//				getTokenReference(), 
//				false, 
//				arguments);
//		}
//		
//		JConstructorBlock constructorBody = new FjConstructorBlock(
//			getTokenReference(), 
//			constructorCall, 
//			statements);
//			
//		return new FjConstructorDeclaration(
//			getTokenReference(), 
//			ACC_PUBLIC, 
//			CciConstants.toCollaborationInterfaceImplName(ident), 
//			parameters, 
//			new CReferenceType[0], 
//			constructorBody, 
//			null, 
//			null, 
//			typeFactory);
//	}
	

}
