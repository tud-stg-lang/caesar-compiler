package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.CaesarMessages;
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
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.Utils;

public class FjCleanClassDeclaration 
	extends FjClassDeclaration
{
	
	public FjCleanClassDeclaration(
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
			binding,
			providing,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}

	public void checkInterface(CContext context) throws PositionedError
	{
		// clean classes may not ...
		
		// ... be abstract
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0,
			CaesarMessages.CLEAN_ABSTRACT_CLASS);

		// ... be inners
		check(
			context,
			(modifiers & FJC_CLEAN) == 0 || getOwner() == null,
			CaesarMessages.CLEAN_CLASS_NO_INNER);

		super.checkInterface(context);

		// ... define package access or protected methods
		for (int i = 0; i < methods.length; i++)
		{
			check(
				context,
				CModifier.contains(
					methods[i].getMethod().getModifiers(),
					ACC_PUBLIC | ACC_PRIVATE),
				CaesarMessages.METHOD_VISIBILITY_IN_CLEAN_CLASS,
				getMethods()[i].getMethod().getIdent());
		}

		// ... define fields with visibility other than private
		for (int i = 0; i < fields.length; i++)
		{
			check(
				context,
				(fields[i].getField().getModifiers() & ACC_PRIVATE) != 0,
				CaesarMessages.CLEAN_JUST_PRIVATE_FIELDS,
				fields[i].getField().getIdent());
		}

		// ... define non-clean inners
		for (int i = 0; i < inners.length; i++)
		{
			check(
				context,
				CModifier.contains(
					inners[i].getModifiers(),
					FJC_CLEAN | FJC_VIRTUAL | FJC_OVERRIDE),
				CaesarMessages.CLEAN_NO_NON_CLEAN_MEMBER,
				inners[i].getCClass().getIdent());
		}
	}

	public void join(CContext context) throws PositionedError
	{
		checkCleanSuperclass(context);
		super.join(context);
	}
	
	protected void checkCleanSuperclass(CContext context)
		throws PositionedError
	{
		CReferenceType superClass;
		try
		{
			superClass = (CReferenceType) getSuperClass().checkType(context);
		}
		catch (UnpositionedError e1)
		{
			try
			{
				String unModifiedName =
					FjConstants.toIfcName(getSuperClass().toString());
				new CClassNameType(unModifiedName).checkType(context);
				// if the unmodified typename is ok, then
				// we are inheriting a non clean class
				if (CModifier.contains(modifiers, CCI_COLLABORATION))
					throw new UnpositionedError(CaesarMessages.NON_CI, 
							getSuperClass().getQualifiedName())
								.addPosition(getTokenReference());
				else
					throw new UnpositionedError(
						CaesarMessages.CLEAN_INHERITS_NON_CLEAN,
						unModifiedName).addPosition(
						getTokenReference());
			}
			catch (UnpositionedError e2)
			{
				// if the unmodified typename isn't ok, too ...
				throw e2.addPosition(getTokenReference());
			}
		}
		//Collaboration interfaces extend only other cis..
		if (CModifier.contains(modifiers, CCI_COLLABORATION)
			&& hasSuperClass()
			&& ! CModifier.contains(superClass.getCClass().getModifiers(), 
				CCI_COLLABORATION))
			throw new UnpositionedError(CaesarMessages.NON_CI, 
					getSuperClass().getQualifiedName())
						.addPosition(getTokenReference());
				
	}

	private TypeFactory typeFactory;
	public void setTypeFactory(TypeFactory typeFactory)
	{
		this.typeFactory = typeFactory;
	}
	public TypeFactory getTypeFactory()
	{
		return typeFactory;
	}

	protected void createAccessorsForPrivateMethods()
	{
		Vector privateMethodAccessors = new Vector();
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjPrivateMethodDeclaration)
			{
				FjPrivateMethodDeclaration method =
					(FjPrivateMethodDeclaration) methods[i];
				method = method.getAccessorMethod(this);
				if (method != null)
					privateMethodAccessors.add(method);
			}
		}
		for (int i = 0; i < privateMethodAccessors.size(); i++)
		{
			append((JMethodDeclaration) privateMethodAccessors.elementAt(i));
		}
	}

	private FjCleanClassInterfaceDeclaration cleanInterface;
	public FjCleanClassInterfaceDeclaration getCleanInterface()
	{
		return cleanInterface;
	}
	public FjCleanClassInterfaceDeclaration createCleanInterface(Object owner)
	{

		//createAccessorsForPrivateMethods();

		FjClassDeclaration ownerDecl =
			(owner instanceof FjClassDeclaration)
				? (FjClassDeclaration) owner
				: null;

		if (cleanInterface == null)
		{
			cleanInterface = newInterfaceDeclaration(
				getTokenReference(),
				FjConstants.cleanInterfaceName( ident ),
				interfaces,
				getCleanMethods()
			);

			if (getSuperClass() != null)
			{
				CClassNameType superIfcType =
					new CClassNameType(
						FjConstants.cleanInterfaceName(
							getSuperClass().getQualifiedName()));
				cleanInterface.addInterface(superIfcType);
			}
		}
		return cleanInterface;
	}

	private FjCleanClassIfcImplDeclaration cleanInterfaceImplementation;
	public FjCleanClassIfcImplDeclaration getCleanInterfaceImplementation()
	{
		return cleanInterfaceImplementation;
	}
	public FjCleanClassIfcImplDeclaration createCleanInterfaceImplementation(Object owner)
	{

		FjClassDeclaration ownerDecl =
			(owner instanceof FjClassDeclaration)
				? (FjClassDeclaration) owner
				: null;

		if (cleanInterfaceImplementation == null)
		{
			cleanInterfaceImplementation =
				newIfcImplDeclaration(
					getTokenReference(),
					FjConstants.cleanInterfaceImplementationName(ident),
					new CReferenceType[] {
						new CClassNameType(
							FjConstants.cleanInterfaceName(ident))},
					getCleanMethods());

			if (getSuperClass() == null)
			{
				CClassNameType superImplType =
					new CClassNameType(FjConstants.CHILD_IMPL_TYPE_NAME);
				cleanInterfaceImplementation.setSuperClass(superImplType);
			}
			else
			{
				CClassNameType superIfcType =
					new CClassNameType(
						FjConstants.cleanInterfaceName(
							getSuperClass().getQualifiedName()));
				cleanInterfaceImplementation.setSuperIfc(superIfcType);
				CClassNameType superImplType =
					new CClassNameType(
						FjConstants.cleanInterfaceImplementationName(
							getSuperClass().getQualifiedName()));
				cleanInterfaceImplementation.setSuperClass(superImplType);
			}

			cleanInterfaceImplementation.addChildsConstructor(typeFactory);
		}
		return cleanInterfaceImplementation;
	}
	protected FjCleanClassInterfaceDeclaration newInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{

		return new FjCleanClassInterfaceDeclaration(
			getTokenReference(),
			ident,
			modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET),
			interfaces,
			methods,
			this);
	}
	protected FjCleanClassIfcImplDeclaration newIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{

		return new FjCleanClassIfcImplDeclaration(
			getTokenReference(),
			ident,
			(modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET)) | ACC_PUBLIC,
			interfaces,
			methods,
			this);
	}

	private FjCleanMethodDeclaration[] cleanMethods;
	public FjCleanMethodDeclaration[] getCleanMethods()
	{
		if (cleanMethods == null)
		{
			int numberOfcleanMethods = 0;
			// count the number of clean methods
			for (int i = 0; i < methods.length; i++)
			{
				if (methods[i] instanceof FjCleanMethodDeclaration)
					numberOfcleanMethods++;
			}
			cleanMethods = new FjCleanMethodDeclaration[numberOfcleanMethods];
			// select the clean methods
			for (int i = 0, j = 0; i < methods.length; i++)
			{
				if (methods[i] instanceof FjCleanMethodDeclaration)
				{
					cleanMethods[j] = (FjCleanMethodDeclaration) methods[i];
					j++;
				}
			}
			// include factory-methods of inner virtual types
			for (int i = 0; i < inners.length; i++)
			{
				if (inners[i] instanceof FjVirtualClassDeclaration)
				{
					((FjVirtualClassDeclaration) inners[i]).setTypeFactory(
						typeFactory);
					cleanMethods =
						append(
							cleanMethods,
							((FjVirtualClassDeclaration) inners[i])
								.getFactoryMethods());
				}
			}
		}
		return cleanMethods;
	}
	private FjCleanMethodDeclaration[] append(
		FjCleanMethodDeclaration[] left,
		FjCleanMethodDeclaration[] right)
	{

		FjCleanMethodDeclaration[] result =
			new FjCleanMethodDeclaration[left.length + right.length];
		int i = 0;
		for (int j = 0; j < left.length; j++)
		{
			result[i] = left[j];
			i++;
		}
		for (int j = 0; j < right.length; j++)
		{
			result[i] = right[j];
			i++;
		}
		return result;
	}

	public FjCleanClassDeclaration getBaseClass()
	{
		return this;
	}

	public void append(JTypeDeclaration type)
	{
		getCleanInterface().append(type);
	}

	public void addInterface(CReferenceType ifc)
	{
		Vector interfaces = new Vector(Arrays.asList(this.interfaces));
		interfaces.add(ifc);
		this.interfaces =
			(CReferenceType[]) Utils.toArray(interfaces, CReferenceType.class);
	}

	public void addSelfContextToCleanMethods(CReferenceType selfType)
	{
		Vector methodVector = new Vector(this.methods.length * 3);

		for (int i = 0; i < this.methods.length; i++)
		{
			if (this.methods[i] instanceof FjMethodDeclaration)
			{
				FjMethodDeclaration[] transformedMethods =
					((FjMethodDeclaration) this.methods[i])
							.getSelfContextMethods(selfType);
				for (int j = 0; j < transformedMethods.length; j++)
				{
					methodVector.add(transformedMethods[j]);
				}
			}
			else
			{
				methodVector.add(this.methods[i]);
			}
		}

		this.methods =
			(JMethodDeclaration[]) toArray(methodVector,
				JMethodDeclaration.class);
	}
	
	protected CReferenceType getSuperConstructorType()
	{
		String superTypeName = getSuperClass().getQualifiedName();
		if (superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
			return null;
		return 
			new CClassNameType(FjConstants.toIfcName(superTypeName));
	}
	protected JExpression getSuperConstructorArgumentExpression()
	{
		String superTypeName = getSuperClass().getQualifiedName();
		if (superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
			return new JNullLiteral(FjConstants.STD_TOKEN_REFERENCE);
		
		return
			new FjNameExpression(
				FjConstants.STD_TOKEN_REFERENCE,
				FjConstants.PARENT_NAME);
	}

	public void addSuperTypeParameterToConstructors()
	{

		//String superTypeName = getSuperClass().getQualifiedName();
		CReferenceType superType = getSuperConstructorType();
		JExpression superArg = getSuperConstructorArgumentExpression();
		CClass owner = getOwner();

		//If it has a super ci, it must 
		//define one more constructor (Only for the most outer classes)
		CReferenceType superCi = null;
		if (owner == null &&
			binding == null && providing == null && 
			! isWeavelet(getCClass()))
		{
			superCi = 
				getSuperCollaborationInterface(
					getCClass(), CCI_BINDING);
			if (superCi == null)
				superCi = 
					getSuperCollaborationInterface(
						getCClass(), CCI_PROVIDING);
		}

		// introduce additional parent-parameterized constructor
		FjConstructorDeclaration[] constructors = getConstructors();
		ArrayList oldConstructors = new ArrayList(constructors.length);
		for (int i = 0; i < constructors.length; i++)
		{
			if (superType != null)
			{
				// only if the class has a supertype
				if (owner == null)
				{
					// only for standalone clean classes *)
					oldConstructors.add(
						createStandardBaseClassConstructor(
							constructors[i], 
							superType));
					
					if (superCi != null)
					{
						oldConstructors.add(
							constructors[i]
								.getCollaborationInterfaceConstructor(
									new CClassNameType(
										FjConstants.toIfcName(
											superCi.getQualifiedName())),
									superType));
					}
				}
				
				constructors[i].addSuperTypeParameter(superType);
			}
			// always set a parent; no supertype => null
			setSuperConstructorArgument(constructors[i], superArg);
		}

		// now provide the old constructors for *)
		// by passing a standard baseclass parent
		addMethods((JMethodDeclaration[]) oldConstructors.toArray(
			new JMethodDeclaration[oldConstructors.size()]));
	}
	

	/**
	 * Is it a weavelet? Or does it have any weavelet as parent? 
	 * @param clazz
	 * @return boolean
	 */
	protected boolean isWeavelet(CClass clazz)
	{
		return getSuperCollaborationInterfaceClass(clazz, CCI_WEAVELET) != null;
	}

	/**
	 * Returns the type which contains the modifier passed in the hierarchy,
	 * or null if it does not find one. 
	 * @return CReferenceType
	 */
	protected CReferenceType getSuperCollaborationInterface(
		CClass clazz, int modifier)
	{
		CClass returnClass = getSuperCollaborationInterfaceClass(
			clazz, modifier);
		return 
			returnClass != null 
				? returnClass.getAbstractType() 
				: null;
	}
	/**
	 * Returns the class in the hierarchy which contains the modifier passed,
	 * or null if the class is not found.
	 * @return CClass
	 */
	protected CClass getSuperCollaborationInterfaceClass(
		CClass clazz, int modifier)
	{
		CClass superClass;
		//If it is an interface the second interface is its super type.
		if (clazz.isInterface() && clazz.getInterfaces().length > 1)
			superClass = clazz.getInterfaces()[1].getCClass();
		else
			superClass = clazz.getSuperClass();
					
		if (CModifier.contains(clazz.getModifiers(), modifier))
			return superClass;

		if (superClass != null)
			return getSuperCollaborationInterfaceClass(superClass, modifier);
		
		// No, it does not have a ci
		return null;		
	}	

	/**
	 * @param declaration
	 * @param superArg
	 */
	protected void setSuperConstructorArgument(
		FjConstructorDeclaration constructor, 
		JExpression superArg)
	{
		constructor.setSuperArg(superArg);
	}

	protected FjConstructorDeclaration createStandardBaseClassConstructor(
		FjConstructorDeclaration constructor, CReferenceType superType)
	{
		return constructor.getStandardBaseClassConstructor(
			superType);
	}
	

	protected JTypeDeclaration getCleanInterfaceOwner()
	{
		return getCleanInterface();
	}

	public void setSuperClass()
	{

		if (getSuperClass() == null)
			setSuperClass(new CClassNameType(FjConstants.CHILD_IMPL_TYPE_NAME));
		else
		{
			setSuperClass(
				new CClassNameType(
					FjConstants.cleanInterfaceImplementationName(
						getSuperClass().getQualifiedName())));
		}
	}

	public void setIdent(String ident)
	{
		super.setIdent(ident);

		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjConstructorDeclaration)
				 ((FjConstructorDeclaration) methods[i]).setIdent(ident);
		}
	}

	public FjConstructorDeclaration[] getConstructors()
	{
		FjConstructorDeclaration[] constructors = super.getConstructors();

		// assure one constructor is there
		if (constructors.length != 0)
		{
			return constructors;
		}
		else
		{
			FjConstructorDeclaration noArgsConstructor =
				new FjConstructorDeclaration(
					getTokenReference(),
					Constants.ACC_PUBLIC,
					ident,
					JFormalParameter.EMPTY,
					CReferenceType.EMPTY,
					new FjConstructorBlock(
						getTokenReference(),
						new FjConstructorCall(
							getTokenReference(),
							false,
							JExpression.EMPTY),
						JStatement.EMPTY),
					null,
					null,
					typeFactory);
			append(noArgsConstructor);
			return new FjConstructorDeclaration[] { noArgsConstructor };
		}

	}
	/**
	 * Checks if the class implements or binds all nested 
	 * interfaces from the CI and if the methods are in the right place. The 
	 * expected methods cannot be defined into a implementation class as 
	 * provided in binding classes.
	 * 
	 */
	public void checkTypeBody(CContext context) 
		throws PositionedError
	{
		CReferenceType superCi;
		// The methods must be in the right place, 
		// and the nested classes must be implemented	
		if (binding != null)
		{
			// it must be a collaboration interface.
			cleanInterface.checkCollaborationInterface(context, 
				binding.getQualifiedName());
				
			checkInnerTypeImplementation(context, CCI_EXPECTED,
				binding.getCClass().getInnerClasses(), 
				binding, 
				CaesarMessages.NESTED_TYPE_NOT_BOUND);

			checkCIMethods(context, binding, true, CCI_EXPECTED, 
				CaesarMessages.MUST_IMPLEMENT_EXPECTED_METHOD,
				ident);
			
			checkCIMethods(context, binding, false, CCI_PROVIDED, 
				CaesarMessages.PROVIDED_METHOD_IN_BINDING, null);
							
		}
		else if (providing != null)
		{
			checkProvidingConstructors(context);
			
			cleanInterface.checkCollaborationInterface(context, 
				providing.getQualifiedName());
			
			checkInnerTypeImplementation(context, CCI_PROVIDED,
				providing.getCClass().getInnerClasses(), 
				providing, 
				CaesarMessages.NESTED_TYPE_NOT_PROVIDED);

			checkCIMethods(context, providing, true, CCI_PROVIDED, 
				CaesarMessages.MUST_IMPLEMENT_PROVIDED_METHOD,
				ident);

			checkCIMethods(context, providing, false, CCI_EXPECTED, 
				CaesarMessages.EXPECTED_METHOD_IN_PROVIDING, null);
		}
		else if (
			(superCi = getSuperCollaborationInterface(getCClass(), CCI_BINDING)) 
			!= null)
		{
			checkCIMethods(context, superCi, false, CCI_PROVIDED, 
				CaesarMessages.PROVIDED_METHOD_IN_BINDING, null);
		}
		else if (
			(superCi = getSuperCollaborationInterface(
				getCClass(), CCI_PROVIDING)) 
			!= null)
		{
			checkProvidingConstructors(context);
			checkCIMethods(context, superCi, false, CCI_EXPECTED, 
				CaesarMessages.EXPECTED_METHOD_IN_PROVIDING, null);
		}
		super.checkTypeBody(context);
	}

	/**
	 * Checks if the the class defines a constructor with parameters.
	 * It must be checked only for providing classes.
	 * @param context
	 * @throws PositionedError
	 */
	protected void checkProvidingConstructors(CContext context)
		throws PositionedError
	{
		FjConstructorDeclaration[] constructors = getConstructors();
		for (int i = 0; i < constructors.length; i++)
		{
			check(context, 
				(constructors[i].getParameters().length <= 1), 
				CaesarMessages.PROVIDING_DEFINES_CONSTRUCTOR, ident);
		}
	}

	/**
	 * This method checks if the class implements or binds all nested 
	 * types from the CI.
	 * 
	 * @param context The valid context.
	 * @param modifier modifier of the type, for example CCI_EXPECTED 
	 * 	for bindings. It is for check if it has to define the inner, if there
	 * is no method with this modifier, it does not need to define it.
	 * @param ciInnerClasses The nested classes from the CI.
	 * @param ci The CI that the class implements or binds.
	 * @param errorMessage The error message that must be shown if any of the 
	 * 	nested classes are not implemented or bound.
	 * @throws PositionedError
	 */
	protected void checkInnerTypeImplementation(
		CContext context,
		int modifier,
		CReferenceType[] ciInnerClasses,
		CReferenceType ci,
		MessageDescription errorMessage)
		throws PositionedError
	{
		if (! FjConstants.isIfcImplName(ident))
		{
			for (int i = 0; i < ciInnerClasses.length; i++)
			{
				if (FjConstants.isIfcName(ciInnerClasses[i]
						.getCClass().getIdent())
					&& getCiMethods(ciInnerClasses[i].getCClass(), 
						modifier).size() > 0)
				{
					boolean found = false;
					for (int k = 0; k < inners.length; k++)
					{
						if (inners[k].getCClass().descendsFrom(
							ciInnerClasses[i].getCClass()))
						{
							found = true;
							break;
						}
					}
					check(context, found, errorMessage, 
						ciInnerClasses[i].getCClass().getIdent(), 
						ci.getQualifiedName());
				}
			}
		}
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
		boolean defined,
		int modifier,
		MessageDescription message,
		String aditionalErrorParameter)
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
			(! defined && ! isDefined(ciMethod))
			|| (defined && isDefined(ciMethod)),
				message,
				ciMethod.getIdent(), 
				aditionalErrorParameter);
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
	 * Creates the proxies of the inner classes of this interface.
	 * @param ownerName
	 * @return if it does not have inners it returns an empty array, 
	 * otherwise it returns the proxy declaration of the its nested types.
	 */
	public JTypeDeclaration[] transformInnerInterfaces()
	{
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof CciInterfaceDeclaration)
				inners[i] =
					((CciInterfaceDeclaration)inners[i])
						.createEmptyVirtualDeclaration(this);
		}

		return inners;
	}
	
	/**
	 * Creates empty implementation of the interface methods.
	 * @return
	 */
	public void createEmptyMethodBodies()
	{
		ArrayList emptyMethods = new ArrayList(methods.length);
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjCleanMethodDeclaration)
				emptyMethods.add(
					((FjCleanMethodDeclaration)methods[i]).createEmptyMethod());
		}
		methods = (JMethodDeclaration[])emptyMethods.toArray(
			new JMethodDeclaration[emptyMethods.size()]);
	}
	
}
