package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.CVoidType;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.MessageDescription;

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
	 * The CIs that the class implements.
	 */
	protected CReferenceType implementation;

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
		CReferenceType[] interfaces,
		CReferenceType binding,
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
		
		this.binding = binding;
	}

	/**
	 * Initializes the <code>implementation</coed> reference with the CI 
	 * implemented by the class, removing this interface from the 
	 * <code>interfaces</code> array. It checks if the class is implementing 
	 * more than one CI.
	 */
	protected void initImplemetation(CContext context)
		throws PositionedError
		
	{
		ArrayList tempInterfaces = new ArrayList(
			Arrays.asList(interfaces));
		ArrayList tempImplementations = new ArrayList();
		for (int i = 0, j = 0; i < interfaces.length; i++)
		{
			if (CModifier.contains(interfaces[i].getCClass().getModifiers(),
					CCI_COLLABORATION))
			{
				tempImplementations.add(interfaces[i]);
				tempInterfaces.remove(interfaces[i]);				
			}
		}
		
		check(context, ! (tempImplementations.size() > 1), 
			CaesarMessages.CLASS_IMPLEMENTS_N_CIS, ident);
		
		if (tempImplementations.size() > 0)
		{
			implementation = (CReferenceType) tempImplementations.get(0);
			
			interfaces = (CReferenceType[]) tempInterfaces.toArray(
				new CReferenceType[tempInterfaces.size()]);
		}

	}


	/**
	 * This method resolves the interface that is passed as parameter 
	 * and performs the checks. 
	 * It performs all checks that are done for standard java interfaces
	 * plus the check if it is a CI, if the last parameter is 
	 * true.
	 *  
	 * @param context
	 * @param interfaceType the interface type to resolve.
	 * @param checkCollaboration if it is true it checks if the interface is 
	 * a collaboration interface, otherwise it does not.
	 * @throws PositionedError
	 */
	protected CReferenceType resolveInterface(CContext context, String ciName, 
		CType interfaceType, boolean checkCollaboration) 
		throws PositionedError
	{
		CReferenceType type;
		try
		{
			type = (CReferenceType) interfaceType.checkType(self);
		}
		catch (UnpositionedError e)
		{
			if (ciName != null)
			{
				try
				{
					type = (CReferenceType) new CClassNameType(
						ciName + "$" + interfaceType).checkType(context);
				}
				catch (UnpositionedError e1)
				{
					throw e.addPosition(getTokenReference());
				}
			}
			else
				throw e.addPosition(getTokenReference());
		}

		CClass clazz = type.getCClass();

		check(
			context,
			clazz.isInterface(),
			KjcMessages.SUPERINTERFACE_WRONG_TYPE,
			type.getQualifiedName());

		check(
			context,
			clazz.isAccessible(sourceClass),
			KjcMessages.SUPERINTERFACE_NOT_ACCESSIBLE,
			type.getQualifiedName());
		check(
			context,
			!(sourceClass.getQualifiedName() == JAV_OBJECT),
			KjcMessages.CIRCULAR_INTERFACE,
			type.getQualifiedName());

		if (checkCollaboration)
		{
			check(
				context,
				CModifier.contains(clazz.getModifiers(), CCI_COLLABORATION),
				CaesarMessages.NON_CI,
				type.getQualifiedName());
		}
		return type;
	}
	
	/**
	 * Overriden for resolve the binding and insert on it the name of 
	 * the outer CI where it is defined. Beyond that, the implementation
	 * of the inner classes must be added. The implementation of the inner 
	 * CIs is not defined by the programmer with the implements clause. It 
	 * is done using the same name as the name used for the inner interface 
	 * of the CI. So we have to add these implementations before resolve them.
	 */
	protected void resolveInterfaces(CContext context)
		throws PositionedError	
	{
		//First try to find the name of the owner CI, if there is one.
		String ciName = null;
		CClass owner = getOwner();
		
		if (owner != null && owner instanceof CciClass)
		{
			// By now, there is always at most one binding or implementation
			CReferenceType[] allInterfaces = owner.getInterfaces();
			CciClass ownerClass = ((CciClass)owner);
		
			//First try the bindings			
			CReferenceType ownerCi = ownerClass.getBinding();
			if (ownerCi != null)
				ciName = ownerCi.getQualifiedName();
			else
			{
				//Now the implementations, adding the implementation interfaces
				//to the inners.
				ownerCi = ownerClass.getImplementation();
				if (ownerCi != null)
				{
					ciName = ownerCi.getQualifiedName();
					addImplementation(ownerCi);
				}
				else
				{
					//The owner can be an interface, so here we look for the
					// CI in the interfaces of the owner.
					CReferenceType[] ownerCis = owner.getInterfaces();
					if (ownerCis != null && ownerCis.length > 0)
					{
						CReferenceType ci = null;
						for (int i = 0; i < ownerCis.length; i++)
						{
							if (CModifier.contains(ownerCis[i].getCClass()
								.getModifiers(), CCI_COLLABORATION))
							{
								ciName = ownerCis[i].getQualifiedName();
								ci = ownerCis[i];
								break;
							}
						}
						
						//Here just add the interface if there is one.
						if (ci != null)
							addImplementation(ci);
					}
				}
			}
		}
		
		//Resolve the interfaces
		for (int i = 0; i < interfaces.length; i++)
		{
			interfaces[i] = resolveInterface(
				context, ciName, interfaces[i], false);
			
		}
		
		//After resolving the interfaces initializes the implementations
		initImplemetation(context);
		
		//Resolves the binding
		if (binding != null)
		{
			//The binding must be a CI, so the last parameter is true.
			//And it is added the name of the owner CI on it.
			binding = resolveInterface(
				context,
				ciName, 
				ciName != null 
					? new CClassNameType(ciName + "$" + binding)
					: binding, 
				true);
			
			addCrossReferenceField(binding.getQualifiedName(), 
				CciConstants.IMPLEMENTATION_FIELD_NAME);
			
			addSettingMethod(
				binding.getQualifiedName(),
				CciConstants.IMPLEMENTATION_FIELD_NAME, 
				context.getTypeFactory());
		}
		else if (implementation != null)
		{
			addCrossReferenceField(implementation.getQualifiedName(), 
				CciConstants.BINDING_FIELD_NAME);

			addSettingMethod(
				implementation.getQualifiedName(),
				CciConstants.BINDING_FIELD_NAME, 
				context.getTypeFactory());

		}
	
		sourceClass.setInterfaces(getAllInterfaces());

	}
	
	/**
	 * Adds a field that is the reference for the implementation 
	 * or the binding.
	 * @param ownerName
	 * @param fieldName
	 * @return
	 */
	protected void addCrossReferenceField(String typeName, 
		String fieldName)
	{
		JFieldDeclaration[] newFields = 
			new JFieldDeclaration[fields.length + 1];
		System.arraycopy(fields, 0, newFields, 0, fields.length);
		
		newFields[fields.length] = new FjFieldDeclaration(
					getTokenReference(), 
					new FjVariableDefinition(
						getTokenReference(), 
						ACC_PRIVATE, 
						new CClassNameType(typeName), 
						fieldName, 
						null),
					false, 
					null, 
					null);

		fields = newFields;
	}
	public JMethodDeclaration createSettingMethod(String ciType, 
		String fieldName, TypeFactory typeFactory)
	{
		TokenReference ref = getTokenReference();
		
		JExpression[] args = new JExpression[]
		{
			new FjFieldAccessExpression(ref, 
				new FjThisExpression(ref), 
				fieldName)
		};
		JStatement assigmentExpression =				
			new JExpressionStatement(ref,
				new FjAssignmentExpression(ref, 
					new FjFieldAccessExpression(ref, 
						new FjThisExpression(ref), 
						fieldName),
					new FjNameExpression(ref, fieldName)),
				null);
		JStatement[] statements;
		
		if (hasSuperClass())
		{
			statements = 
				new JStatement[] 
				{
					assigmentExpression,
					new JExpressionStatement(ref,
						new FjMethodCallExpression(ref, 
							new FjSuperExpression(ref), 
							CciConstants.toSettingMethodName(fieldName),
							args),
						null)
				};
		}
		else
			statements = new JStatement[]{assigmentExpression};
				
		JBlock body = new JBlock(getTokenReference(), statements, null);
		
		JFormalParameter[] parameters = new JFormalParameter[]
		{
			new FjFormalParameter(ref, 
				JFormalParameter.DES_PARAMETER, 
				new CClassNameType(ciType), 
				fieldName,
				false)
		};

		return
			new FjMethodDeclaration(
				ref, 
				ACC_PUBLIC, 
				new CTypeVariable[0],
				typeFactory.getVoidType(),
				CciConstants.toSettingMethodName(fieldName),
				parameters,
				new CReferenceType[0],
				body,
				null, null);
		
	}
	/**
	 * Adds the methods to access the binding and implementation references.
	 *
	 */
	protected void addSettingMethod(String ciType, 
		String fieldName, TypeFactory typeFactory)
	{
		addMethod(createSettingMethod(ciType, fieldName, typeFactory));
	}
	public boolean hasSuperClass()
	{
		return getSuperClass() != null
			&& ! (getSuperClass().getQualifiedName().equals(
					FjConstants.CHILD_IMPL_TYPE_NAME))
			&& ! (getSuperClass().getQualifiedName().equals(
					Constants.JAV_OBJECT));
	}
	
