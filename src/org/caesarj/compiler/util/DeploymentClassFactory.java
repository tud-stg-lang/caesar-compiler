package org.caesarj.compiler.util;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.PerSingleton;
import org.caesarj.compiler.CaesarConstants;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.compiler.ast.DeploymentSupportClassDeclaration;
import org.caesarj.compiler.ast.FjAssignmentExpression;
import org.caesarj.compiler.ast.FjCastExpression;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjCleanMethodDeclaration;
import org.caesarj.compiler.ast.FjFieldAccessExpression;
import org.caesarj.compiler.ast.FjFieldDeclaration;
import org.caesarj.compiler.ast.FjFormalParameter;
import org.caesarj.compiler.ast.FjInterfaceDeclaration;
import org.caesarj.compiler.ast.FjMethodCallExpression;
import org.caesarj.compiler.ast.FjMethodDeclaration;
import org.caesarj.compiler.ast.FjNameExpression;
import org.caesarj.compiler.ast.FjThisExpression;
import org.caesarj.compiler.ast.FjUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.FjVariableDefinition;
import org.caesarj.compiler.ast.PointcutDeclaration;
import org.caesarj.compiler.ast.ProceedDeclaration;
import org.caesarj.kjc.CArrayType;
import org.caesarj.kjc.CBooleanType;
import org.caesarj.kjc.CByteType;
import org.caesarj.kjc.CCharType;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CDoubleType;
import org.caesarj.kjc.CFloatType;
import org.caesarj.kjc.CIntType;
import org.caesarj.kjc.CLongType;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CShortType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JAssignmentExpression;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JCastExpression;
import org.caesarj.kjc.JCatchClause;
import org.caesarj.kjc.JClassBlock;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorCall;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JEqualityExpression;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JIfStatement;
import org.caesarj.kjc.JIntLiteral;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JNameExpression;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JRelationalExpression;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JStringLiteral;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTryCatchStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JTypeNameExpression;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.JVariableDefinition;
import org.caesarj.kjc.JWhileStatement;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.kjc.TypeFactory;

/**
 * This factory creates the support classes for dynamic deployment.
 * 
 * @author Jürgen Hallpap
 */
public class DeploymentClassFactory implements CaesarConstants {

	private FjClassDeclaration aspectClass;

	private String singletonAspectName;

	private String aspectInterfaceName;

	private String multiInstanceAspectClassName;

	private String multiThreadAspectClassName;

	private String qualifiedAspectInterfaceName;

	private String qualifiedMultiInstanceAspectClassName;

	private String qualifiedMultiThreadAspectClassName;

	private String qualifiedSingletonAspectName;

	private String packagePrefix;

	private KjcEnvironment environment;

	private TypeFactory typeFactory;

	private TokenReference where;

	/**
	 * Constructor for CaesarDeploymentUtils.
	 */
	public DeploymentClassFactory(
		FjClassDeclaration aspectClass,
		KjcEnvironment environment) {
		super();

		this.aspectClass = aspectClass;
		this.where = aspectClass.getTokenReference();
		this.typeFactory = environment.getTypeFactory();
		this.environment = environment;

		initNames();
	}

	private void initNames() {
		String packageName = aspectClass.getCClass().getPackage();
		this.packagePrefix = packageName.length() > 0 ? packageName + "/" : "";

		//Intialize some class and interface identifiers
		this.aspectInterfaceName =
			aspectClass.getIdent() + ASPECT_IFC_EXTENSION;
		this.qualifiedAspectInterfaceName =
			aspectClass.getCClass().getQualifiedName() + ASPECT_IFC_EXTENSION;

		this.multiInstanceAspectClassName =
			aspectClass.getIdent() + MULTI_INSTANCE_CONTAINER_EXTENSION;
		this.qualifiedMultiInstanceAspectClassName =
			aspectClass.getCClass().getQualifiedName()
				+ MULTI_INSTANCE_CONTAINER_EXTENSION;

		this.multiThreadAspectClassName =
			aspectClass.getIdent() + THREAD_MAPPER_EXTENSION;
		this.qualifiedMultiThreadAspectClassName =
			aspectClass.getCClass().getQualifiedName()
				+ THREAD_MAPPER_EXTENSION;

		this.singletonAspectName = aspectClass.getIdent() + REGISTRY_EXTENSION;
		this.qualifiedSingletonAspectName =
			aspectClass.getCClass().getQualifiedName() + REGISTRY_EXTENSION;

		AdviceDeclaration[] advices = aspectClass.getAdvices();
		for (int i = 0; i < advices.length; i++) {
			createAdviceMethodName(advices[i]);
		}

	}

	/**
	 * Creates the Aspect Interface.
	 */
	public FjInterfaceDeclaration createAspectInterface() {

		AdviceDeclaration[] adviceDeclarations = aspectClass.getAdvices();

		FjMethodDeclaration[] methods =
			new FjMethodDeclaration[adviceDeclarations.length];

		for (int i = 0; i < adviceDeclarations.length; i++) {
			methods[i] = createInterfaceAdviceMethod(adviceDeclarations[i]);
		}

		CReferenceType[] superInterfaces =
			{ new CClassNameType(CAESAR_ASPECT_IFC_CLASS)};

		FjInterfaceDeclaration aspectInterface =
			new FjInterfaceDeclaration(
				aspectClass.getTokenReference(),
				ACC_PUBLIC,
				aspectInterfaceName,
				CTypeVariable.EMPTY,
				superInterfaces,
				FjFieldDeclaration.EMPTY,
				methods,
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);

		aspectInterface.generateInterface(
			environment.getClassReader(),
			aspectClass.getOwner(),
			packagePrefix);

		return aspectInterface;
	}

