package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

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
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JBinaryExpression;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JCastExpression;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JEqualityExpression;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFieldAccessExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JIfStatement;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.JVariableDefinition;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.util.Utils;

/**
 * AST element for a class declaration.
 * Created by the parser when it finds a class that 
 * implements or binds an interface.
 * 
 * 
 * @author Walter Augusto Werner
 */
public class CciClassDeclaration 
	extends JClassDeclaration
{
	/** 
	 * The CI that the class binds.
	 */
	protected CReferenceType binding;
	/** 
	 * The CIs that the class provides.
	 */
	protected CReferenceType providing;
	
	/**
	 * The owner reference. It was pulled up.
	 */
	protected FjClassDeclaration ownerDecl;
	
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
	public CciClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType binding,
		CReferenceType providing,
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
			superClass,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
			
		this.providing = providing;
		this.binding = binding;
	}

	/**
	 * Does it have super class?
	 * @return boolean
	 */
	public boolean hasSuperClass()
	{
		return getSuperClass() != null
			&& ! (getSuperClass().getQualifiedName().equals(
					FjConstants.CHILD_IMPL_TYPE_NAME))
			&& ! (getSuperClass().getQualifiedName().equals(
					Constants.JAV_OBJECT));
	}

	
	/**
	 * @return CReferenceType the Collaboration Interface which it binds.
	 */
	public CReferenceType getBinding()
	{
		return binding;
	}

	/**
	 * @return CReferenceType the Collaboration Interface which it implements.
	 */
	public CReferenceType getProviding()
	{
		return providing;
	}
	
	/**
	 * @return CReferenceType the super class of the class.
	 */
	public CReferenceType getSuperClass()
	{
		return superClass;
	}	

	/**
	 * Returns the InnerClasses. This method was pulled up. 
	 * @return JTypeDeclaration[]
	 */
	public JTypeDeclaration[] getInners()
	{
		return inners;
	}

	/**
	 * Returns all constructors. This method was pulled up. 
	 * @return FjConstructorDeclaration[]
	 */
	protected FjConstructorDeclaration[] getConstructors()
	{
		Vector contructors = new Vector(methods.length);
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjConstructorDeclaration)
				contructors.add(methods[i]);
		}
		return (FjConstructorDeclaration[]) Utils.toArray(
			contructors,
			FjConstructorDeclaration.class);
	}

	/**
	 * Returns the qualified type name of the binding.
	 * @return String
	 */
	public String getBindingTypeName()
	{
		return ownerDecl != null 
				? ownerDecl.getBindingTypeName() + "$" + binding.toString()
				: binding.toString();
	}

	/**
	 * Sets the owner declaration. This method was pulled up.
	 * @param ownerDecl
	 */
	public void setOwnerDeclaration(Object ownerDecl)
	{
		if (ownerDecl instanceof FjClassDeclaration)
			this.ownerDecl = (FjClassDeclaration) ownerDecl;
	}
	
	/**
	 * Returns the owner declaration. This method was pulled up.
	 * @return FjClassDeclaration
	 */
	public FjClassDeclaration getOwnerDeclaration()
	{
		return ownerDecl;
	}
	/**
	 * Returns the ident of the class
	 * @return String
	 */
	public String getIdent()
	{
		return ident;
	}
	
	
	/**
	 * @return An int with all modifiers allowed for classes.
	 */	
	protected int getAllowedModifiers()
	{
		return ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE | 
			ACC_ABSTRACT | ACC_STATIC | ACC_FINAL | ACC_STRICT;
	}

	/**
	 * Sets the inner classes.
	 * This method was pulled up from FjClassDeclaration.
	 * @param type
	 */
	protected void setInners(JTypeDeclaration[] inners)
	{
		this.inners = inners;
	}
	
	/**
	 * Construct the source class.
	 * @param owner
	 * @param prefix
	 * @return CSourceClass
	 */
	protected CSourceClass constructSourceClass(CClass owner, String prefix)
	{
		return new FjSourceClass(
			owner, 
			getTokenReference(), 
			modifiers, 
			ident, 
			prefix + ident, 
			typeVariables, 
			isDeprecated(), 
			false, 
			this);
	}
	
	
	/**
	 * Appends an inner class.
	 * This method was pulled up from FjClassDeclaration.
	 * @param type
	 */
	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners = new JTypeDeclaration[inners.length + 1];
		
		System.arraycopy(inners, 0, newInners, 0, inners.length);

		newInners[inners.length] = type;
		setInners(newInners);
	}


	/**
	 * Adds a method to the class. This method was pulled up. 
	 * @param newMethod
	 */
	public void addMethod(JMethodDeclaration newMethod)
	{
		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[methods.length + 1];
	
		System.arraycopy(methods, 0, newMethods, 0, methods.length);
	
		newMethods[methods.length] = newMethod;
	
		methods = newMethods;
	}

	/**
	 * Adds a field in the class.
	 * @param newField field to be inserted
	 */
	public void addField(JFieldDeclaration newField)
	{
		JFieldDeclaration[] newFields =
			new JFieldDeclaration[fields.length + 1];
	
		System.arraycopy(fields, 0, newFields, 0, fields.length);
	
		newFields[fields.length] = newField;
	
		fields = newFields;
	}

	/**
	 * Adds fields in the class.
	 * @param newFields fields to be inserted
	 */
	public void addFields(ArrayList newFields)
	{
		List tempList = Arrays.asList(fields);
		ArrayList oldFields = new ArrayList(tempList.size() + newFields.size());
		
		oldFields.addAll(newFields);
		fields = 
			(JFieldDeclaration[]) 
				oldFields.toArray(new JFieldDeclaration[oldFields.size()]);
	}
	public void addMethods(ArrayList methodsToAdd)
	{
		addMethods(
			(JMethodDeclaration[])
				methodsToAdd.toArray(
					new JMethodDeclaration[methodsToAdd.size()]));

	}

	public void addMethods(JMethodDeclaration[] methodsToAdd)
	{
		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[methods.length + methodsToAdd.length];

		System.arraycopy(methods, 0, newMethods, 0, methods.length);
		System.arraycopy(
			methodsToAdd,
			0,
			newMethods,
			methods.length,
			methodsToAdd.length);

		methods = newMethods;
	}

		
	/**
	 * Resolves the binding and providing references. Of course it calls the
	 * super implementation of the method also.
	 */
	public void checkInterface(CContext context) 
		throws PositionedError
	{
		super.checkInterface(context);
		if (binding != null)
			binding = resolveCollabortationInterface(context, binding);

		if (providing != null)
			providing = resolveCollabortationInterface(context, providing);
	}
	/**
	 * Resolves the collaboration interface passed as parameter.
	 * Returns the ci checked.
	 * @param context
	 * @param ci
	 * @return CReferenceType 
	 * @throws PositionedError
	 */
	protected CReferenceType resolveCollabortationInterface(
		CContext context, CReferenceType ci)
		throws PositionedError		
	{
		try
		{
			ci = (CReferenceType) ci.checkType(context);
		}
		catch (UnpositionedError e)
		{
			if (e.getFormattedMessage().getDescription()
				!= KjcMessages.CLASS_AMBIGUOUS)
				throw e.addPosition(getTokenReference());
					
			CClass[] candidates = (CClass[]) 
				e.getFormattedMessage().getParams()[1];
				
			ci = candidates[0].getAbstractType();
		}
		return ci;	
	}
	
	/**
	 * Transforms the inner classes in overriden types. The current class
	 * must be a providing class (getProviding() != null).
	 * @return JTypeDeclaration[] the new nested classes.
	 */
	public JTypeDeclaration[] transformInnerProvidingClasses()
	{
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof FjClassDeclaration)
			{
				inners[i] = 
					((FjClassDeclaration)inners[i])
						.createOverrideClassDeclaration(this);
			}
		}
		return inners;
	}
	
	/**
	 * Sets the right name of the inner classes whose bind some ci. 
	 * 
	 * @param superOwner
	 */
	public void transformInnerBindingClasses(String superOwner)
	{
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof FjClassDeclaration)
			{
				FjClassDeclaration innerClass = (FjClassDeclaration)inners[i];
				if (innerClass.getBinding() != null)
				{
					innerClass.setModifiers(
						innerClass.getModifiers() | CCI_BINDING);
					String superClassName = superOwner + "$" + 
						innerClass.getBindingTypeName();
					innerClass.setSuperClass(
						new CClassNameType(superClassName));
					innerClass.addProvidingAcessor();
				}
				innerClass.transformInnerBindingClasses(superOwner);
			}
		}
	}		
	/**
	 * Creates an override type. This is done when the compiler finds a 
	 * providing class, so it has to change its inners for an overriding classe.
	 * @param owner
	 * @return FjOverrideClassDeclaration
	 */
	public FjOverrideClassDeclaration createOverrideClassDeclaration(
		CciClassDeclaration owner)
	{
		providing = new CClassNameType(owner.getProviding().getQualifiedName() 
			+ "$" + ident);
		return 
			new FjOverrideClassDeclaration(
				getTokenReference(),
				modifiers | CCI_PROVIDING,
				ident,
				typeVariables,
				null,
				null,
				providing,
				interfaces,
				fields,
				methods,
				transformInnerProvidingClasses(),
				this.body,
				null,
				null);
	}

	/**
	 * Adds the providing reference accessor. The class must be a binding class.
	 * The method will actually return a dispatcher of self in this context.
	 */
	public void addProvidingAcessor()
	{
		TokenReference ref = getTokenReference();
		//Adds the implementation accessor.
		addMethod(
			createAccessor(
				CciConstants.PROVIDING_REFERENCE_NAME,
				new JMethodCallExpression(
					ref,
					new JThisExpression(ref),
					FjConstants.GET_DISPATCHER_METHOD_NAME,
					new JExpression[]
					{
						new FjNameExpression(
								ref,
								FjConstants.SELF_NAME)
					}),
				FjConstants.CHILD_TYPE));	
	}
	

	/**
	 * Creates an accessor method.
	 * @param accessedName The name to be accessed
	 * @param returnExpression The return expression
	 * @param returnType The return type
	 * @return FjCleanMethodDeclaration
	 */
	protected FjCleanMethodDeclaration createAccessor(
		String accessedName, 
		JExpression returnExpression, 
		CReferenceType returnType)
	{
		JStatement[] statements =
			new JStatement[] {
				 new JReturnStatement(
					getTokenReference(),
					returnExpression,
					null)};
					
		JBlock body = new JBlock(getTokenReference(), statements, null);
	
		return new FjCleanMethodDeclaration(
			getTokenReference(),
			ACC_PUBLIC,
			new CTypeVariable[0],
			returnType,
			CciConstants.toAccessorMethodName(accessedName),
			new JFormalParameter[0],
			new CReferenceType[0],
			body,
			null,
			null);
	}
	public void addWrapperRecyclingStructure()
	{
		ArrayList wrappingMappings = new ArrayList(inners.length);
		ArrayList wrappingMethods = new ArrayList(inners.length);
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof CciClassDeclaration
				&& CModifier.contains(inners[i].getModifiers(), CCI_BINDING))
			{
				CciClassDeclaration innerDecl = (CciClassDeclaration) inners[i];
				wrappingMappings.add(
					createWrapperMapping(innerDecl));
				wrappingMethods.add(
					createWrapperRecyclingMethod(innerDecl));
			}
		}
		
		if (wrappingMappings.size() > 0)
			addFields(wrappingMappings);
		if (wrappingMethods.size() > 0)
			addMethods(wrappingMethods);
		
	}

	/**
	 * Creates a field declaration which will contain all 
	 * instances of the wrappers of the type passed.
	 * 
	 * @param binding inner type that will be contained in the map.
	 */
	protected JFieldDeclaration createWrapperMapping(
		CciClassDeclaration binding)
	{
		TokenReference ref = getTokenReference();
		String mapName = 
			CciConstants.toWrappingMapName(binding.getIdent());
		return
			new FjFieldDeclaration(
				ref, 
				new FjVariableDefinition(
					ref, 
					ACC_PRIVATE,
					CciConstants.WRAPPING_MAP_TYPE,
					mapName,
					new CciInternalUnqualifiedInstanceCreation(
						ref, 
						CciConstants.WRAPPING_MAP_TYPE, 
						JExpression.EMPTY)),
				false,
				CciConstants.WRAPPING_MAP_JAVADOC,
				new JavaStyleComment[0]);
	}

	/**
	 * 
	 */
	protected JMethodDeclaration createWrapperRecyclingMethod(
		CciClassDeclaration binding)
	{
		TokenReference ref = getTokenReference();
		String methodName = 
			CciConstants.toWrappingMethodCreationName(binding.getIdent());

		JStatement[] statements = new JStatement[]
		{
			//LocalType wrapper = (LocalType) map.get(_getObjectId(wrappee))
			new JVariableDeclarationStatement(
				ref,
				new JVariableDefinition(
					ref, 
					0, 
					new CClassNameType(binding.getIdent()), 
					CciConstants.WRAPPING_LOCAL_VAR,
					new JCastExpression(
						ref, 
						new FjMethodCallExpression(
							ref, 
							new JFieldAccessExpression(
								ref, 
								new JThisExpression(ref), 
								CciConstants.toWrappingMapName(binding.getIdent())),
							CciConstants.WRAPPING_MAP_ACCESS,
							new JExpression[]
							{
		
								new JMethodCallExpression(
									ref, 
									new JThisExpression(ref), 
									CciConstants.WRAPPEE_ID_METHOD_NAME, 
									new JExpression[]
									{
										new FjNameExpression(
											ref, 
											null, 
											CciConstants.WRAPPEE_PARAMETER_NAME)
									})
							}),
					new CClassNameType(binding.getIdent()))),
				null),

			//if (wrapper == null)
			//{
			//	wrapper = new Binding(wrappee)
			//	map.put(getObjectId(wrappee), wrapper)
			//}
			new JIfStatement(
				ref, 
				new JEqualityExpression(
					ref, 
					true, 
					new FjNameExpression(
						ref,
						CciConstants.WRAPPING_LOCAL_VAR), 
						new JNullLiteral(ref)),
				new JBlock(
					ref, 
					new JStatement[]
					{
						//wrapper = new Binding(wrappee)
						new JExpressionStatement(
							ref,
							new FjAssignmentExpression(
								ref, 
								new FjNameExpression(
									ref, 
									CciConstants.WRAPPING_LOCAL_VAR), 
								new FjUnqualifiedInstanceCreation(
									ref, 
									new CClassNameType(binding.getIdent()), 
									new JExpression[]
									{
										new FjNameExpression(
											ref, 
											CciConstants.WRAPPEE_PARAMETER_NAME)
									})),
							null),
						//map.put(getObjectId(wrappee), wrapper)
						new JExpressionStatement(
							ref,
							new FjMethodCallExpression(
								ref,
								new JFieldAccessExpression(
									ref, 
									new JThisExpression(ref), 
									CciConstants.toWrappingMapName(
										binding.getIdent())),
								CciConstants.WRAPPING_MAP_PUT,
								new JExpression[]
								{
									new JMethodCallExpression(
										ref, 
										new JThisExpression(ref), 
										CciConstants.WRAPPEE_ID_METHOD_NAME, 
										new JExpression[]
										{
											new FjNameExpression(
												ref, 
												null, 
												CciConstants
													.WRAPPEE_PARAMETER_NAME)
										}),
									new FjNameExpression(
										ref, 
										CciConstants.WRAPPING_LOCAL_VAR)						
								}),
							null),
					},
					null),
				null,
				null),//endIf
			
			//return wrapper
			new JReturnStatement(
				ref,
				new FjNameExpression(ref, CciConstants.WRAPPING_LOCAL_VAR),
				null)
		};
		
		JBlock methodBody = new JBlock(
			ref,
			statements,
			null);
			
		FjFormalParameter[] parameters = new FjFormalParameter[]
		{
			new FjFormalParameter(
				ref, 
				JLocalVariable.DES_PARAMETER, 
				new CClassNameType(JAV_OBJECT), 
				CciConstants.WRAPPEE_PARAMETER_NAME, 
				false)
		};
		
		return 
			new FjMethodDeclaration(
				ref, 
				ACC_PUBLIC, 
				typeVariables, 
				new CClassNameType(binding.getIdent()),
				methodName,
				parameters,
				CReferenceType.EMPTY,
				methodBody,
				CciConstants.WRAPPING_CREATION_JAVADOC,
				new JavaStyleComment[0]);
	}

	
	/* DEBUG
	 * (non-Javadoc)
	 * @see at.dms.kjc.JTypeDeclaration#print()
	 */
	public void print()
	{
		System.out.print(CModifier.toString(modifiers));
		System.out.print("class ");
		super.print();
		if (superClass != null)
			System.out.print(" extends " + superClass );
		if (interfaces.length > 0)
		{
			System.out.print(" implements ");
			for (int i = 0; i < interfaces.length; i++)
			{
				if (i > 0)
					System.out.print(", ");
					
				System.out.print(interfaces[i]);
			}
		}
		
		if (providing != null)
		{
			System.out.print(" provides ");

			System.out.print(providing);
		}
			
		
		if (binding != null)
		{
			System.out.print(" binds ");
			System.out.print(binding);
		}
		
		System.out.println();
	}
	
}