//	/**
//	 * Updates the cosntructors for call the super constructors of the proxies.
//	 * If it does not have one, it adds a constructor that will call the super
//	 * one.
//	 */
//	protected void updateConstructors(
//		String collaborationInterface, 
//		String fieldName,
//		TypeFactory typefactory)
//	{
//		boolean hasConstructor = false;
//		for (int i = 0; i < methods.length; i++)
//		{
//			if (methods[i] instanceof FjConstructorDeclaration)
//			{
//				((FjConstructorDeclaration)methods[i])
//					.updateConstructor(
//						collaborationInterface, 
//						fieldName);
//				hasConstructor = true;
//			}
//		}
//		
//		if (! hasConstructor)
//		{
//			addMethod(
//				createConstructor(
//					collaborationInterface, 
//					fieldName, 
//					typefactory));
//		}
//
//	}
//	
//	/**
//	 * Creates a constructor without parameters that calls the super type 
//	 * constructor. The super constructor has to receive two parameters: the
//	 * implementation and the binding, so it creates two fresh objects and 
//	 * passes for the super constructor.
//	 */
//	protected JMethodDeclaration createConstructor(
//		String collaborationInterface, 
//		String fieldName,
//		TypeFactory typeFactory)
//	{
//		JFormalParameter[] parameters = 
//			new JFormalParameter[]
//			{
//				new FjFormalParameter(
//					getTokenReference(), 
//					JFormalParameter.DES_PARAMETER, 
//					new CClassNameType(collaborationInterface), 
//					fieldName, 
//					false)
//			};
//		
//		// ver se tem super, dae insira um coinstructor call.
//		
//		JStatement[] statements = new JStatement[]
//		{
//			new JExpressionStatement(
//				getTokenReference(),
//				new FjAssignmentExpression(
//					getTokenReference(), 
//					new FjFieldAccessExpression(
//						getTokenReference(), 
//						new FjThisExpression(getTokenReference()), 
//						fieldName), 
//					new FjNameExpression(
//						getTokenReference(), 
//						fieldName)),
//				null)			
//		};
//		
//		FjConstructorBlock body = 
//			new FjConstructorBlock(getTokenReference(), null, statements);
//		
//		return new FjConstructorDeclaration(
//			getTokenReference(), 
//			ACC_PUBLIC, 
//			ident, 
//			parameters, 
//			new CReferenceType[0], 
//			body, 
//			null, 
//			null, 
//			typeFactory);
//	}	
	/**
	 * This method is used to insert the collaboration interaface when the 
	 * class is nested and shall implement the collaboration interface that 
	 * is nested on the super collaboration interface. The class shall 
	 * implement it if it has the same name of a nested interface of the 
	 * collaboration interface of its owner. 
	 * 
	 * @param ownerCi
	 */
	protected void addImplementation(CReferenceType ownerCi)
	{
		FjTypeSystem typeSystem = new FjTypeSystem();
		CReferenceType[] ownerInners = ownerCi.getCClass().getInnerClasses();					
		for (int i = 0; i < interfaces.length; i++)
		{
			String interfaceIdent = interfaces[i].getQualifiedName();
			interfaceIdent = interfaceIdent.substring(
				interfaceIdent.lastIndexOf("$") + 1);
			
			CReferenceType ownerInnerType = typeSystem.declaresInner(
				ownerCi.getCClass(), interfaceIdent);
			if (ownerInnerType != null)
			{
				CReferenceType[] newInterfaces = 
					new CReferenceType[interfaces.length + 1];
					
				System.arraycopy(interfaces, 0, newInterfaces, 
					0, interfaces.length);
					
				newInterfaces[interfaces.length] = 
					new CClassNameType(ownerInnerType.getQualifiedName());
					
				interfaces = newInterfaces;
				
				//It will override the super virtual type.
				modifiers = modifiers | FJC_OVERRIDE;
				sourceClass.setModifiers(modifiers);
				return;
			}
		}
	}
	
	/**
	 * @return the Collaboration Interface which it binds.
	 */
	public CReferenceType getBinding()
	{
		return binding;
	}

	/**
	 * @return the Collaboration Interface which it implements.
	 */
	public CReferenceType getImplementation()
	{
		return implementation;
	}
	/**
	 * Sets the implementation of the class
	 * @param implementations
	 */
	public void setImplementation(CReferenceType implementation)
	{
		this.implementation = implementation;
	}
	
	/**
	 * @return all interfaces including the CIs.
	 */
	public CReferenceType[] getAllInterfaces()
	{
		CReferenceType binding = getBinding();
		CReferenceType implementation = getImplementation();
		
		CReferenceType[] allInterfaces = 
			new CReferenceType[
				interfaces.length 
				+ (binding != null ? 1 : (implementation != null ? 1 : 0))];
				
		System.arraycopy(interfaces, 0, allInterfaces, 0, interfaces.length);
		if (binding != null)
			allInterfaces[interfaces.length] = binding;
		else if (implementation != null)
			allInterfaces[interfaces.length] = implementation;

		return allInterfaces;
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
	 * Construct the source class.
	 * @param owner
	 * @param prefix
	 * @return
	 */
	protected CSourceClass constructSourceClass(CClass owner, String prefix)
	{
		return new CciSourceClass(
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
	 * @return the super class of the class.
	 */
	public CReferenceType getSuperClass()
	{
		return superClass;
	}	
	
	/**
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
	 * This method was pulled up from FjClassDeclaration.
	 * @param type
	 * @author Walter Augusto Werner
	 */
	protected void setInners(JTypeDeclaration[] inners)
	{
		this.inners = inners;
	}

	/**
	 * Checks if the class implements or binds all nested 
	 * interfaces from the CI and if the methods are in the right place. The 
	 * expected methods cannot be defined into a implementation class as 
	 * provided in binding classes. After checking, it inserts the methods
	 * that are not defined in the class: for example the provided methods in 
	 * the binding classes. The implementation of these methods is just a
	 * delegation for the object whose is set on the delegation field of the
	 * class, for example: the field _implementation in the binding classes.
	 * 
	 * 
	 * @see at.dms.kjc.JTypeDeclaration#checkTypeBody(at.dms.kjc.CContext)
	 */
	public void checkTypeBody(CContext context) 
		throws PositionedError
	{
		if (binding != null)
		{
			checkInnerTypeImplementation(context, 
				binding.getCClass().getInnerClasses(), 
				binding, 
				CaesarMessages.NESTED_TYPE_NOT_BOUND);
			
			checkCIMethods(context, binding, CCI_PROVIDED, 
				CaesarMessages.PROVIDED_METHOD_IN_BINDING);
							
			addUndefinedMethods(constructContext(context), binding, 
				CCI_PROVIDED, CciConstants.IMPLEMENTATION_FIELD_NAME);
			
		}
		else if (implementation != null)
		{
			checkInnerTypeImplementation(context, 
				implementation.getCClass().getInnerClasses(), 
				implementation, 
				CaesarMessages.NESTED_TYPE_NOT_IMPLEMENTED);

			checkCIMethods(context, implementation, CCI_EXPECTED, 
				CaesarMessages.EXPECTED_METHOD_IN_IMPLEMENTATION);
				
			addUndefinedMethods(constructContext(context), implementation, 
				CCI_EXPECTED, CciConstants.BINDING_FIELD_NAME);
		}
		
		super.checkTypeBody(context);
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
		CContext context,
		CReferenceType collaborationInterface,
		int modifier,
		MessageDescription message)
		throws PositionedError
	{
		ArrayList ciMethods = 
			getCiMethods(
				collaborationInterface.getCClass(), 
				modifier);

		for (int i = 0; i < ciMethods.size(); i++)
		{
			CMethod ciMethod = (CMethod) ciMethods.get(i);
			check(context,
				! isDefined(ciMethod),
				message,
				ciMethod.getIdent());
		}

	}

	/**
	 * This method is used to check if the method is defined in the class.
	 * 
	 * @param method the method which one wants to know if there is an
	 * implementation.
	 * @return true if the method is already defined, false otherwise.
	 */
	protected boolean isDefined(CMethod method)
	{
		for (int i = 0; i < methods.length; i++)
		{
			CMethod definedMethod = methods[i].getMethod();
			if (method.getIdent().equals(definedMethod.getIdent())
				&& method.hasSameSignature(definedMethod, null))
				return true;
		}
		return false;
	}
	
	/**
	 * Adds the methods that cannot be defined by the programer 
	 * in the source code. For example, the provided methods on binding classes.
	 * The body of the method is just a method call for the reference that is
	 * set as its delegation reference.
	 * 
	 * @param context
	 * @param collaborationInterface
	 * @param methodModifier
	 * @param fieldName
	 * @throws PositionedError
	 */
	protected void addUndefinedMethods(CContext context,
		CReferenceType collaborationInterface, 
		int methodModifier, String fieldName) 
		throws PositionedError
	{
		TokenReference ref = getTokenReference();
		//The methods that must be defined now.
		CMethod[] ciMethods = (CMethod[]) getCiMethods(
			collaborationInterface.getCClass(), 
			methodModifier).toArray(new CMethod[0]);
		
		// We have to store both the method itself and its source reference	
		ArrayList newMethods = new ArrayList(ciMethods.length);
		ArrayList sourceMethods = new ArrayList(ciMethods.length);
		//Creates the methods
		for (int i = 0; i < ciMethods.length; i++)
		{
			FjCleanMethodDeclaration method =
				createDelagationMethod(fieldName, ciMethods[i]);
					
			// Now we have to initializate the internal structure of the method,
			// as well as creates their sources.
			FjClassContext classContext = (FjClassContext) 
				context.getClassContext();
			classContext.pushContextInfo(this);
			if (isCleanClass())
			{
				//For clean classes we have to create the self context methods
				FjMethodDeclaration[] cleanMethods = 
					method.getSelfContextMethods(getCClass().getAbstractType());
				for (int j = 0; j < cleanMethods.length; j++)
				{
					newMethods.add(cleanMethods[j]);
					cleanMethods[j].checkInterface(classContext);
					sourceMethods.add(cleanMethods[j].initFamilies(
						classContext));
				}
			}
			else
			{
				newMethods.add(method);
				method.checkInterface(classContext);
				sourceMethods.add(method.initFamilies(classContext));
			}
			classContext.popContextInfo();
		}
		
		addMethods((JMethodDeclaration[]) 
			newMethods.toArray(new JMethodDeclaration[newMethods.size()]));
		
		sourceMethods.addAll(Arrays.asList(sourceClass.getMethods()));
		
		((CciSourceClass)sourceClass).setMethods(
			(CMethod[])
				sourceMethods.toArray(
					new CMethod[sourceMethods.size()]));
	}
	
	public FjCleanMethodDeclaration createDelagationMethod(
		String fieldName,
		CMethod method)
	{
		return 
			createDelagationMethod(
				fieldName, 
				method.getIdent(), 
				method.getParameters(),
				method.getTypeVariables(),
				method.getReturnType(), 
				method.getThrowables());
	}

	public FjCleanMethodDeclaration createDelagationMethod(
		String fieldName,
		String methodName,
		CType[] parameterTypes,
		CTypeVariable[] typeVariables,
		CType returnType,
		CReferenceType[] exceptions)
	{
		TokenReference ref = getTokenReference();
		// The parameters and the arguments are created in the same round.
		FjFormalParameter[] parameters = new FjFormalParameter[
			parameterTypes.length];
		JExpression[] args = new JExpression[
			parameterTypes.length];
		
		for (int j = 0; j < parameters.length; j++)
		{
			String parameterName = ("param" + j).intern();
			//Creates the parameter...
			parameters[j] = new FjFormalParameter(
				ref, 
				JFormalParameter.DES_PARAMETER, 
				parameterTypes[j],
				parameterName,
				false);
			//Creates the argument...
			args[j] = new FjNameExpression(ref, parameterName);
		}
		
		return
			new FjCleanMethodDeclaration(
				ref, 
				ACC_PUBLIC, 
				typeVariables, 
				returnType, 
				methodName, 
				parameters, 
				exceptions, 
				createUndefinedMethodBody(fieldName, 
					methodName, args, ! (returnType instanceof CVoidType)), 
				null, 
				null);
		
	}
	
	/**
	 * @return Is it a clean class?
	 */
	public boolean isCleanClass()
	{
		return CModifier.contains(modifiers, FJC_CLEAN)
				| CModifier.contains(modifiers, FJC_VIRTUAL)
				| CModifier.contains(modifiers, FJC_OVERRIDE);
	}
	
	/**
	 * Returns the methods that are defined in the CI with the passed modifier.
	 * For example, we could want all provided methods defined in the CI, 
	 * including those methods defined in super CIs.
	 * 
	 * @param collaborationInterface
	 * @param modifier
	 * @return
	 */
	protected ArrayList getCiMethods(CClass collaborationInterface, 
		int modifier)
	{
		CMethod[] ciMethods = collaborationInterface.getMethods();
		ArrayList methodsToReturn = new ArrayList(ciMethods.length);
		for (int i = 0; i < ciMethods.length; i++)
			if (CModifier.contains(ciMethods[i].getModifiers(), modifier))
				methodsToReturn.add(ciMethods[i]);
		CReferenceType[] interfaces = collaborationInterface.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			CClass superType = interfaces[i].getCClass();
			if (CModifier.contains(superType.getModifiers(), CCI_COLLABORATION))
				methodsToReturn.addAll(getCiMethods(superType, modifier));
		}
		return methodsToReturn;
	}

	/**
	 * Returns a method body for the methods that are not defined by 
	 * the programmers. This body is just a method call for the method with 
	 * the same name as the passed method for the field also passed as 
	 * parameter.
	 * 
	 * @param fieldName
	 * @param method
	 * @param args
	 * @return
	 */
	protected JBlock createUndefinedMethodBody(
		String fieldName,
		String methodName,
		JExpression[] args,
		boolean returns)
	{
		TokenReference ref = getTokenReference();
		JExpression expr = 
			new FjMethodCallExpression(
				ref, 
				new FjFieldAccessExpression(
					ref, 
					new FjThisExpression(ref), 
					fieldName),
				methodName,
				args);
				
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
	
	protected void addMethods(JMethodDeclaration[] newMethods)
	{
		JMethodDeclaration[] tempMethods = new JMethodDeclaration[
			methods.length + newMethods.length];
			
		System.arraycopy(methods, 0, tempMethods, 0, methods.length);
		System.arraycopy(newMethods, 0, tempMethods, methods.length, 
			newMethods.length);
		
		methods = tempMethods;
	}

	/**
	 * This method checks if the class implements or binds all nested 
	 * types from the CI.
	 * 
	 * @param context The valid context.
	 * @param ciInnerClasses The nested classes from the CI.
	 * @param ci The CI that the class implements or binds.
	 * @param errorMessage The error message that must be shown if any of the 
	 * 	nested classes are not implemented or bound.
	 * @throws PositionedError
	 */
	protected void checkInnerTypeImplementation(
		CContext context,
		CReferenceType[] ciInnerClasses,
		CReferenceType ci,
		MessageDescription errorMessage)
		throws PositionedError
	{
		if (ciInnerClasses != null && ! FjConstants.isIfcImplName(ident))
		{
			for (int i = 0; i < ciInnerClasses.length; i++)
			{
				boolean found = false;
				for (int k = 0; k < inners.length; k++)
				{
					if (inners[k].getCClass().descendsFrom(ciInnerClasses[i].getCClass()))
					{
						found = true;
						break;
					}
				}
				check(context, found, errorMessage, 
					ciInnerClasses[i].getIdent(), ci.getIdent());
			}
		}
	}

	/**
	 * Pulled up!
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
	public void checkInterface(CContext context) throws PositionedError
	{
		super.checkInterface(context);
		sourceClass.setInterfaces(getAllInterfaces());
	}

	/* (non-Javadoc)
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
		
		if (implementation != null)
		{
			System.out.print(" implementsCI ");

			System.out.print(implementation);
		}
			
		
		if (binding != null)
		{
			System.out.print(" binds ");
			System.out.print(binding);
		}
		
		System.out.println();
	}

}
