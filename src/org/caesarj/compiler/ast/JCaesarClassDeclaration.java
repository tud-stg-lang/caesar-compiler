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
import org.caesarj.compiler.joinpoint.DeploymentPreparation;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;
import org.caesarj.util.Utils;

public class JCaesarClassDeclaration 
	extends JClassDeclaration
{
	/*
	 * Integration of FjClassDeclaration (Karl Klose)
	 */
	
	/** The declared advices */
	protected JAdviceDeclaration[] advices;
	/** e.g. declare precedence */
	protected CaesarDeclare[] declares;

	/** e.g. perSingleton, perCflow,..*/
	protected CaesarPointcut perClause;

	/** The declared pointcuts */
	protected JPointcutDeclaration[] pointcuts;

	
	public JCaesarClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		JPointcutDeclaration[] pointcuts,
		JAdviceDeclaration[] advices,
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


		//statically deployed classes are considered as aspects
		if (isStaticallyDeployed())
		{
			DeploymentPreparation.prepareForStaticDeployment(context, this);

			modifiers |= ACC_FINAL;
		}



		super.checkInterface(context);


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

	protected JMethodDeclaration[] getInterfaceMethods() {
		// TODO
		return this.methods;
	}

	private JMethodDeclaration[] append(
			JMethodDeclaration[] left,
			JMethodDeclaration[] right)
	{

		JMethodDeclaration[] result =
			new JMethodDeclaration[left.length + right.length];
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

	public JCaesarClassDeclaration getBaseClass()
	{
		return this;
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
			new JNameExpression(
				FjConstants.STD_TOKEN_REFERENCE,
				FjConstants.PARENT_NAME);
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


	public void setIdent(String ident)
	{
		super.setIdent(ident);

		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof JConstructorDeclaration)
				 ((JConstructorDeclaration) methods[i]).setIdent(ident);
		}
	}

	public JConstructorDeclaration[] getConstructors()
	{
		JConstructorDeclaration[] constructors = super.getConstructors();

		// assure one constructor is there
		if (constructors.length != 0)
		{
			return constructors;
		}
		else
		{
			JConstructorDeclaration noArgsConstructor =
				new JConstructorDeclaration(
					getTokenReference(),
					ClassfileConstants2.ACC_PUBLIC,
					ident,
					JFormalParameter.EMPTY,
					CReferenceType.EMPTY,
					new JConstructorBlock(
						getTokenReference(),
						new JConstructorCall(
							getTokenReference(),
							false,
							JExpression.EMPTY),
						JStatement.EMPTY),
					null,
					null,
					typeFactory);
			append(noArgsConstructor);
			return new JConstructorDeclaration[] { noArgsConstructor };
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
			new JFieldDeclaration(
				ref, 
				new JVariableDefinition(
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
		
	}
    
	public JPointcutDeclaration[] getPointcuts()
	{
		return pointcuts;
	}

	public JAdviceDeclaration[] getAdvices()
	{
		return advices;
	}

	public void setPointcuts(JPointcutDeclaration[] pointcuts)
	{
		this.pointcuts = pointcuts;
	}

	public void setAdvices(JAdviceDeclaration[] advices)
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

   public boolean isStaticallyDeployed()
   {
	   return (modifiers & ACC_DEPLOYED) != 0;
   }

// TODO !!!
/*
   public void initFamilies(CClassContext context) throws PositionedError
   {
   	   super.initFamilies(context);

	   //ckeckInterface of the advices
	   for (int j = 0; j < advices.length; j++)
	   {
		   advices[j].checkInterface(self);
		   //during the following compiler passes
		   //the advices should be treated like methods
		   getFjSourceClass().addMethod((CSourceAdviceMethod) advices[j].getMethod());
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
*/
}