	/**
	 * Creates an advice method for the aspect interface.
	 */
	private FjMethodDeclaration createInterfaceAdviceMethod(AdviceDeclaration advice) {
		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_ABSTRACT,
			CTypeVariable.EMPTY,
			advice.getReturnType(),
			advice.getIdent(),
			advice.getParameters(),
			advice.getExceptions(),
			null,
			null,
			null);

	}

	/**
	 * Modifes the aspect class.
	 * Makes it implement the aspectInterface, adds the required methods 
	 * and adds a private deploymentThread field.
	 */
	public void modifyAspectClass() {

		//implement the aspect interface
		aspectClass.addInterface(
			new CClassNameType(qualifiedAspectInterfaceName));

		//add support methods
		List newMethods = new ArrayList();
		boolean cleanMethodsRequired = (aspectClass instanceof FjCleanClassDeclaration);

		newMethods.add(createAspectClassDeployMethod(cleanMethodsRequired));
		newMethods.add(createAspectClassUndeployMethod(cleanMethodsRequired));
		newMethods.add(createGetDeploymentThreadMethod(cleanMethodsRequired));
		newMethods.add(createSetDeploymentThreadMethod(cleanMethodsRequired));
		newMethods.add(createGetSingletonAspectMethod(cleanMethodsRequired));
		newMethods.add(createGetThreadLocalDeployedInstancesMethod(cleanMethodsRequired));

		aspectClass.addMethods(
			(JMethodDeclaration[]) newMethods.toArray(
				new JMethodDeclaration[0]));

		//add deploymentThread field
		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);
		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				type,
				DEPLOYMENT_THREAD,
				null);

		aspectClass.addField(
			new FjFieldDeclaration(where, var, true, null, null));
	}

	/**
	 * Create the appropriate advice method for the aspect class.
	 * That means, creates a "normal" method with the former advice body.
	 */
	private FjMethodDeclaration createAspectClassAdviceMethod(AdviceDeclaration advice) {
		JStatement[] body = { createAspectClassAdviceStatement_1(advice)};

		return
		new FjCleanMethodDeclaration(
					where,
					ACC_PUBLIC | ACC_SYNCHRONIZED,
					CTypeVariable.EMPTY,
					advice.getReturnType(),
					advice.getIdent(),
					advice.getParameters(),
					advice.getExceptions(),
					new JBlock(where, body, null),
					null,
					null);
	}

	/**
	 * Creates the following statement for all advice kindes except around:
	 * 
	 * if (getDeploymentThread() == Thread.currentThread()) {
	 * 		->advice.getBody()
	 * }
	 * 
	 * Creates the following statement for around advices:
	 * 
	 * if (getDeploymentThread() == Thread.currentThread()) {
	 * 		->advice.getBody()
	 * } else {
	 * 		->createAspectClassAdviceStatement_1_1(advice)
	 * }
	 */
	private JStatement createAspectClassAdviceStatement_1(AdviceDeclaration advice) {
		JExpression left =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		CReferenceType threadType = new CClassNameType("java/lang/Thread");
		JExpression prefix = new JTypeNameExpression(where, threadType);
		JExpression right =
			new FjMethodCallExpression(
				where,
				prefix,
				"currentThread",
				JExpression.EMPTY);
		JExpression cond = new JEqualityExpression(where, true, left, right);

		JStatement[] thenClause = { advice.getBody()};

		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression elseExpr =
			new FjMethodCallExpression(
				where,
				new JTypeNameExpression(where, singletonType),
				advice.getIdent() + PROCEED_METHOD,
				(JExpression[]) args.toArray(new JExpression[0]));
		JStatement[] elseClause =
			{ new JReturnStatement(where, elseExpr, null)};

		return new JIfStatement(
			where,
			cond,
			new JBlock(where, thenClause, null),
			advice.isAroundAdvice()
				? createAspectClassAdviceStatement_1_1(advice)
				: null,
			null);

	}

	/**
	 * Creates the following statement for around advices:
	 * 
	 * (return) proceed(..);
	 */
	private JStatement createAspectClassAdviceStatement_1_1(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new FjMethodCallExpression(
				where,
				new JTypeNameExpression(where, singletonType),
				advice.getIdent() + PROCEED_METHOD,
				(JExpression[]) args.toArray(new JExpression[0]));

		JStatement[] body = new JStatement[1];
		if (advice.getReturnType() == typeFactory.getVoidType()) {
			body[0] = new JExpressionStatement(where, proceedCallExpr, null);
		} else {
			body[0] = new JReturnStatement(where, proceedCallExpr, null);
		};

		return new JBlock(where, body, null);

	}

	/**
	 * Creates the deploy method for single instance aspects.
	 */
	private FjMethodDeclaration createAspectClassDeployMethod(boolean cleanMethod) {
		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		FjFormalParameter param =
			new FjFormalParameter(
				where,
				FjFormalParameter.DES_GENERATED,
				ifcType,
				INSTANCE_TO_DEPLOY,
				false);

		FjFormalParameter[] deployParam = { param };

		JStatement[] body =
			{
				createAspectClassDeployStatement_1(),
				createAspectClassDeployStatement_2(),
				createAspectClassDeployStatement_3(),
				createAspectClassDeployStatement_4(),
				createAspectClassDeployStatement_5()};

		return cleanMethod?
			new FjCleanMethodDeclaration(
						where,
						ACC_PUBLIC | ACC_SYNCHRONIZED,
						CTypeVariable.EMPTY,
						ifcType,
						DEPLOY_METHOD,
						deployParam,
						CReferenceType.EMPTY,
						new JBlock(where, body, null),
						null,
						null):
			
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				DEPLOY_METHOD,
				deployParam,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * AspectIfc aspectInstance;
	 */
	private JStatement createAspectClassDeployStatement_1() {
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		FjVariableDefinition var =
			new FjVariableDefinition(where, 0, ifcType, ASPECT_INSTANCE, null);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * if (deploymentThread == instanceToDeploy.getDeploymentThread())
	 * 		->createAspectClassDeployStatement_2_1()
	 * else
	 *  	->createAspectClassDeployStatement_2_2()
	 */
	private JStatement createAspectClassDeployStatement_2() {
		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression prefix =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);

		JExpression right =
			new FjMethodCallExpression(
				where,
				prefix,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		JExpression cond = new JEqualityExpression(where, true, left, right);

		return new JIfStatement(
			where,
			cond,
			createAspectClassDeployStatement_2_1(),
			createAspectClassDeployStatement_2_2(),
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 * 		->createAspectClassDeployStatement_2_1_1()
	 * 		->createAspectClassDeployStatement_2_1_2() 
	 * }
	 */
	private JStatement createAspectClassDeployStatement_2_1() {
		JStatement[] body =
			{
				createAspectClassDeployStatement_2_1_1(),
				createAspectClassDeployStatement_2_1_2()};

		return new JBlock(where, body, null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance = new MultiInstanceAspect();
	 */
	private JStatement createAspectClassDeployStatement_2_1_1() {
		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);

		CReferenceType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		JExpression right =
			new FjUnqualifiedInstanceCreation(
				where,
				multiInstanceType,
				JExpression.EMPTY);

		JExpression expr = new FjAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Return the following statement:
	 * 
	 * aspectInstance.setDeploymentThread(deploymentThread);
	 */
	private JStatement createAspectClassDeployStatement_2_1_2() {
		JExpression[] args =
			{ new FjNameExpression(where, null, DEPLOYMENT_THREAD)};

		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new FjMethodCallExpression(
				where,
				prefix,
				SET_DEPLOYMENT_THREAD_METHOD,
				args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance = new MultiThreadAspect();
	 */
	private JStatement createAspectClassDeployStatement_2_2() {

		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);
		CReferenceType type =
			new CClassNameType(qualifiedMultiThreadAspectClassName);
		JExpression right =
			new FjUnqualifiedInstanceCreation(where, type, JExpression.EMPTY);
		JExpression expr = new FjAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance._deploy(this);
	 */
	private JStatement createAspectClassDeployStatement_3() {
		JExpression[] args = { new FjThisExpression(where)};
		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new FjMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance._deploy(instanceToDeploy);
	 */
	private JStatement createAspectClassDeployStatement_4() {
		JExpression[] args =
			{ new FjNameExpression(where, null, INSTANCE_TO_DEPLOY)};
		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new FjMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return aspectInstance;
	 */
	private JStatement createAspectClassDeployStatement_5() {
		JExpression expr = new FjNameExpression(where, null, ASPECT_INSTANCE);
		return new JReturnStatement(where, expr, null);
	}

	/**
	 * Creates the undeploy method for a single instance aspect.
	 */
	private FjMethodDeclaration createAspectClassUndeployMethod(boolean cleanMethod) {

		JStatement[] statements =
			{
				createAspectClassUndeployStatement_1(),
				createAspectClassUndeployStatement_2(),
				new JReturnStatement(where, new JNullLiteral(where), null)};

		JBlock body = new JBlock(where, statements, null);

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);
		return cleanMethod?  
			new FjCleanMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				UNDEPLOY_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				body,
				null,
				null):
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				UNDEPLOY_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				body,
				null,
				null);

	}

	/**
	 * Set activeRegistries = (Set) AspectRegistry.threadLocalRegistries.get();
	 */
	private JStatement createAspectClassUndeployStatement_1() {
		CType setType = new CClassNameType("java/util/Set");

		JExpression aspectRegistry =
			new JTypeNameExpression(
				where,
				new CClassNameType(CAESAR_SINGLETON_ASPECT_IFC_CLASS));
		JExpression expr =
			new FjNameExpression(
				where,
				aspectRegistry,
				"threadLocalRegistries");

		JExpression getCall =
			new FjMethodCallExpression(where, expr, "get", JExpression.EMPTY);

		JExpression init = new FjCastExpression(where, getCall, setType);

		JVariableDefinition var =
			new JVariableDefinition(
				where,
				0,
				setType,
				"activeRegistries",
				init);
		return new JVariableDeclarationStatement(where, var, null);

	}

	/**
	 * activeRegistries.remove(AnAspect.ajc$perSingletonInstance);
	 */
	private JStatement createAspectClassUndeployStatement_2() {

		JExpression type =
			new JTypeNameExpression(
				where,
				new CClassNameType(qualifiedSingletonAspectName));

		JExpression prefix = new FjNameExpression(where, "activeRegistries");
		JExpression[] args =
			{
				 new FjFieldAccessExpression(
					where,
					type,
					PER_SINGLETON_INSTANCE_FIELD)};
		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, "remove", args);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	 * Creates the getSingletonAspectMethod.
	 * 
	 * return singletonAspectType.ajc$perSingletonInstance
	 */
	private FjMethodDeclaration createGetSingletonAspectMethod(boolean cleanMethod) {

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression prefix = new JTypeNameExpression(where, singletonType);

		JExpression expr =
			new FjNameExpression(where, prefix, PER_SINGLETON_INSTANCE_FIELD);

		JStatement[] body = { new JReturnStatement(where, expr, null)};

		CReferenceType ifcType =
			new CClassNameType(CAESAR_SINGLETON_ASPECT_IFC_CLASS);

		return cleanMethod?
			new FjCleanMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				GET_SINGLETON_ASPECT_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null):
		 
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				GET_SINGLETON_ASPECT_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	/**
	 * Creates a class, that handles mulitple deployed instances of the same class.
	 */
	public FjClassDeclaration createMultiInstanceAspectClass() {

		CClassNameType stackType = new CClassNameType("java/util/Stack");

		JExpression stackInit =
			new FjUnqualifiedInstanceCreation(
				where,
				stackType,
				JExpression.EMPTY);

		FjVariableDefinition deployedInstances =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				stackType,
				DEPLOYED_INSTANCES,
				stackInit);

		FjVariableDefinition deploymentThread =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				new CClassNameType(QUALIFIED_THREAD_CLASS),
				DEPLOYMENT_THREAD,
				null);

		FjFieldDeclaration[] fields = new FjFieldDeclaration[2];

		fields[0] =
			new FjFieldDeclaration(where, deployedInstances, true, null, null);

		fields[1] =
			new FjFieldDeclaration(where, deploymentThread, true, null, null);

		List methods = new ArrayList();

		methods.add(createMultiInstanceDeployMethod());
		methods.add(createMultiInstanceUndeployMethod());
		methods.add(createSetDeploymentThreadMethod(false));
		methods.add(createGetDeploymentThreadMethod(false));
		methods.add(createMultiInstanceGetDeployedInstancesMethod());
		methods.add(createGetThreadLocalDeployedInstancesMethod(false));

		AdviceDeclaration[] adviceMethods = aspectClass.getAdvices();
		List inners = new ArrayList();

		for (int i = 0; i < adviceMethods.length; i++) {
			methods.add(createMultiInstanceAdviceMethod(adviceMethods[i]));
			if (adviceMethods[i].isAroundAdvice()) {
				inners.add(createAroundClosure(adviceMethods[i]));
				methods.add(createDoAroundMethod(adviceMethods[i]));
			}
		}

		CReferenceType[] interfaces =
			{ new CClassNameType(qualifiedAspectInterfaceName)};

		JPhylum[] initializers = { fields[0] };

		FjClassDeclaration multiInstanceAspectClass =
			new DeploymentSupportClassDeclaration(
				where,
				0,
				multiInstanceAspectClassName,
				CTypeVariable.EMPTY,
				null,
				interfaces,
				fields,
				(JMethodDeclaration[]) methods.toArray(
					new JMethodDeclaration[0]),
				(JTypeDeclaration[]) inners.toArray(new JTypeDeclaration[0]),
				initializers,
				null,
				null,
				aspectClass,
				MULTI_INSTANCE_CONTAINER_EXTENSION);

		multiInstanceAspectClass.generateInterface(
			environment.getClassReader(),
			aspectClass.getOwner(),
			packagePrefix);

		return multiInstanceAspectClass;
	}

	/**
	 *  Creates the advice method (for the given advice) for the multi instance aspect.
	 */
	private FjMethodDeclaration createMultiInstanceAdviceMethod(AdviceDeclaration advice) {
		JStatement[] statements =
			{ createMultiInstanceAdviceStatement_1(advice)};

		JBlock body = new JBlock(where, statements, null);

		FjMethodDeclaration adviceMethod =
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				advice.getReturnType(),
				advice.getIdent(),
				advice.getParameters(),
				advice.getExceptions(),
				body,
				null,
				null);

		return adviceMethod;
	}

	/**
	 * Returns the following statement:
	 * 
	 * if (getDeploymentThread() == Thread.currentThread()) {
	 * 		->createMultiInstanceSimpleAdviceStatement_1_1
	 * 		->createMultiInstanceSimpleAdviceStatement_1_2
	 * }
	 * 
	 * for around advices:
	 * if (getDeploymentThread() == Thread.currentThread()) {
	 * 		->createMultiInstanceSimpleAdviceStatement_1_3
	 * } else {
	 * 		->createMultiInstanceSimpleAdviceStatement_1_4
	 * }	 
	 */
	private JStatement createMultiInstanceAdviceStatement_1(AdviceDeclaration advice) {
		JExpression left =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		CReferenceType threadType = new CClassNameType("java/lang/Thread");
		JExpression prefix = new JTypeNameExpression(where, threadType);
		JExpression right =
			new FjMethodCallExpression(
				where,
				prefix,
				"currentThread",
				JExpression.EMPTY);
		JExpression cond = new JEqualityExpression(where, true, left, right);

		List thenClause = new ArrayList();
		List elseClause = new ArrayList();

		if (!advice.isAroundAdvice()) {
			thenClause.add(createMultiInstanceAdviceStatement_1_1(advice));
			thenClause.add(createMultiInstanceAdviceStatement_1_2(advice));
		} else {
			thenClause.add(createMultiInstanceAdviceStatement_1_3(advice));
			elseClause.add(createMultiInstanceAdviceStatement_1_4(advice));

		}

		return new JIfStatement(
			where,
			cond,
			new JBlock(
				where,
				(JStatement[]) thenClause.toArray(new JStatement[0]),
				null),
			new JBlock(
				where,
				(JStatement[]) elseClause.toArray(new JStatement[0]),
				null),
			null);
	}
	/**
	 * Returns the following statement:
	 * 
	 * Iterator iterator = getDeployedInstances().iterator();
	 */
	private JStatement createMultiInstanceAdviceStatement_1_1(AdviceDeclaration advice) {
		JExpression prefix =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression initializer =
			new FjMethodCallExpression(
				where,
				prefix,
				"iterator",
				JExpression.EMPTY);

		CType iteratorType = new CClassNameType(QUALIFIED_ITERATOR_CLASS);

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				0,
				iteratorType,
				"iterator",
				initializer);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * while(iterator.hasNext()) {
	 * 	->createMultiInstanceSimpleAdviceStatement_1_2_1()
	 *  ->createMultiInstanceSimpleAdviceStatement_1_2_2()
	 * }
	 */
	private JStatement createMultiInstanceAdviceStatement_1_2(AdviceDeclaration advice) {
		JExpression prefix = new FjNameExpression(where, null, "iterator");

		JExpression cond =
			new FjMethodCallExpression(
				where,
				prefix,
				"hasNext",
				JExpression.EMPTY);
		JStatement[] statements =
			{
				createMultiInstanceAdviceStatement_1_2_1(advice),
				createMultiInstanceAdviceStatement_1_2_2(advice)};

		JStatement body = new JBlock(where, statements, null);
		return new JWhileStatement(where, cond, body, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * AspectInterface aspectInstance =
	 * 		(AspectInterface) iterator.next();
	 */
	private JStatement createMultiInstanceAdviceStatement_1_2_1(AdviceDeclaration advice) {

		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);

		JExpression prefix = new FjNameExpression(where, null, "iterator");

		JExpression expr =
			new FjMethodCallExpression(
				where,
				prefix,
				"next",
				JExpression.EMPTY);

		JExpression initializer = new FjCastExpression(where, expr, ifcType);

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				0,
				ifcType,
				ASPECT_INSTANCE,
				initializer);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
		* Returns the following statement:
		* 
		* aspectInstance.advice();
		*/
	private JStatement createMultiInstanceAdviceStatement_1_2_2(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new FjNameExpression(where, params[i].getIdent());
		}

		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression expr =
			new FjMethodCallExpression(where, prefix, advice.getIdent(), args);

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, expr, null);
		} else {
			return new JReturnStatement(where, expr, null);
		}
	}

	/**
	 * Create the following statement:
	 * 
	 * (return) proceed();
	 */
	private JStatement createMultiInstanceAdviceStatement_1_3(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		List args = new ArrayList();

		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}
		//		args.add(new JNameExpression(where, DEPLOYED_INSTANCES));
		args.add(
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY));

		JExpression doAroundExpr =
			new JMethodCallExpression(
				where,
				null,
				("do" + advice.getIdent()).intern(),
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, doAroundExpr, null);
		} else {
			return new JReturnStatement(where, doAroundExpr, null);
		}
	}

	/**
	 * Create the following statement:
	 * 
	 * (return) proceed();
	 */
	private JStatement createMultiInstanceAdviceStatement_1_4(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new FjMethodCallExpression(
				where,
				new JTypeNameExpression(where, singletonType),
				advice.getIdent() + PROCEED_METHOD,
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, proceedCallExpr, null);
		} else {
			return new JReturnStatement(where, proceedCallExpr, null);
		}

	}

	private FjMethodDeclaration createDoAroundMethod(AdviceDeclaration advice) {
		JFormalParameter[] adviceParams = advice.getParameters();

		FjFormalParameter[] params =
			new FjFormalParameter[advice.getParameters().length + 1];

		for (int i = 0; i < adviceParams.length; i++) {
			params[i] =
				new FjFormalParameter(
					where,
					FjFormalParameter.DES_PARAMETER,
					adviceParams[i].getType(),
					adviceParams[i].getIdent(),
					false);
		}

		CType stack = new CClassNameType("java/util/Stack");
		params[adviceParams.length] =
			new FjFormalParameter(
				where,
				FjFormalParameter.DES_PARAMETER,
				stack,
				"stack",
				false);

		JStatement[] body = { createAdviceDoAroundMethodStatement_0(advice)};

		return new FjMethodDeclaration(
			where,
			ACC_PROTECTED | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			advice.getReturnType(),
			("do" + advice.getIdent()).intern(),
			params,
			advice.getExceptions(),
			new JBlock(where, body, null),
			null,
			null);

	}

	/**
	 * 
	 * if (list.empty()) 
	 *  -> createAdviceDoAroundMethodStatement_1
	 * else 
	 * 	-> createAdviceDoAroundMethodStatement_2
	 */
	private JStatement createAdviceDoAroundMethodStatement_0(AdviceDeclaration advice) {
		JNameExpression list = new JNameExpression(where, null, "stack");
		JExpression cond =
			new FjMethodCallExpression(where, list, "empty", JExpression.EMPTY);

		JStatement thenClause = createAdviceDoAroundMethodStatement_1(advice);
		JStatement elseClause = createAdviceDoAroundMethodStatement_2(advice);

		return new JIfStatement(where, cond, thenClause, elseClause, null);
	}

	private JStatement createAdviceDoAroundMethodStatement_1(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new FjMethodCallExpression(
				where,
				new JTypeNameExpression(where, singletonType),
				advice.getIdent() + PROCEED_METHOD,
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, proceedCallExpr, null);
		} else {
			return new JReturnStatement(where, proceedCallExpr, null);
		}
	}

	/**
	 * list.get(0).around(
	 * 
	 */
	private JStatement createAdviceDoAroundMethodStatement_2(AdviceDeclaration advice) {
		JExpression list = new FjNameExpression(where, null, "stack");
		JFormalParameter[] params = advice.getParameters();

		JExpression[] getArgs = { new JIntLiteral(where, 0)};

		JExpression get =
			new FjMethodCallExpression(where, list, "get", getArgs);

		CReferenceType type =
			new CClassNameType(
				(advice.getIdent() + "$MultiInstanceAroundClosure").intern());

		//	JClassDeclaration decl = createAnonymousAroundClosure(advice);

		List creationArgs = new ArrayList();
		for (int i = 0; i < advice.getParameters().length; i++) {
			creationArgs.add(
				new JNameExpression(
					where,
					advice.getParameters()[i].getIdent()));
		}
		creationArgs.add(list);
		creationArgs.add(new JThisExpression(where));

		List args = new ArrayList();
		int aroundClosurePos = advice.getProceedParameters().length - 1;

		for (int i = 0; i < params.length; i++) {
			if (i == aroundClosurePos) {
				args.add(
					new JUnqualifiedInstanceCreation(
						where,
						type,
						(JExpression[]) creationArgs.toArray(
							new JExpression[0])));
			} else {
				args.add(new FjNameExpression(where, params[i].getIdent()));
			}

		}

		JExpression aroundCall =
			new FjMethodCallExpression(
				where,
				new FjCastExpression(
					where,
					get,
					new CClassNameType(qualifiedAspectInterfaceName)),
				advice.getIdent(),
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, aroundCall, null);
		} else {
			return new JReturnStatement(where, aroundCall, null);
		}
	}

	/**
	 * Creates the getDeployedInstances() method for multi-instance aspects.
	 */
	private FjMethodDeclaration createMultiInstanceGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new FjNameExpression(where, DEPLOYED_INSTANCES),
					null)};

		return new FjMethodDeclaration(
			where,
			ACC_PROTECTED | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			new CClassNameType("java/util/Stack"),
			GET_DEPLOYED_INSTANCES_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);

	}

	/**
	 * Creates the deploy method for multi instance aspects.
	 */
	private FjMethodDeclaration createMultiInstanceDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		FjFormalParameter[] params =
			{
				 new FjFormalParameter(
					where,
					FjFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false)};

		JStatement[] statements = { createMultiInstanceDeployStatement_1()};

		JBlock body = new JBlock(where, statements, null);

		FjMethodDeclaration deployMethod =
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				DEPLOY_METHOD,
				params,
				CReferenceType.EMPTY,
				body,
				null,
				null);

		return deployMethod;
	}

	/**
	 * Returns the following statement:
	 * 
	 * if (deploymentThread == instanceToDeploy._getDeploymentThread())
	 * 	-> createMultiInstanceDeployStatement_1_1()
	 * else
	 * 	-> createMultiInstanceDeployStatement_1_2()
	 * */
	private JStatement createMultiInstanceDeployStatement_1() {

		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression prefix =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);

		JExpression right =
			new FjMethodCallExpression(
				where,
				prefix,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		JExpression cond = new JEqualityExpression(where, true, left, right);

		return new JIfStatement(
			where,
			cond,
			createMultiInstanceDeployStatement_1_1(),
			createMultiInstanceDeployStatement_1_2(),
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 * 	deployedInstances.push(instanceToDeploy);
	 * 	return this;
	 * }
	 */
	private JStatement createMultiInstanceDeployStatement_1_1() {
		JStatement[] body = new JStatement[2];

		JExpression prefix =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);

		JExpression[] args = new JExpression[1];
		args[0] = new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);

		body[0] =
			new JExpressionStatement(
				where,
				new FjMethodCallExpression(where, prefix, "push", args),
				null);

		body[1] =
			new JReturnStatement(where, new FjThisExpression(where), null);

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 * 		->createMultiInstanceDeployStatement_1_2_1()
	 * 		->createMultiInstanceDeployStatement_1_2_2()
	 * 		->createMultiInstanceDeployStatement_1_2_3()
	 * 		->createMultiInstanceDeployStatement_1_2_4()
	 * }
	 */
	private JStatement createMultiInstanceDeployStatement_1_2() {
		JStatement[] body =
			{
				createMultiInstanceDeployStatement_1_2_1(),
				createMultiInstanceDeployStatement_1_2_2(),
				createMultiInstanceDeployStatement_1_2_3(),
				createMultiInstanceDeployStatement_1_2_4()};

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * CaesarAspectIfc apsectInstance = new MultiThreadAspect();
	 */
	private JStatement createMultiInstanceDeployStatement_1_2_1() {
		CReferenceType multiThreadType =
			new CClassNameType(qualifiedMultiThreadAspectClassName);

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JExpression initializer =
			new FjUnqualifiedInstanceCreation(
				where,
				multiThreadType,
				JExpression.EMPTY);

		FjVariableDefinition localVarDef =
			new FjVariableDefinition(
				where,
				0,
				ifcType,
				ASPECT_INSTANCE,
				initializer);

		return new JVariableDeclarationStatement(where, localVarDef, null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance._deploy(this);
	 */
	private JStatement createMultiInstanceDeployStatement_1_2_2() {
		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression[] args = { new FjThisExpression(where)};

		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance._deploy(instanceToDeploy);
	 */
	private JStatement createMultiInstanceDeployStatement_1_2_3() {
		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression[] args =
			{ new FjNameExpression(where, null, INSTANCE_TO_DEPLOY)};

		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return aspectInstance;
	 */
	private JStatement createMultiInstanceDeployStatement_1_2_4() {
		return new JReturnStatement(
			where,
			new FjNameExpression(where, null, ASPECT_INSTANCE),
			null);

	}

	/**
	 * Creates the undeploy method for multi instance aspects.
	 */
	private FjMethodDeclaration createMultiInstanceUndeployMethod() {
		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JStatement[] statements =
			{
				createMultiInstanceUndeployStatement_1(),
				createMultiInstanceUndeployStatement_2(),
				createMultiInstanceUndeployStatement_3()};

		JBlock body = new JBlock(where, statements, null);

		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			ifcType,
			UNDEPLOY_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			body,
			null,
			null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * deployedInstances.pop();
	 */
	private JStatement createMultiInstanceUndeployStatement_1() {

		JExpression prefix =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);
		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, "pop", JExpression.EMPTY);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * if (deployedInstances.size() < 2) {
	 * 	return (CaesarAspectIfc) deployedInstances.pop();
	 * }
	 */
	private JStatement createMultiInstanceUndeployStatement_2() {

		JExpression prefix =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);

		JExpression left =
			new FjMethodCallExpression(
				where,
				prefix,
				"size",
				JExpression.EMPTY);

		JExpression right = new JIntLiteral(where, 2);

		JExpression cond =
			new JRelationalExpression(where, OPE_LT, left, right);

		FjMethodCallExpression pop =
			new FjMethodCallExpression(where, prefix, "pop", JExpression.EMPTY);

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JExpression cast = new FjCastExpression(where, pop, ifcType);

		return new JIfStatement(
			where,
			cond,
			new JReturnStatement(where, cast, null),
			null,
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return this;
	 */
	private JStatement createMultiInstanceUndeployStatement_3() {
		return new JReturnStatement(where, new FjThisExpression(where), null);
	}

	/**
	 * Creates the multi thread aspect class which is needed for aspect deployment
	 * out of different threads.
	 */
	public FjClassDeclaration createMultiThreadAspectClass() {

		CReferenceType hashMapType =
			new CClassNameType("java/util/WeakHashMap");

		JExpression initializer =
			new FjUnqualifiedInstanceCreation(
				where,
				hashMapType,
				JExpression.EMPTY);

		CType mapType = new CClassNameType("java/util/WeakHashMap");

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				mapType,
				PER_THREAD_DEPLOYED_INSTANCES,
				initializer);

		FjFieldDeclaration perThreadMap =
			new FjFieldDeclaration(where, var, true, null, null);

		CType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		FjVariableDefinition deploymentThreadVar =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				threadType,
				DEPLOYMENT_THREAD,
				new JNullLiteral(where));

		FjFieldDeclaration deploymentThread =
			new FjFieldDeclaration(
				where,
				deploymentThreadVar,
				true,
				null,
				null);

		FjFieldDeclaration[] fields = { perThreadMap, deploymentThread };

		List methods = new ArrayList();

		methods.add(createMultiThreadDeployMethod());
		methods.add(createMultiThreadUndeployMethod());
		methods.add(createSetDeploymentThreadMethod(false));
		methods.add(createGetDeploymentThreadMethod(false));
		methods.add(createMultiThreadGetDeployedInstancesMethod());
		methods.add(createMultiThreadGetThreadLocalDeployedInstancesMethod());

		AdviceDeclaration[] adviceMethods = aspectClass.getAdvices();
		for (int i = 0; i < adviceMethods.length; i++) {
			methods.add(createMultiThreadAdviceMethod(adviceMethods[i]));
		}

		CReferenceType[] interfaces =
			{ new CClassNameType(qualifiedAspectInterfaceName)};

		JPhylum[] initializers = { perThreadMap };

		FjClassDeclaration multiThreadClassDeclaration =
			new DeploymentSupportClassDeclaration(
				where,
				0,
				multiThreadAspectClassName,
				CTypeVariable.EMPTY,
				null,
				interfaces,
				fields,
				(JMethodDeclaration[]) methods.toArray(
					new JMethodDeclaration[0]),
				new JTypeDeclaration[0],
				initializers,
				null,
				null,
				aspectClass,
				THREAD_MAPPER_EXTENSION);

		multiThreadClassDeclaration.generateInterface(
			environment.getClassReader(),
			aspectClass.getOwner(),
			packagePrefix);

		return multiThreadClassDeclaration;

	}

	/**
	 *  Creates the advice method (for the given adivice) for the multi thread aspect.
	 */
	private FjMethodDeclaration createMultiThreadAdviceMethod(AdviceDeclaration advice) {
		JStatement[] statements =
			{
				createMultiThreadAdviceStatement_1(advice),
				createMultiThreadAdviceStatement_2(advice)};

		JBlock body = new JBlock(where, statements, null);

		FjMethodDeclaration adviceMethod =
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				advice.getReturnType(),
				advice.getIdent(),
				advice.getParameters(),
				advice.getExceptions(),
				body,
				null,
				null);

		return adviceMethod;
	}

	/**
	 * Returns the following statement:
	 * 
	 * AspectInterface aspectInstance =
	 * 	(AspectInterface) getDeployedInstances().get(Thread.currentThread());
	 */
	private JStatement createMultiThreadAdviceStatement_1(AdviceDeclaration advice) {

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression threadPrefix = new JTypeNameExpression(where, threadType);
		JExpression[] args =
			{
				 new FjMethodCallExpression(
					where,
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};

		JExpression prefix =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression getMethodCall =
			new FjMethodCallExpression(where, prefix, "get", args);

		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);

		JExpression initializer =
			new FjCastExpression(where, getMethodCall, ifcType);

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				0,
				ifcType,
				ASPECT_INSTANCE,
				initializer);

		return new JVariableDeclarationStatement(where, var, null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * if (aspectInstance != null) {
	 * 	-> createMultiThreadSimpleAdviceStatement_2_1()
	 * }
	 * 	
	 */
	private JStatement createMultiThreadAdviceStatement_2(AdviceDeclaration advice) {

		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		return new JIfStatement(
			where,
			cond,
			createMultiThreadAdviceStatement_2_1(advice),
			advice.isAroundAdvice()
				? createMultiThreadAdviceStatement_2_2(advice)
				: null,
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance.advice();
	 */
	private JStatement createMultiThreadAdviceStatement_2_1(AdviceDeclaration advice) {
		JExpression[] args = new JExpression[advice.getParameters().length];
		for (int i = 0; i < advice.getParameters().length; i++) {

			args[i] =
				new FjNameExpression(
					where,
					null,
					advice.getParameters()[i].getIdent());

		}

		JExpression prefix = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression expr =
			new FjMethodCallExpression(where, prefix, advice.getIdent(), args);

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, expr, null);
		} else {
			return new JReturnStatement(where, expr, null);
		}
	}

	private JStatement createMultiThreadAdviceStatement_2_2(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new FjMethodCallExpression(
				where,
				new JTypeNameExpression(where, singletonType),
				advice.getIdent() + PROCEED_METHOD,
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, proceedCallExpr, null);
		} else {
			return new JReturnStatement(where, proceedCallExpr, null);
		}
	}

	private FjMethodDeclaration createMultiThreadGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new FjNameExpression(
						where,
						null,
						PER_THREAD_DEPLOYED_INSTANCES),
					null)};

		return new FjMethodDeclaration(
			where,
			ACC_PROTECTED | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			new CClassNameType("java/util/Map"),
			GET_DEPLOYED_INSTANCES_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);

	}

	/**
	 * Creates the deploy method for multi thread aspects.
	 */
	private FjMethodDeclaration createMultiThreadDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		FjFormalParameter[] params =
			{
				 new FjFormalParameter(
					where,
					FjFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false)};

		JStatement[] statements =
			{
				createMultiThreadDeployStatement_1(),
				createMultiThreadDeployStatement_2(),
				createMultiThreadDeployStatement_3()};

		JBlock body = new JBlock(where, statements, null);

		FjMethodDeclaration deployMethod =
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				ifcType,
				DEPLOY_METHOD,
				params,
				CReferenceType.EMPTY,
				body,
				null,
				null);

		return deployMethod;

	}

	/**
	 * Returns the following statement:
	 * 
	 * CaesarAspectInterface aspectInstance =
	 * 	(CasesarAspectInterface) perThreadDeployedInstances.get(instanceToDeploy.getDeploymentThread());
	 */
	private JStatement createMultiThreadDeployStatement_1() {

		JExpression instanceToDeployPrefix =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);

		JExpression args[] =
			{
				 new FjMethodCallExpression(
					where,
					instanceToDeployPrefix,
					GET_DEPLOYMENT_THREAD_METHOD,
					JExpression.EMPTY)};

		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression getMethodCall =
			new FjMethodCallExpression(where, prefix, "get", args);

		CType type = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);
		JExpression initializer =
			new FjCastExpression(where, getMethodCall, type);

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				0,
				type,
				ASPECT_INSTANCE,
				initializer);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	 * Returns the following Statement:
	 * 
	 * if (aspecInstance != null)
	 * 		->createMultiThreadDeployStatement_2_1()
	 * else 
	 * 		->createMultiThreadDeployStatement_2_2()
	 */
	private JStatement createMultiThreadDeployStatement_2() {

		JExpression left = new FjNameExpression(where, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		return new JIfStatement(
			where,
			cond,
			createMultiThreadDeployStatement_2_1(),
			createMultiThreadDeployStatement_2_2(),
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 * 		->createMultiThreadDeployStatement_2_1_1()
	 * 		->createMultiThreadDeployStatement_2_1_2()
	 * }
	 */
	private JStatement createMultiThreadDeployStatement_2_1() {
		JStatement[] body =
			{
				createMultiThreadDeployStatement_2_1_1(),
				createMultiThreadDeployStatement_2_1_2()};

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the follwing statement:
	 *
	 * 	aspectInstance = aspectInstance._deploy(instaceToDeploy);
	 */
	private JStatement createMultiThreadDeployStatement_2_1_1() {

		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression param = new FjNameExpression(where, INSTANCE_TO_DEPLOY);

		JExpression[] args = { param };
		JExpression right =
			new FjMethodCallExpression(where, left, DEPLOY_METHOD, args);

		JExpression assignment = new FjAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, assignment, null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * 	perThreadDeployedInstances.put(instanceToDeploy.getDeploymentThread(),aspectInstance);	
	 */
	private JStatement createMultiThreadDeployStatement_2_1_2() {
		JExpression instanceToDeploy =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);

		JExpression[] args =
			{
				new FjMethodCallExpression(
					where,
					instanceToDeploy,
					GET_DEPLOYMENT_THREAD_METHOD,
					JExpression.EMPTY),
				new FjNameExpression(where, ASPECT_INSTANCE)};

		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new FjMethodCallExpression(where, prefix, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * perThreadDeployedInstances.put(instanceToDeploy.getDeploymentThread(), instanceToDeploy);
	 */
	private JStatement createMultiThreadDeployStatement_2_2() {

		JExpression prefix =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);
		JExpression methodCall =
			new FjMethodCallExpression(
				where,
				prefix,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);
		JExpression[] args =
			{
				methodCall,
				new FjNameExpression(where, null, INSTANCE_TO_DEPLOY)};

		JExpression map =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr = new FjMethodCallExpression(where, map, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return this;
	 */
	private JStatement createMultiThreadDeployStatement_3() {
		return new JReturnStatement(where, new FjThisExpression(where), null);
	}

	/**
	 * Creates the undeploy method for the multi thread aspect.
	 */
	private FjMethodDeclaration createMultiThreadUndeployMethod() {
		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JStatement[] statements =
			{
				createMultiThreadUndeployStatement_1(),
				createMultiThreadUndeployStatement_2(),
				createMultiThreadUndeployStatement_3(),
				createMultiThreadUndeployStatement_4()};

		JBlock body = new JBlock(where, statements, null);

		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			ifcType,
			UNDEPLOY_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			body,
			null,
			null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * CaesarAspectInterface aspectInstance = (CaesarAspectInterface)
	 * 		perThreadDeployedInstances.get(Thread.currentThread());
	 */
	private JStatement createMultiThreadUndeployStatement_1() {

		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression thread = new JTypeNameExpression(where, threadType);

		JExpression[] args =
			{
				 new FjMethodCallExpression(
					where,
					thread,
					"currentThread",
					JExpression.EMPTY)};

		JExpression getMethodCall =
			new FjMethodCallExpression(where, prefix, "get", args);

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JExpression initializer =
			new FjCastExpression(where, getMethodCall, ifcType);

		FjVariableDefinition var =
			new FjVariableDefinition(
				where,
				0,
				ifcType,
				ASPECT_INSTANCE,
				initializer);

		return new JVariableDeclarationStatement(where, var, null);

	}

	/**
	 * Returns the following statement:
	 * 
	 * if (aspectInstance != null) {
	 * 		aspectInstance = aspectInstance._undeploy();
	 * }
	 */
	private JStatement createMultiThreadUndeployStatement_2() {

		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		JExpression assignmentRight =
			new FjMethodCallExpression(
				where,
				left,
				UNDEPLOY_METHOD,
				JExpression.EMPTY);

		JExpression expr =
			new FjAssignmentExpression(where, left, assignmentRight);

		return new JIfStatement(
			where,
			cond,
			new JExpressionStatement(where, expr, null),
			null,
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * if (aspectInstance == null) {
	 * 		-> createMultiThreadUndeployStatement_3_1()
	 * else
	 * 		-> createMultiThreadUndeployStatement_3_2()
	 */
	private JStatement createMultiThreadUndeployStatement_3() {
		JExpression left = new FjNameExpression(where, null, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(where, true, left, new JNullLiteral(where));

		return new JIfStatement(
			where,
			cond,
			createMultiThreadUndeployStatement_3_1(),
			createMultiThreadUndeployStatement_3_2(),
			null);
	}

	/** 
	 * Returns the following statement:
	 * 
	 * {
	 * 		createMultiThreadUndeployStatement_3_1_1();
	 * 		createMultiThreadUndeployStatement_3_1_2();
	 * }
	 */
	private JStatement createMultiThreadUndeployStatement_3_1() {
		JStatement[] body =
			{
				createMultiThreadUndeployStatement_3_1_1(),
				createMultiThreadUndeployStatement_3_1_2()};

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * perThreadDeployedInstances.remove(Thread.currentThread());
	 */
	private JStatement createMultiThreadUndeployStatement_3_1_1() {

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression threadClass = new JTypeNameExpression(where, threadType);

		JExpression methodCall =
			new FjMethodCallExpression(
				where,
				threadClass,
				"currentThread",
				JExpression.EMPTY);

		JExpression[] args = { methodCall };

		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new FjMethodCallExpression(where, prefix, "remove", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Creates the following statement:
	 * 
	 * if (perThreadDeployedInstances.size() < 2) {
	 * 		return (AspectIfc)
	 * 		perThreadDeployedInstances.values().iterator().next();
	 * }		
	 */
	private JStatement createMultiThreadUndeployStatement_3_1_2() {
		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression left =
			new FjMethodCallExpression(
				where,
				prefix,
				"size",
				JExpression.EMPTY);

		JExpression cond =
			new JRelationalExpression(
				where,
				OPE_LT,
				left,
				new JIntLiteral(where, 2));

		JExpression entrySetCall =
			new FjMethodCallExpression(
				where,
				prefix,
				"values",
				JExpression.EMPTY);

		JExpression iteratorCall =
			new FjMethodCallExpression(
				where,
				entrySetCall,
				"iterator",
				JExpression.EMPTY);

		JExpression returnExpr =
			new FjMethodCallExpression(
				where,
				iteratorCall,
				"next",
				JExpression.EMPTY);

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JExpression castExpr = new FjCastExpression(where, returnExpr, ifcType);

		return new JIfStatement(
			where,
			cond,
			new JReturnStatement(where, castExpr, null),
			null,
			null);
	}

	/** 
	 * Returns the following statement:
	 * 
	 * perThreadDeployedInstances.put(Thread.currentThread(), aspectInstance);
	 */
	private JStatement createMultiThreadUndeployStatement_3_2() {
		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JExpression threadPrefix = new JTypeNameExpression(where, threadType);

		JExpression methodCall =
			new FjMethodCallExpression(
				where,
				threadPrefix,
				"currentThread",
				JExpression.EMPTY);

		JExpression[] args =
			{ methodCall, new FjNameExpression(where, null, ASPECT_INSTANCE)};

		JExpression prefix =
			new FjFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new FjMethodCallExpression(where, prefix, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return this;
	 */
	private JStatement createMultiThreadUndeployStatement_4() {
		return new JReturnStatement(where, new FjThisExpression(where), null);
	}

	/**
	 * Creates the getDeploymentThread method for all implementors
	 * of the aspect interface.
	 */
	private FjMethodDeclaration createGetDeploymentThreadMethod(boolean cleanMethod) {
		JExpression fieldExpr =
			new FjFieldAccessExpression(where, DEPLOYMENT_THREAD);
		JStatement[] body = { new JReturnStatement(where, fieldExpr, null)};

		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);

		return cleanMethod? 
			new FjCleanMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				type,
				GET_DEPLOYMENT_THREAD_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null):
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				type,
				GET_DEPLOYMENT_THREAD_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	/**
	 * Creates the setDeploymentThread method for all implementors
	 * of the aspect interface.
	 */
	private FjMethodDeclaration createSetDeploymentThreadMethod(boolean cleanMethod) {
		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JExpression fieldExpr =
			new FjFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression right =
			new FjNameExpression(where, null, DEPLOYMENT_THREAD);

		JExpression assignment = new FjAssignmentExpression(where, left, right);

		JStatement[] body =
			{ new JExpressionStatement(where, assignment, null)};

		FjFormalParameter[] params =
			{
				 new FjFormalParameter(
					where,
					FjFormalParameter.DES_GENERATED,
					type,
					DEPLOYMENT_THREAD,
					false)};

		return cleanMethod ? 		
		  new FjCleanMethodDeclaration(where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			SET_DEPLOYMENT_THREAD_METHOD,
			params,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null) :
			
		  new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			SET_DEPLOYMENT_THREAD_METHOD,
			params,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	 * Creates the singleton aspect,the class which is needed by the weaver.
	 * It manages the deployment of aspects and dispatches the 
	 * advice method calls to the deployed instances.
	 */
	public FjClassDeclaration createSingletonAspect() {
		AdviceDeclaration[] advices = aspectClass.getAdvices();
		JMethodDeclaration[] methods = aspectClass.getMethods();
		List fields = new ArrayList();

		List singletonAspectMethods = new ArrayList();
		JMethodDeclaration[] aspectClassMethods =
			new JMethodDeclaration[methods.length + advices.length];

		AdviceDeclaration[] modifiedAdvices =
			new AdviceDeclaration[advices.length];

		System.arraycopy(
			methods,
			0,
			aspectClassMethods,
			advices.length,
			methods.length);

		for (int i = 0; i < advices.length; i++) {

			//add the advice method to the aspect class
			aspectClassMethods[i] = createAspectClassAdviceMethod(advices[i]);

			//create a proceed method for around advices
			if (advices[i].isAroundAdvice()) {
				singletonAspectMethods.add(createProceedMethod(advices[i]));
			}

			modifiedAdvices[i] = createSingletonAspectAdviceMethod(advices[i]);
		}

		//create the deploy and undeploy method
		singletonAspectMethods.add(createSingletonAspectDeployMethod());
		singletonAspectMethods.add(createSingletonAspectUndeployMethod());
		singletonAspectMethods.add(createSingletonGetDeployedInstancesMethod());
		singletonAspectMethods.add(
			createSingletonGetThreadLocalDeployedInstancesMethod());

		//create the deployedInstances field
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		FjVariableDefinition deployedInstancesVar =
			new FjVariableDefinition(
				where,
				ACC_PRIVATE,
				ifcType,
				DEPLOYED_INSTANCES,
				null);

		fields.add(
			new FjFieldDeclaration(
				where,
				deployedInstancesVar,
				true,
				null,
				null));

		if (!CModifier.contains(aspectClass.getModifiers(), ACC_ABSTRACT)) {
			singletonAspectMethods.add(createSingletonAjcClinitMethod());
			singletonAspectMethods.add(createAspectOfMethod());
		}

		//create the ajc$perSingletonInstance field
		CType singletonType = new CClassNameType(qualifiedSingletonAspectName);
		FjVariableDefinition aspectInstanceVar =
			new FjVariableDefinition(
				where,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				singletonType,
				PER_SINGLETON_INSTANCE_FIELD,
				null);

		fields.add(
			new FjFieldDeclaration(where, aspectInstanceVar, true, null, null));

		//Implement the CaesarSingletonAspectIfc
		CReferenceType[] interfaces =
			{ new CClassNameType(CAESAR_SINGLETON_ASPECT_IFC_CLASS)};

		int modifiers = aspectClass.getModifiers();
		if (aspectClass.getOwner() != null) {
			//the nested singletons need to be static
			modifiers |= ACC_STATIC;
		}
		modifiers = CModifier.notElementsOf(modifiers, ACC_DEPLOYED);

		JPhylum[] initializers;
		if (!CModifier.contains(aspectClass.getModifiers(), ACC_ABSTRACT)) {
			initializers = new JPhylum[1];
			initializers[0] = createSingletonAspectClinit();
		} else {
			initializers = new JPhylum[0];
		}
		
		/* 
		 * If the Aspect is not abstract, keep the pointcuts in the Aspect. 
		 * otherwise copy the pointcuts to the AspectRegistry (aka 
		 * singletonAspect).  
		 * Also the Inheritance relation between registries is only kept, 
		 * if the aspect is abstract. see DeploymentSupportClass.checkInterface 
		 * for details.
		 */
		FjClassDeclaration singletonAspect=null;
		
		
			if((!CModifier.contains(aspectClass.getModifiers(),ACC_ABSTRACT))
			){
				
				//create the aspect
				singletonAspect=
				new DeploymentSupportClassDeclaration(
					aspectClass.getTokenReference(),
					modifiers,
					singletonAspectName,
					CTypeVariable.EMPTY,
					null,
					interfaces,
					(FjFieldDeclaration[]) fields.toArray(
						new FjFieldDeclaration[0]),
					(JMethodDeclaration[]) singletonAspectMethods.toArray(
						new JMethodDeclaration[0]),
					new JTypeDeclaration[0],
					initializers,
					null,
					null,
						//in concrete Aspects, COPY the pointcuts to the singleton. 
						// Only the Pointcutresolver complains.
					//aspectClass.getPointcuts(),
					new PointcutDeclaration[0],
					modifiedAdvices,
					aspectClass.getDeclares(),
					aspectClass,
					REGISTRY_EXTENSION);

			singletonAspect.setPerClause(new PerSingleton());

			
			//aspectClass.setPointcuts(new PointcutDeclaration[0]);
			}else{
				//create the aspect
				 singletonAspect =
				new DeploymentSupportClassDeclaration(
					aspectClass.getTokenReference(),
					modifiers,
					singletonAspectName,
					CTypeVariable.EMPTY,
					null,
					interfaces,
					(FjFieldDeclaration[]) fields.toArray(
						new FjFieldDeclaration[0]),
					(JMethodDeclaration[]) singletonAspectMethods.toArray(
						new JMethodDeclaration[0]),
					new JTypeDeclaration[0],
					initializers,
					null,
					null,
					aspectClass.getPointcuts(),
					//new PointcutDeclaration[0],
					modifiedAdvices,
					aspectClass.getDeclares(),
					aspectClass,
					REGISTRY_EXTENSION);

			singletonAspect.setPerClause(new PerSingleton());

			
			aspectClass.setPointcuts(new PointcutDeclaration[0]);
			}
		
		aspectClass.setAdvices(new AdviceDeclaration[0]);
		aspectClass.setDeclares(null);
		aspectClass.setMethods(aspectClassMethods);

		singletonAspect.generateInterface(
			environment.getClassReader(),
			aspectClass.getOwner(),
			packagePrefix);

		return singletonAspect;
	}


 

	private JClassBlock createSingletonAspectClinit() {

		CReferenceType type = new CClassNameType(qualifiedSingletonAspectName);
		JExpression prefix = new JTypeNameExpression(where, type);

		JExpression expr =
			new FjMethodCallExpression(
				where,
				prefix,
				AJC_CLINIT_METHOD,
				JExpression.EMPTY);

		JStatement[] body = { new JExpressionStatement(where, expr, null)};

		return new JClassBlock(where, true, body);
	}

	/**
	 * Creates an AdviceDeclaration, that has the same interface
	 * as the given advice but dispatches calls the aspect instance.
	 */
	private AdviceDeclaration createSingletonAspectAdviceMethod(AdviceDeclaration advice) {
		JStatement[] body = { createSingletonAdviceStatement_1(advice)};
		advice.setBody(new JBlock(where, body, null));
		return advice;
	}

	/** 
	 * Returns the following statement:
	 * 
	 * if (getDeployedInstances()!= null) {
	 * 	->createSingletonAdviceStatement_1_1()	
	 * } else {
	 * 	->createSingletonAdviceStatement_1_2()	
	 * }
	 */
	private JStatement createSingletonAdviceStatement_1(AdviceDeclaration advice) {

		JExpression left =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);
		//XXX FjEqualityExpression leads to a NullPointerException !?
		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));
		return new JIfStatement(
			where,
			cond,
			createSingletonAdviceStatement_1_1(advice),
			advice.isAroundAdvice()
				? createSingletonAdviceStatement_1_2(advice)
				: null,
			null);
	}

	/**
	* Returns the following statement:
	* 
	* In case of advice.getReturnType() == void
	* 	getDeployedInstances().adviceMethod(); 
	* else
	* 	return getDeployedInstances().adviceMethod();
	*/
	private JStatement createSingletonAdviceStatement_1_1(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();
		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new FjNameExpression(where, params[i].getIdent());
		}

		JExpression prefix =
			new FjCastExpression(
				where,
				new FjMethodCallExpression(
					where,
					null,
					GET_DEPLOYED_INSTANCES_METHOD,
					JExpression.EMPTY),
				new CClassNameType(qualifiedAspectInterfaceName));
		JExpression expr =
			new FjMethodCallExpression(where, prefix, advice.getIdent(), args);
		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, expr, null);
		} else {
			return new JReturnStatement(where, expr, null);
		}

	}

	/**
	* Returns the following statement:
	* 
	* In case of advice.getReturnType() == void
	* 	proceed(); 
	* else
	* 	return proceed();
	*/
	private JStatement createSingletonAdviceStatement_1_2(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();
		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new FjNameExpression(where, params[i].getIdent());
		}

		JExpression expr =
			new FjMethodCallExpression(
				where,
				null,
				advice.getProceedMethodDeclaration().getIdent(),
				args);
		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, expr, null);
		} else {
			return new JReturnStatement(where, expr, null);
		}

	}

	private FjMethodDeclaration createSingletonAjcClinitMethod() {
		JStatement[] body =
			{
				createSingletonClinitMethodStatement_1(),
				createSingletonClinitMethodStatement_2()};
		return new FjMethodDeclaration(
			where,
			ACC_PRIVATE | ACC_STATIC,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			AJC_CLINIT_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	* Creates the following statement:
	* 
	* ajc$perSingletonInstance = new AnAspect$SingletonAspect();
	*/
	private JStatement createSingletonClinitMethodStatement_1() {
		JExpression left =
			new FjNameExpression(where, PER_SINGLETON_INSTANCE_FIELD);
		JExpression right =
			new FjUnqualifiedInstanceCreation(
				where,
				new CClassNameType(singletonAspectName),
				JExpression.EMPTY);
		return new JExpressionStatement(
			where,
			new FjAssignmentExpression(where, left, right),
			null);
	}

	/**
	* Creates the following statement:
	* 
	* try {
	* 	->createSingletonClinitMethodStatement_2_1
	* } catch(ClassNotFoundException e) {
	* }
	*/
	private JStatement createSingletonClinitMethodStatement_2() {
		JStatement[] tryBody = { createSingletonClinitMethodStatement_2_1()};
		JBlock tryBlock = new JBlock(where, tryBody, null);
		JBlock catchBlock = new JBlock(where, JStatement.EMPTY, null);
		CType excType = new CClassNameType("java/lang/ClassNotFoundException");
		JFormalParameter e =
			new JFormalParameter(
				where,
				JFormalParameter.DES_GENERATED,
				excType,
				"e",
				false);
		JCatchClause[] catchClauses = { new JCatchClause(where, e, catchBlock)};
		return new JTryCatchStatement(where, tryBlock, catchClauses, null);
	}

	/**
	* Creates the following statement:
	* 
	* Class.forName("AnAspect");
	*/
	private JStatement createSingletonClinitMethodStatement_2_1() {
		JExpression[] args =
			{
				 new JStringLiteral(
					where,
					aspectClass.getCClass().getQualifiedName().replace(
						'/',
						'.'))};
		CReferenceType type = new CClassNameType("java/lang/Class");
		JExpression prefix = new JTypeNameExpression(where, type);
		JExpression expr =
			new JMethodCallExpression(where, prefix, "forName", args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	* Creates the getDeployedInstances() method for the singleton aspect.
	*/
	private FjMethodDeclaration createSingletonGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new FjNameExpression(where, null, DEPLOYED_INSTANCES),
					null)};
		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			new CClassNameType(CAESAR_ASPECT_IFC_CLASS),
			GET_DEPLOYED_INSTANCES_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	* Creates the deploy method for the singleton aspect.
	*/
	private FjMethodDeclaration createSingletonAspectDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);
		CType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		FjFormalParameter[] params =
			{
				new FjFormalParameter(
					where,
					FjFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false),
				new FjFormalParameter(
					where,
					FjFormalParameter.DES_GENERATED,
					threadType,
					DEPLOYMENT_THREAD,
					false)};
		JStatement[] body =
			{
				createSingletonDeployStatement_0(),
				createSingletonDeployStatement_1(),
				createSingletonDeployStatement_2(),
				createSingletonDeployStatement_3(),
				createSingletonDeployStatement_4(),
				};
		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			DEPLOY_METHOD,
			params,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	* 
	* Returns the following statements:
	* 
	* if (instanceToDeploy == null)
	* 	return;
	*/
	private JStatement createSingletonDeployStatement_0() {
		JStatement thenClause = new JReturnStatement(where, null, null);

		JExpression cond =
			new JEqualityExpression(
				where,
				true,
				new FjNameExpression(where, INSTANCE_TO_DEPLOY),
				new JNullLiteral(where));
		return new JIfStatement(where, cond, thenClause, null, null);
	}

	/**
	* 
	* Returns the following statements:
	* 
	* Set registrySet = (Set) AspectRegistry.threadLocalRegistries.get();
	*/
	private JStatement createSingletonDeployStatement_1() {
		CType setType = new CClassNameType("java/util/Set");

		JExpression prefix =
			new JTypeNameExpression(
				where,
				new CClassNameType(CAESAR_SINGLETON_ASPECT_IFC_CLASS));

		JExpression threadLocalRegistries =
			new FjNameExpression(where, prefix, "threadLocalRegistries");

		JExpression getCall =
			new FjMethodCallExpression(
				where,
				threadLocalRegistries,
				"get",
				JExpression.EMPTY);

		JExpression init = new FjCastExpression(where, getCall, setType);

		JVariableDefinition var =
			new JVariableDefinition(where, 0, setType, "registrySet", init);
		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	* 
	* Returns the following statements:
	* 
	* registrySet.add(this);
	*/
	private JStatement createSingletonDeployStatement_2() {

		JExpression prefix = new FjNameExpression(where, "registrySet");
		JExpression[] args = { new FjThisExpression(where)};

		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, "add", args);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	* 
	* Returns the following statements:
	* 
	* instanceToDeploy.setDeploymentThread(deploymentThread);
	*/
	private JStatement createSingletonDeployStatement_3() {

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression threadPrefix = new JTypeNameExpression(where, threadType);
		JExpression[] args = { new FjNameExpression(where, DEPLOYMENT_THREAD)};
		JExpression prefix = new FjNameExpression(where, INSTANCE_TO_DEPLOY);

		JExpression expr =
			new FjMethodCallExpression(
				where,
				prefix,
				SET_DEPLOYMENT_THREAD_METHOD,
				args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statements:
	 * 
	 * if (deployedInstance == null) 
	 * 	->createSingletonDeployStatement_4_1()
	 * else
	 * 	->createSingletonDeployStatement_4_2()
	 */
	private JStatement createSingletonDeployStatement_4() {
		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);
		JExpression cond =
			new JEqualityExpression(where, true, left, new JNullLiteral(where));
		return new JIfStatement(
			where,
			cond,
			createSingletonDeployStatement_4_1(),
			createSingletonDeployStatement_4_2(),
			null);
	}

	/**
	* Returns the following statement:
	*
	* deployedInstances = (AnAspectIfc) instanceToDeploy;
	*/
	private JStatement createSingletonDeployStatement_4_1() {

		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression varExpr =
			new FjNameExpression(where, null, INSTANCE_TO_DEPLOY);
		JExpression right = new FjCastExpression(where, varExpr, ifcType);
		JExpression expr = new FjAssignmentExpression(where, left, right);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	* Returns the following statement:
	* 
	* deployedInstances = (AnAspectIfc) deployedInstances._deploy(instanceToDeploy);
	*/
	private JStatement createSingletonDeployStatement_4_2() {

		JExpression deployedInstancesField =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);
		JExpression[] args =
			{ new FjNameExpression(where, null, INSTANCE_TO_DEPLOY)};
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression right =
			new FjCastExpression(
				where,
				new FjMethodCallExpression(
					where,
					deployedInstancesField,
					DEPLOY_METHOD,
					args),
				ifcType);
		JExpression expr =
			new FjAssignmentExpression(where, deployedInstancesField, right);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	* Creates the undeploy method for singleton aspect.
	*/
	private FjMethodDeclaration createSingletonAspectUndeployMethod() {

		JStatement[] body = { createSingletonUndeployStatement_2()};
		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			UNDEPLOY_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	* Returns the following statement:
	* 
	* deployedInstances = (AnAspectIfc) deployedInstances._undeploy();
	*/
	private JStatement createSingletonUndeployStatement_2() {

		JExpression left =
			new FjFieldAccessExpression(where, DEPLOYED_INSTANCES);
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression right =
			new FjCastExpression(
				where,
				new FjMethodCallExpression(
					where,
					left,
					UNDEPLOY_METHOD,
					JExpression.EMPTY),
				ifcType);
		JExpression expr = new FjAssignmentExpression(where, left, right);
		return new JExpressionStatement(where, expr, null);
	}

	private FjMethodDeclaration createAspectOfMethod() {

		CType singletonType = new CClassNameType(qualifiedSingletonAspectName);
		JExpression expr =
			new FjFieldAccessExpression(
				where,
				null,
				PER_SINGLETON_INSTANCE_FIELD);
		JStatement[] body = { new JReturnStatement(where, expr, null)};
		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_STATIC,
			CTypeVariable.EMPTY,
			singletonType,
			ASPECT_OF_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	private JMethodDeclaration createGetThreadLocalDeployedInstancesMethod(boolean cleanMethod) {

		CType deployableType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JStatement[] body =
			{ new JReturnStatement(where, new FjThisExpression(where), null)};

		return cleanMethod?
			new FjCleanMethodDeclaration(
				where,
				ACC_PUBLIC,
				CTypeVariable.EMPTY,
				deployableType,
				GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null):
			 
			new FjMethodDeclaration(
				where,
				ACC_PUBLIC,
				CTypeVariable.EMPTY,
				deployableType,
				GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
				FjFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	private JMethodDeclaration createMultiThreadGetThreadLocalDeployedInstancesMethod() {

		CType deployableType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);
		JExpression[] args =
			{
				 new FjMethodCallExpression(
					where,
					new JTypeNameExpression(
						where,
						new CClassNameType(QUALIFIED_THREAD_CLASS)),
					"currentThread",
					JExpression.EMPTY)};

		JExpression getDeployedInstancesCall =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression methodCall =
			new FjMethodCallExpression(
				where,
				getDeployedInstancesCall,
				"get",
				args);

		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new FjCastExpression(where, methodCall, deployableType),
					null)};

		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC,
			CTypeVariable.EMPTY,
			deployableType,
			GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	/**
	 * if (getDeployedInstances() != null) {
	 * 	return getDeployedInstances().getThreadLocalDeployedInstances();
	 * } else 
	 * 	return null;
	 */
	private JMethodDeclaration createSingletonGetThreadLocalDeployedInstancesMethod() {

		CType deployableType = new CClassNameType(CAESAR_ASPECT_IFC_CLASS);

		JStatement[] body =
			{ createSingletonGetThreadLocalDeployedInstancesStatement_1()};

		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC,
			CTypeVariable.EMPTY,
			deployableType,
			GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
			FjFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	private JStatement createSingletonGetThreadLocalDeployedInstancesStatement_1() {
		JExpression left =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);
		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		JExpression prefix =
			new FjMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression expr =
			new FjMethodCallExpression(
				where,
				prefix,
				GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		return new JIfStatement(
			where,
			cond,
			new JReturnStatement(where, expr, null),
			new JReturnStatement(where, new JNullLiteral(where), null),
			null);
	}

	//also implement the other support method here, instead of in CaesarSourceClass

	/**
	* Creates the proceed method for the given around advice.
	*/
	private JMethodDeclaration createProceedMethod(AdviceDeclaration advice) {
		ProceedDeclaration proceedMethodDeclaration =
			new ProceedDeclaration(
				where,
				advice.getReturnType(),
				advice.getIdent() + PROCEED_METHOD,
				advice.getProceedParameters(),
				advice.getIdent());
		//attach proceed-method to the adviceDeclaration
		advice.setProceedMethodDeclaration(proceedMethodDeclaration);
		return proceedMethodDeclaration;
	}

	/**
		* Changes the name of the given advice.
		*/
	private void createAdviceMethodName(AdviceDeclaration adviceDeclaration) {
		String ident =
			NameMangler.adviceName(
				TypeX.forName(aspectClass.getCClass().getQualifiedName()),
				adviceDeclaration.getKind(),
				adviceDeclaration.getTokenReference().getLine());
		adviceDeclaration.setIdent(ident);
	}

	private FjClassDeclaration createAroundClosure(AdviceDeclaration advice) {
		CReferenceType superClass =
			new CClassNameType("org/aspectj/runtime/internal/AroundClosure");

		List fields = new ArrayList();
		JFormalParameter[] params = advice.getParameters();
		for (int i = 0; i < params.length; i++) {
			JExpression init = new JNullLiteral(where);

			CType type = params[i].getType();
			JVariableDefinition var =
				new JVariableDefinition(
					where,
					ACC_PRIVATE,
					type,
					params[i].getIdent(),
					init);
			fields.add(new FjFieldDeclaration(where, var, true, null, null));
		}

		CType stack = new CClassNameType("java/util/Stack");
		JVariableDefinition var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				stack,
				"stack",
				new JNullLiteral(where));
		fields.add(new FjFieldDeclaration(where, var, true, null, null));

		CType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				multiInstanceType,
				"multiInstanceContainer",
				new JNullLiteral(where));
		fields.add(new FjFieldDeclaration(where, var, true, null, null));

		JMethodDeclaration[] methods =
			{ createClosureConstructor(advice), createRunMethod(advice)};

		FjClassDeclaration closure =
			new FjClassDeclaration(
				where,
				0,
				(advice.getIdent() + "$MultiInstanceAroundClosure").intern(),
				CTypeVariable.EMPTY,
				superClass,
				null,
				null,
				null,
				CReferenceType.EMPTY,
				(JFieldDeclaration[]) fields.toArray(new FjFieldDeclaration[0]),
				methods,
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);

		return closure;
	}

	private JConstructorDeclaration createClosureConstructor(AdviceDeclaration advice) {
		JFormalParameter[] adviceParameters = advice.getParameters();

		FjFormalParameter[] params =
			new FjFormalParameter[adviceParameters.length + 2];

		for (int i = 0; i < adviceParameters.length; i++) {
			params[i] =
				new FjFormalParameter(
					where,
					FjFormalParameter.DES_PARAMETER,
					adviceParameters[i].getType(),
					adviceParameters[i].getIdent(),
					false);
		}

		CType stack = new CClassNameType("java/util/Stack");
		params[adviceParameters.length] =
			new FjFormalParameter(
				where,
				FjFormalParameter.DES_PARAMETER,
				stack,
				"stack",
				false);

		CType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		params[adviceParameters.length + 1] =
			new FjFormalParameter(
				where,
				FjFormalParameter.DES_PARAMETER,
				multiInstanceType,
				"multiInstanceContainer",
				false);

		List statements = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			JExpression left =
				new JNameExpression(
					where,
					new JThisExpression(where),
					params[i].getIdent());

			JExpression right =
				new JNameExpression(where, params[i].getIdent());

			JExpression expr = new JAssignmentExpression(where, left, right);

			statements.add(new JExpressionStatement(where, expr, null));
		}

		JConstructorBlock body =
			new JConstructorBlock(
				where,
				new JConstructorCall(where, false, JExpression.EMPTY),
				(JStatement[]) statements.toArray(new JStatement[0]));

		return new JConstructorDeclaration(
			where,
			ACC_PUBLIC,
			(advice.getIdent() + "$MultiInstanceAroundClosure").intern(),
			params,
			CReferenceType.EMPTY,
			body,
			null,
			null,
			typeFactory);
	}

	/**
	 * 
	 * Creates the run method for the AroundClosure.
	 */
	private FjMethodDeclaration createRunMethod(AdviceDeclaration advice) {
		CReferenceType objectType = new CClassNameType("java/lang/Object");
		CArrayType arrayType = new CArrayType(objectType, 1);
		FjFormalParameter[] objArray =
			{
				 new FjFormalParameter(
					where,
					FjFormalParameter.DES_PARAMETER,
					arrayType,
					"arg",
					false)};

		CReferenceType[] exc = { new CClassNameType("java/lang/Throwable")};

		List statements = new ArrayList();
		statements.add(createRunStatement_1());
		statements.add(createRunStatement_2());
		statements.add(createRunStatement_3(advice));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			statements.add(
				new JReturnStatement(where, new JNullLiteral(where), null));
		}

		return new FjMethodDeclaration(
			where,
			ACC_PUBLIC,
			CTypeVariable.EMPTY,
			objectType,
			"run",
			objArray,
			exc,
			new JBlock(
				where,
				(JStatement[]) statements.toArray(new JStatement[0]),
				null),
			null,
			null);
	}

	/**
	 * Stack clone = stack.clone();
	 * 
	 */
	private JStatement createRunStatement_1() {
		CType type = new CClassNameType("java/util/Stack");

		JExpression prefix = new FjNameExpression(where, null, "stack");
		JExpression init =
			new FjMethodCallExpression(
				where,
				prefix,
				"clone",
				JExpression.EMPTY);

		CReferenceType dest = new CClassNameType("java/util/Stack");
		JExpression cast = new JCastExpression(where, init, dest);

		JVariableDefinition var =
			new JVariableDefinition(where, 0, type, "clone", cast);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	 * clone.remove(0);
	 */
	private JStatement createRunStatement_2() {
		JExpression prefix = new FjNameExpression(where, null, "clone");

		JExpression[] args = { new JIntLiteral(where, 0)};
		JExpression methodCall =
			new FjMethodCallExpression(where, prefix, "remove", args);

		return new JExpressionStatement(where, methodCall, null);

	}

	/**
	 * multiInstanceContainer.doaround..();
	 */
	private JStatement createRunStatement_3(AdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		List args = new ArrayList();

		for (int i = 0; i < params.length; i++) {
			args.add(new FjNameExpression(where, params[i].getIdent()));
		}
		args.add(new JNameExpression(where, "clone"));

		JExpression prefix =
			new FjNameExpression(where, "multiInstanceContainer");

		JExpression doAroundExpr =
			new JMethodCallExpression(
				where,
				prefix,
				("do" + advice.getIdent()).intern(),
				(JExpression[]) args.toArray(new JExpression[0]));

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, doAroundExpr, null);
		} else if (advice.getReturnType().isPrimitive()) {
			return new JReturnStatement(
				where,
				createWrapper(advice, doAroundExpr),
				null);
		} else {
			return new JReturnStatement(where, doAroundExpr, null);
		}

	}

	/**
	 * Wraps the primitive value of the expr in a ReferenceType.
	 */
	private JExpression createWrapper(
		AdviceDeclaration advice,
		JExpression expr) {
		CType returnType = advice.getReturnType();
		JExpression[] args = { expr };

		if (returnType instanceof CIntType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Integer"),
				args);
		}

		if (returnType instanceof CFloatType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Float"),
				args);
		}

		if (returnType instanceof CDoubleType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Double"),
				args);
		}

		if (returnType instanceof CByteType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Byte"),
				args);
		}

		if (returnType instanceof CCharType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Character"),
				args);
		}

		if (returnType instanceof CBooleanType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Boolean"),
				args);
		}

		if (returnType instanceof CLongType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Long"),
				args);
		}

		if (returnType instanceof CShortType) {
			return new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType("java/lang/Short"),
				args);
		}

		return null;

	}

}
