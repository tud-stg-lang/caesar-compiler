package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;
import org.caesarj.util.Utils;

public class CaesarClassDeclaration 
	extends JClassDeclaration
{
	/*
	 * Integration of FjClassDeclaration (Karl Klose)
	 */
	
	/** The declared advices */
	protected AdviceDeclaration[] advices;
	/** e.g. declare precedence */
	protected CaesarDeclare[] declares;

	/** e.g. perSingleton, perCflow,..*/
	protected CaesarPointcut perClause;

	/** The declared pointcuts */
	protected PointcutDeclaration[] pointcuts;

	
	public CaesarClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType binding,
		CReferenceType providing,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		PointcutDeclaration[] pointcuts,
		AdviceDeclaration[] advices,
		CaesarDeclare[] declares) {
			super(
				where,
				modifiers & ACC_CAESARCLASS,
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
			this.wrappee = wrappee;
			this.advices = advices;
			this.declares = declares;
			this.pointcuts = pointcuts;
			
				
			// structural detection of crosscutting property
			if ((advices.length > 0) || (pointcuts.length > 0))
				 this.modifiers |= ACC_CROSSCUTTING;
				
			
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

		//statically deployed classes are considered as aspects
		if (isStaticallyDeployed())
		{
			DeploymentPreparation.prepareForStaticDeployment(context, (JClassDeclaration)this);

			modifiers |= ACC_FINAL;
		}



		super.checkInterface(context);

		if (binding != null)
			binding = resolveCollabortationInterface(context, binding);
	
		if (providing != null)
			providing = resolveCollabortationInterface(context, providing);


		if (isPrivileged() || isStaticallyDeployed())
		{
			getFjSourceClass().setPerClause(
				CaesarPointcut.createPerSingleton()
				);
		}

		//ckeckInterface of the pointcuts
		for (int j = 0; j < pointcuts.length; j++)
		{
			pointcuts[j].checkInterface(self);
		}
		

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
	
	public FjCleanClassInterfaceDeclaration createCleanInterface(JClassDeclaration owner)
	{

		//createAccessorsForPrivateMethods();

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
				cleanInterface.addInterface(
					superIfcType, 
					CciConstants.SUPER_TYPE_INDEX);
			}
		}
		return cleanInterface;
	}

	private FjCleanClassIfcImplDeclaration cleanInterfaceImplementation;
	public FjCleanClassIfcImplDeclaration getCleanInterfaceImplementation()
	{
		return cleanInterfaceImplementation;
	}
	public FjCleanClassIfcImplDeclaration createCleanInterfaceImplementation(JClassDeclaration owner)
	{
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
			(modifiers & getInternalModifiers()),
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
			(modifiers & getInternalModifiers()),
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
					FjVirtualClassDeclaration inner = 
						(FjVirtualClassDeclaration) inners[i];
					
					inner.setTypeFactory(typeFactory);
					cleanMethods =
						append(
							cleanMethods,
								inner.getFactoryMethods());
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

	public CaesarClassDeclaration getBaseClass()
	{
		return this;
	}

	public void append(JTypeDeclaration type)
	{
		getCleanInterface().append(type);
	}

	public void addInterface(CReferenceType ifc)
	{
		addInterface(ifc, interfaces.length);
	}

	public void addInterface(CReferenceType ifc, int index)
	{
		Vector interfaces = new Vector(Arrays.asList(this.interfaces));
		interfaces.add(index,ifc);
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
	public CReferenceType getSuperCollaborationInterface(
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
			superClass = clazz.getInterfaces()
				[CciConstants.SUPER_TYPE_INDEX].getCClass();
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
					ClassfileConstants2.ACC_PUBLIC,
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
		checkWrapper(context);
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
		
		if (advices != null)
		{
			for (int i = 0; i < advices.length; i++)
			{
				advices[i].checkBody1(self);
			}
		}

		
		super.checkTypeBody(context);
	}

	/**
	 * Only virtual or override classes can wrap!
	 */
	protected void checkWrapper(CContext context)
		throws PositionedError
	{
		check(context, 
			wrappee == null,
			CaesarMessages.NON_BINDING_WRAPPER,
			getCClass().getQualifiedName());
		
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
	 * Insert the wrapper mappings in the body reference of the class.
	 * It is for the compiler insert the right initialization.
	 */
	public void insertWrapperMappingsInitialization(JFieldDeclaration map)
	{
		JPhylum[] newBody = new JPhylum[body.length + 1];
		System.arraycopy(body, 0, newBody, 1, body.length);
		
		newBody[0] = map;

		body = newBody;
	}


	/**
	 * Creates a field declaration which will contain all 
	 * instances of the wrappers of the type passed.
	 * 
	 * @param binding inner type that will be contained in the map.
	 */
	public JFieldDeclaration createWrapperMap(
		String mapName)
	{
		TokenReference ref = getTokenReference();

		return
			new FjFieldDeclaration(
				ref, 
				new FjVariableDefinition(
					ref, 
					ACC_PRIVATE | ACC_FINAL,
					CciConstants.WRAPPER_MAP_TYPE,
					mapName,
					new JUnqualifiedInstanceCreation(
						ref, 
						CciConstants.WRAPPER_MAP_TYPE, 
						JExpression.EMPTY)),
				CciConstants.WRAPPER_MAP_JAVADOC,
				new JavaStyleComment[0]);
	}
	
	/* (non-Javadoc)
	 * @see org.caesarj.compiler.ast.JClassDeclaration#accept(org.caesarj.compiler.ast.KjcVisitor)
	 */
	public void accept(KjcVisitor p) {
		p.visitFjCleanClassDeclaration(this,
					modifiers,
					ident,
					typeVariables,
					superClass != null ? superClass.toString() : null,
					interfaces,
					body,
					methods,
					inners);
	}
	public PointcutDeclaration[] getPointcuts()
	{
		return pointcuts;
	}

	public AdviceDeclaration[] getAdvices()
	{
		return advices;
	}

	public void setPointcuts(PointcutDeclaration[] pointcuts)
	{
		this.pointcuts = pointcuts;
	}

	public void setAdvices(AdviceDeclaration[] advices)
	{
		this.advices = advices;
	}

	/**
	 * Returns the precedenceDeclaration.
	 * @return Declare
	 */
	public CaesarDeclare[] getDeclares()
	{
		return declares;
	}

	/**
	 * Sets the precedenceDeclaration.
	 * @param precedenceDeclaration The precedenceDeclaration to set
	 */
	public void setDeclares(CaesarDeclare[] declares)
	{
		this.declares = declares;
	}

	/**
	 * Sets the perClause.
	 * @param perClause The perClause to set
	 */
	public void setPerClause(CaesarPointcut perClause)
	{
		this.perClause = perClause;
	}

	public boolean isCrosscutting() {
		return CModifier.contains(modifiers, ACC_CROSSCUTTING);
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
		   if (inners[i] instanceof JClassDeclaration)
		   {
			   inners[i] = 
				   ((JClassDeclaration)inners[i]) 
					 .createOverrideClassDeclaration(this);
		   }
	   }
	   return inners;
   }
	
   /**
	* Transforms the inner classes which bind some CI in virtual types. 
	* The current class must be a binding class (getBinding() != null).
	* @return JTypeDeclaration[] the new nested classes.
	*/
   public JTypeDeclaration[] transformInnerBindingClasses(
	   JClassDeclaration owner)
   {
	   for (int i = 0; i < inners.length; i++)
	   {
		   if (inners[i] instanceof JClassDeclaration)
		   {
			   JClassDeclaration innerClass = (JClassDeclaration)inners[i];
			   if (innerClass.getBinding() != null)
			   {
				   innerClass.setOwnerDeclaration(owner);
				   inners[i] = innerClass.createVirtualClassDeclaration(
					   owner);
			   }
			   else
				   innerClass.transformInnerBindingClasses(this);
		   }
	   }
	   return inners;
   }		
   /**
	* Creates an override type. This is done when the compiler finds a 
	* providing class, so it has to change its inners for an overriding classe.
	* @param owner
	* @return FjOverrideClassDeclaration
	*/
   public FjOverrideClassDeclaration createOverrideClassDeclaration(
	   JClassDeclaration owner)
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
			   wrappee,
			   interfaces,
			   fields,
			   methods,
			   transformInnerProvidingClasses(),
			   this.body,
			   null,
			   null);
   }


   /**
	* Creates an virtual type. This is done when the compiler finds a 
	* binding class, so it has to change its inners for virtual classes.
	* @param owner
	* @return FjOverrideClassDeclaration
	*/
   public FjVirtualClassDeclaration createVirtualClassDeclaration(
	   JClassDeclaration owner)
   {
	   String superClassName = getBindingTypeName();

	   FjVirtualClassDeclaration result =
		   new FjVirtualClassDeclaration(
			   getTokenReference(),
			   (modifiers | CCI_BINDING) & ~FJC_CLEAN,
			   ident,
			   typeVariables,
			   new CClassNameType(superClassName),
			   new CClassNameType(superClassName),
			   null,
			   wrappee,
			   interfaces,
			   fields,
			   methods,
			   transformInnerBindingClasses(this),
			   this.body,
			   null,
			   null);

	   result.addProvidingAcessor();
		
	   return result;
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
   public boolean isStaticallyDeployed()
   {
	   return (modifiers & ACC_DEPLOYED) != 0;
   }
   public void initFamilies(CClassContext context) throws PositionedError
   {
   	   super.initFamilies(context);

	   //ckeckInterface of the advices
	   for (int j = 0; j < advices.length; j++)
	   {
		   advices[j].checkInterface(self);
		   //during the following compiler passes
		   //the advices should be treated like methods
		   getFjSourceClass().addMethod((CaesarAdvice) advices[j].getMethod());
	   }

	   //consider declares
	   if (declares != null)
	   {
		   for (int j = 0; j < declares.length; j++)
		   {
			   declares[j].resolve(
				   new CaesarScope(
					   (FjClassContext) constructContext(context),
					   getFjSourceClass()));
		   }

		   getFjSourceClass().setDeclares(declares);
	   }		
   }

}
