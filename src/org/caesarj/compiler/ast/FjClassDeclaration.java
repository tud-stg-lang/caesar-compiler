package org.caesarj.compiler.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.PerSingleton;
import org.caesarj.compiler.CaesarConstants;
import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.compiler.util.DeploymentClassFactory;
import org.caesarj.kjc.CBodyContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceField;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.ClassReader;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JClassBlock;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JTypeNameExpression;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.Utils;

public class FjClassDeclaration
	extends CciClassDeclaration
	implements CaesarConstants
{

	/** The declared advices */
	private AdviceDeclaration[] advices;

	/** e.g. declare precedence */
	private Declare[] declares;

	/** e.g. perSingleton, perCflow,..*/
	private PerClause perClause;

	/** The declared pointcuts */
	protected PointcutDeclaration[] pointcuts;

	public FjClassDeclaration(
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
		JavaStyleComment[] comment)
	{
		this(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			binding,
			providing,
			wrappee,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			PointcutDeclaration.EMPTY,
			AdviceDeclaration.EMPTY,
			null);
	}

	public FjClassDeclaration(
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
		Declare[] declares)
	{
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			binding,
			providing,
			wrappee,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);

		this.pointcuts = pointcuts;
		this.advices = advices;
		this.declares = declares;
	}

	public JMethodDeclaration[] getMethods()
	{
		return methods;
	}

	protected void checkModifiers(final CContext context)
		throws PositionedError
	{
		int modifiers = getModifiers();

		// Syntactically valid class modifiers
		check(context, CModifier.isSubsetOf(modifiers, getAllowedModifiers()),
			KjcMessages.NOT_CLASS_MODIFIERS,
			CModifier.toString(CModifier.notElementsOf(modifiers, 
				getAllowedModifiers())));
		// FJLS 1 : modifiers virtual and override pertain only to member classes
		check(
			context,
			!(CModifier.contains(modifiers, FJC_VIRTUAL)
				|| CModifier.contains(modifiers, FJC_OVERRIDE))
				|| isNested() & CModifier.contains(modifiers, FJC_VIRTUAL)
				|| isNested() & CModifier.contains(modifiers, FJC_OVERRIDE),
			CaesarMessages.MODIFIERS_INNER_CLASSES_ONLY,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, FJC_VIRTUAL | FJC_OVERRIDE)));
		// andreas end

		// JLS 8.1.1 : The access modifier public pertains only to top level
		// classes and to member classes.
		check(
			context,
			(!isNested() || !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PUBLIC),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_PUBLIC)));

		// JLS 8.1.1 : The access modifiers protected and private pertain only to
		// member classes within a directly enclosing class declaration.
		check(
			context,
			(isNested()
				&& getOwner().getCClass().isClass()
				&& !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PROTECTED | ACC_PRIVATE),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_PRIVATE)));

		// JLS 8.1.1 : The access modifier static pertains only to member classes.
		check(
			context,
			isNested() || !CModifier.contains(modifiers, ACC_STATIC),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_STATIC)));

		// JLS 8.1.1.2 : A compile-time error occurs if a class is declared both
		// final and abstract.
		check(
			context,
			CModifier.getSubsetSize(modifiers, ACC_FINAL | ACC_ABSTRACT) <= 1,
			KjcMessages.INCOMPATIBLE_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_FINAL | ACC_ABSTRACT)));

		// JLS 9.5 : A member type declaration in an interface is implicitly
		// static and public.
		if (isNested() && getOwner().getCClass().isInterface())
		{
			setModifiers(modifiers | ACC_STATIC | ACC_PUBLIC);
		}
		if (getCClass().isNested()
			&& getOwner().getCClass().isClass()
			&& !getCClass().isStatic()
			&& context.isStaticContext())
		{
			setModifiers(modifiers | ACC_STATIC);
		}
	}

	public void generateInterface(
		ClassReader classReader,
		CClass owner,
		String prefix)
	{
		sourceClass =
			new FjSourceClass(
				owner,
				getTokenReference(),
				modifiers,
				ident,
				prefix + ident,
				typeVariables,
				isDeprecated(),
				false,
				this,
				perClause);

		setInterface(sourceClass);

		CReferenceType[] innerClasses = new CReferenceType[inners.length];
		for (int i = 0; i < inners.length; i++)
		{
			inners[i].generateInterface(
				classReader,
				sourceClass,
				sourceClass.getQualifiedName() + "$");
			innerClasses[i] = inners[i].getCClass().getAbstractType();
		}

		sourceClass.setInnerClasses(innerClasses);
		uniqueSourceClass = classReader.addSourceClass(sourceClass);
	}

	public JTypeDeclaration[] getInners()
	{
		return inners;
	}

	protected void setInners(JTypeDeclaration[] d)
	{
		inners = d;
	}

	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners =
			(JTypeDeclaration[]) Array.newInstance(
				JTypeDeclaration.class,
				inners.length + 1);
		System.arraycopy(inners, 0, newInners, 0, inners.length);
		newInners[inners.length] = type;
		setInners(newInners);
	}

	protected JTypeDeclaration getCleanInterfaceOwner()
	{
		return this;
	}

	public CReferenceType getSuperClass()
	{
		return superClass;
	}
	public CTypeContext getTypeContext()
	{
		return self;
	}
	private JPhylum[] cacheInitializers;
	protected JPhylum[] getInitializers()
	{
		return cacheInitializers;
	}
	private JavadocComment cacheJavadoc;
	protected JavadocComment getJavadoc()
	{
		return cacheJavadoc;
	}
	private JavaStyleComment[] cacheComment;
	protected JavaStyleComment[] getComment()
	{
		return cacheComment;
	}

	public void join(CContext context) throws PositionedError
	{
		try
		{
			super.join(context);
		}
		catch (PositionedError e)
		{
			// non clean classes may not inherrit
			// clean, virtual or override classes
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.CLASS_EXTENDS_INTERFACE)
			{
				String ifcName =
					e.getFormattedMessage().getParams()[0].toString();
				FjTypeSystem fjts = new FjTypeSystem();
				if (fjts.isCleanIfc(context, getSuperClass().getCClass()))
					throw new PositionedError(
						getTokenReference(),
						CaesarMessages.NON_CLEAN_INHERITS_CLEAN,
						ifcName);
			}
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.TYPE_UNKNOWN
				&& !(this instanceof FjCleanClassDeclaration))
			{

				JTypeDeclaration ownerDecl = getOwnerDeclaration();
				CType familyType = null;
				if (ownerDecl != null)
				{
					String superName = getSuperClass().toString();
					FjTypeSystem fjts = new FjTypeSystem();
					String[] splitName = fjts.splitQualifier(superName);
					if (splitName != null)
					{
						String qualifier = splitName[0];
						String remainder = splitName[1];
						JFieldDeclaration familyField = null;
						int i = 0;
						for (; i < ownerDecl.getFields().length; i++)
						{
							familyField = ownerDecl.getFields()[i];
							if (familyField
								.getVariable()
								.getIdent()
								.equals(qualifier))
							{
								familyType =
									familyField.getVariable().getType();
								break;
							}
						}
						if (familyType != null)
						{
							try
							{
								familyType = familyType.checkType(context);
								if (familyType.isReference())
									new CClassNameType(
										familyType
											.getCClass()
											.getQualifiedName()
											+ "$"
											+ remainder).checkType(
										context);
								// a virtual type is referenced!
								throw new PositionedError(
									getTokenReference(),
									CaesarMessages.MUST_BE_VIRTUAL,
									getIdent());
							}
							catch (UnpositionedError e2)
							{
							}
						}
					}
				}
			}
			throw e;
		} 

	}

	public void checkInterface(CContext context) throws PositionedError
	{

		// register type at CaesarBcelWorld!!!
		CaesarBcelWorld.getInstance().resolve(getCClass());

		//statically deployed classes are considered as aspects
		if (isStaticallyDeployed())
		{
			prepareForStaticDeployment(context);

			modifiers |= ACC_FINAL;
		}




		super.checkInterface(context);


		if (isPrivileged() || isStaticallyDeployed())
		{
			getFjSourceClass().setPerClause(new PerSingleton());
		}

		if (!(isCrosscutting() || isStaticallyDeployed())
			&& (pointcuts.length > 0 || advices.length > 0))
		{
			context.reportTrouble(
				new PositionedError(
					getTokenReference(),
					CaesarMessages
						.POINTCUTS_OR_ADVICES_IN_NON_CROSSCUTTING_CLASS));
		}
		
		

		//if descendants of crosscutting class must be declared crosscutting too
		if (getCClass().getSuperClass() != null
				&& CModifier.contains(
					getCClass().getSuperClass().getModifiers(),
					ACC_CROSSCUTTING)
				&& ! isCrosscutting()) 
//				&& ! (this instanceof DeploymentSupportClassDeclaration))
		{
			context.reportTrouble(
				new PositionedError(
					getTokenReference(),
					CaesarMessages
						.DESCENDANT_OF_CROSSCUTTING_CLASS_NOT_DECLARED_CROSSCUTTING));

		}

		//ckeckInterface of the pointcuts
		for (int j = 0; j < pointcuts.length; j++)
		{
			pointcuts[j].checkInterface(self);
		}
		


	}

	/**
	 * Initilizes the family in the class. It does almost everything that is
	 * done during the checkInterface again.
	 * 
	 * @param context
	 * @throws PositionedError
	 */
	public void initFamilies(CClassContext context) throws PositionedError
	{
		int generatedFields = getCClass().hasOuterThis() ? 1 : 0;

		//Initializes the families of the fields.
		Hashtable hashField =
			new Hashtable(fields.length + generatedFields + 1);
		for (int i = fields.length - 1; i >= 0; i--)
		{
			CSourceField field =
				((FjFieldDeclaration) fields[i]).initFamily(context);

			field.setPosition(i);

			hashField.put(field.getIdent(), field);
		}
		if (generatedFields > 0)
		{
			CSourceField field = outerThis.checkInterface(self);

			field.setPosition(hashField.size());

			hashField.put(JAV_OUTER_THIS, field);
		}

		int generatedMethods = 0;

		if (getDefaultConstructor() != null)
			generatedMethods++;

		if (statInit != null)
			generatedMethods++;

		if (instanceInit != null)
			generatedMethods++;

		// Initializes the families of the methods.
		CMethod[] methodList = new CMethod[methods.length + generatedMethods];
		int i = 0;
		for (; i < methods.length; i++)
		{
			if (methods[i] instanceof FjMethodDeclaration)
				methodList[i] =
					((FjMethodDeclaration) methods[i]).initFamilies(context);
			else
				methodList[i] = methods[i].getMethod();

		}

		JConstructorDeclaration defaultConstructor = getDefaultConstructor();
		if (defaultConstructor != null)
		{
			if (defaultConstructor instanceof FjConstructorDeclaration)
				methodList[i++] =
					((FjConstructorDeclaration) defaultConstructor)
								.initFamilies(context);
			else
				methodList[i++] = defaultConstructor.getMethod();
		}
		if (statInit != null)
			methodList[i++] = statInit.getMethod();
		
		if (instanceInit != null)
			methodList[i++] = instanceInit.getMethod();

		sourceClass.close(
			interfaces,
			sourceClass.getSuperType(),
			hashField,
			methodList);
		

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

	public String getIdent()
	{
		return ident;
	}
	
	public void append(JMethodDeclaration newMethod)
	{
		Vector methods = new Vector(Arrays.asList(this.methods));
		methods.add(newMethod);
		this.methods =
			(JMethodDeclaration[]) Utils.toArray(
				methods,
				JMethodDeclaration.class);
	}

	public FjSourceClass getFjSourceClass()
	{
		return (FjSourceClass) sourceClass;
	}

	public void addField(JFieldDeclaration newField)
	{
		JFieldDeclaration[] newFields =
			new JFieldDeclaration[fields.length + 1];

		System.arraycopy(fields, 0, newFields, 0, fields.length);

		newFields[fields.length] = newField;
		fields = newFields;
	}



	public void addClassBlock(JClassBlock initializerDeclaration)
	{
		JPhylum[] newBody = new JPhylum[body.length + 1];
		System.arraycopy(body, 0, newBody, 0, body.length);
		newBody[body.length] = initializerDeclaration;
		body = newBody;
	}

	public void addInterface(CReferenceType newInterface)
	{
		CReferenceType[] newInterfaces =
			new CReferenceType[interfaces.length + 1];

		System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
		newInterfaces[interfaces.length] = newInterface;

		interfaces = newInterfaces;
	}
	
	/**
	 * Does the class have a clean interface?
	 * @return
	 */
	public boolean isClean()
	{
		return (modifiers & (FJC_CLEAN | FJC_VIRTUAL | FJC_OVERRIDE)) != 0;
	}


	public boolean isPrivileged()
	{
		return (modifiers & ACC_PRIVILEGED) != 0;
	}

	public boolean isCrosscutting()
	{
		return (modifiers & ACC_CROSSCUTTING) != 0;
	}

	public boolean isStaticallyDeployed()
	{
		return (modifiers & ACC_DEPLOYED) != 0;
	}

	public void setMethods(JMethodDeclaration[] methods)
	{
		this.methods = methods;
	}

	public void setFields(JFieldDeclaration[] fields)
	{
		this.fields = fields;
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
	public Declare[] getDeclares()
	{
		return declares;
	}

	/**
	 * Sets the precedenceDeclaration.
	 * @param precedenceDeclaration The precedenceDeclaration to set
	 */
	public void setDeclares(Declare[] declares)
	{
		this.declares = declares;
	}

	/**
	 * Sets the perClause.
	 * @param perClause The perClause to set
	 */
	public void setPerClause(PerClause perClause)
	{
		this.perClause = perClause;
	}

	protected void prepareForStaticDeployment(CContext context)
	{
		for (int i = 0; i < advices.length; i++)
		{
			createAdviceMethodName(advices[i]);

			if (advices[i].isAroundAdvice())
			{
				//create a proceed method for around advices
				addMethod(createProceedMethod(advices[i]));
			}
		}

		CType singletonType = new CClassNameType(getIdent());
		FjVariableDefinition aspectInstanceVar =
			new FjVariableDefinition(
				TokenReference.NO_REF,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				singletonType,
				PER_SINGLETON_INSTANCE_FIELD,
				null);
		addField(
			new FjFieldDeclaration(
				getTokenReference(),
				aspectInstanceVar,
				true,
				null,
				null));
		addMethod(createSingletonAjcClinitMethod(context.getTypeFactory()));

		addClassBlock(createSingletonAspectClinit());
	}

	protected FjMethodDeclaration createSingletonAjcClinitMethod(TypeFactory typeFactory)
	{
		JStatement[] body = { createSingletonClinitMethodStatement_1()};
		return new FjMethodDeclaration(
			TokenReference.NO_REF,
			ACC_PRIVATE | ACC_STATIC,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			AJC_CLINIT_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(TokenReference.NO_REF, body, null),
			null,
			null);
	}

	/**
	 * Creates the following statement:
	 * 
	 * ajc$perSingletonInstance = new AnAspect$SingletonAspect();
	 */
	protected JStatement createSingletonClinitMethodStatement_1()
	{
		JExpression left =
			new FjNameExpression(
				TokenReference.NO_REF,
				PER_SINGLETON_INSTANCE_FIELD);
		JExpression right =
			new CciInternalUnqualifiedInstanceCreation(
				TokenReference.NO_REF,
				new CClassNameType(getIdent()),
				JExpression.EMPTY);
		return new JExpressionStatement(
			TokenReference.NO_REF,
			new FjAssignmentExpression(TokenReference.NO_REF, left, right),
			null);
	}

	protected JClassBlock createSingletonAspectClinit()
	{

		CReferenceType type =
			new CClassNameType(getFjSourceClass().getQualifiedName());
		JExpression prefix = new JTypeNameExpression(getTokenReference(), type);

		JExpression expr =
			new FjMethodCallExpression(
				getTokenReference(),
				prefix,
				AJC_CLINIT_METHOD,
				JExpression.EMPTY);

		JStatement[] body =
			{ new JExpressionStatement(getTokenReference(), expr, null)};

		return new JClassBlock(getTokenReference(), true, body);
	}

	/**
	 * Changes the name of the given advice.
	 */
	protected void createAdviceMethodName(AdviceDeclaration adviceDeclaration)
	{
		String ident =
			NameMangler.adviceName(
				TypeX.forName(getCClass().getQualifiedName()),
				adviceDeclaration.getKind(),
				adviceDeclaration.getTokenReference().getLine());
		adviceDeclaration.setIdent(ident);
	}


	/**
	 * Generates for every nested crosscutting class the corresponding deployment support classes.
	 */
	public void prepareForDynamicDeployment(KjcEnvironment environment)
	{
		List newInners = new ArrayList();

		for (int i = 0; i < inners.length; i++)
		{

			newInners.add(inners[i]);

			if (inners[i] instanceof FjClassDeclaration)
			{

				//create support classes for each crosscutting inner class
				FjClassDeclaration innerCaesarClass =
					(FjClassDeclaration) inners[i];
				if (innerCaesarClass.isCrosscutting())
				{

					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							innerCaesarClass,
							environment);

					//modify the aspect class		
					utils.modifyAspectClass();

					//add the deployment support classes to the enclosing class
					newInners.add(utils.createAspectInterface());
					newInners.add(utils.createMultiInstanceAspectClass());
					newInners.add(utils.createMultiThreadAspectClass());
					newInners.add(utils.createSingletonAspect());
				}

				//handle the inners of the inners
				JTypeDeclaration[] innersInners = innerCaesarClass.getInners();
				for (int j = 0; j < innersInners.length; j++)
				{
					if (innersInners[j] instanceof FjClassDeclaration)
					{
						FjClassDeclaration currentInnerInner =
							(FjClassDeclaration) innersInners[j];
						currentInnerInner.prepareForDynamicDeployment(
							environment);
					}
				}
			}

		}

		inners =
			(JTypeDeclaration[]) newInners.toArray(new JTypeDeclaration[0]);

		//Important! Regenerate the interface of the enclosing class.				
		String prefix = getCClass().getPackage().replace('.', '/') + "/";
		generateInterface(environment.getClassReader(), getOwner(), prefix);
	}

	public void checkTypeBody(CContext context) throws PositionedError
	{

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
	 * Creates the proceed method for around advices.
	 * */
	private JMethodDeclaration createProceedMethod(AdviceDeclaration advice)
	{
		ProceedDeclaration proceedMethodDeclaration =
			new ProceedDeclaration(
				advice.getTokenReference(),
				advice.getReturnType(),
				advice.getIdent() + PROCEED_METHOD,
				advice.getProceedParameters(),
				advice.getIdent());
		//attach proceed-method to the adviceDeclaration
		advice.setProceedMethodDeclaration(proceedMethodDeclaration);
		return proceedMethodDeclaration;
	}

	/**
	 * Constructs the class context.
	 */
	protected CClassContext constructContext(CContext context)
	{
		return new FjClassContext(
			context,
			context.getEnvironment(),
			sourceClass,
			this);
	}

	/**
	 * Return the default class modifiers plus VIRTUAL, OVERRIDE and CLEAN.
	 * 
	 * @see caesar.ci.compiler.ast.CciClassDeclaration#getAllowedModifiers()
	 * @author Walter Augusto Werner
	 */
	protected int getAllowedModifiers()
	{
		return super.getAllowedModifiers() 
			| FJC_VIRTUAL
			| FJC_OVERRIDE
			| FJC_CLEAN
			| ACC_CROSSCUTTING  // Klaus
			| getInternalModifiers();
	}
	
	protected int getInternalModifiers()
	{
		return  CCI_COLLABORATION
				| CCI_BINDING
				| CCI_PROVIDING
				| CCI_WEAVELET
				//Jurgen's
				| ACC_PRIVILEGED 
				| ACC_CROSSCUTTING 
				| ACC_DEPLOYED;
	}

}