package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarNameMangler;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.expression.literal.JBooleanLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JIntLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JStringLiteral;
import org.caesarj.compiler.ast.phylum.statement.*;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.*;
import org.caesarj.util.TokenReference;

/**
 * This factory creates the support classes for dynamic deployment.
 * 
 * @author Jürgen Hallpap
 */
public class DeploymentClassFactory implements CaesarConstants {

	private CjVirtualClassDeclaration aspectClass;
	
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
		CjVirtualClassDeclaration aspectClass,
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

		CjAdviceDeclaration[] advices = aspectClass.getAdvices();
		for (int i = 0; i < advices.length; i++) {
			createAdviceMethodName(advices[i]);
		}

	}

	/**
	 * Creates the Aspect Interface.
	 */
	public CjInterfaceDeclaration createAspectInterface() {

		CjAdviceDeclaration[] adviceDeclarations = aspectClass.getAdvices();

		CjMethodDeclaration[] methods =
			new CjMethodDeclaration[adviceDeclarations.length];

		for (int i = 0; i < adviceDeclarations.length; i++) {
			methods[i] = createInterfaceAdviceMethod(adviceDeclarations[i]);
		}

		CReferenceType[] superInterfaces =
			{ new CClassNameType(CAESAR_DEPLOYABLE_IFC)};

		CjInterfaceDeclaration aspectInterface =
			new CjInterfaceDeclaration(
				aspectClass.getTokenReference(),
				ACC_PUBLIC,
				aspectInterfaceName,
				CTypeVariable.EMPTY,
				superInterfaces,
				JFieldDeclaration.EMPTY,
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
	private CjMethodDeclaration createInterfaceAdviceMethod(CjAdviceDeclaration advice) {
		return new CjMethodDeclaration(
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

		//IVICA: implement the aspect interface
		aspectClass.getMixinIfcDeclaration().addInterface(
			new CClassNameType(qualifiedAspectInterfaceName));
        
        aspectClass.getMixinIfcDeclaration().addInterface(            
            new CClassNameType(CAESAR_ASPECT_IFC));
        
        aspectClass.getMixinIfcDeclaration().addInterface(            
            new CClassNameType(CAESAR_DEPLOYABLE_IFC));
        
		//add support methods
		List newMethods = new ArrayList();
        
		newMethods.add(createAspectClassDeployMethod());
		newMethods.add(createAspectClassUndeployMethod());
		newMethods.add(createGetDeploymentThreadMethod());
		newMethods.add(createSetDeploymentThreadMethod());
		newMethods.add(createGetThreadLocalDeployedInstancesMethod());

		// abstract aspect classes cannot be deployed
		if (!CModifier.contains(aspectClass.getModifiers(), ACC_ABSTRACT))
		{
			newMethods.add(createDeploySelfMethod());
			newMethods.add(createUndeploySelfMethod());
		}	
		
		aspectClass.addMethods(
			(JMethodDeclaration[]) newMethods.toArray(
				new JMethodDeclaration[0]));

		//add deploymentThread field
		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JVariableDefinition var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				type,
				DEPLOYMENT_THREAD,
				null);

		JFieldDeclaration	field = new JFieldDeclaration(where, var, true, null, null);
		field.setGenerated();
		aspectClass.addField( field );
	}

	/**
	 * Create the appropriate advice method for the aspect class.
	 * That means, creates a "normal" method with the former advice body.
	 */
	private JMethodDeclaration createAspectClassAdviceMethod(CjAdviceDeclaration advice) {
		JStatement[] body = { createAspectClassAdviceStatement_1(advice)};

		return
		new JMethodDeclaration(
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
	private JStatement createAspectClassAdviceStatement_1(CjAdviceDeclaration advice) {
	    AstGenerator gen = environment.getAstGenerator();
	    
	    // TODO this is should be done in the init method
	    // / should be replaced with . for packages
	    // $ should be replaced with . for inners
	    // Registry is not an inner class	    
	    String _qualifiedSingletonAspectName =
	        qualifiedSingletonAspectName.replaceAll("/",".");
	    
	    /*
	    _qualifiedSingletonAspectName =
	        _qualifiedSingletonAspectName.replaceAll("\\$",".");
	    */
	    
	    gen.writeBlock("{");
	    gen.writeBlock("if ("+GET_DEPLOYMENT_THREAD_METHOD+"() == Thread.currentThread()) {}");
	    if(advice.isAroundAdvice()) {
	        gen.writeBlock("else {");
	        
	        if (advice.getReturnType() != typeFactory.getVoidType())
	        	gen.writeBlock("return ");
	        
	        gen.writeBlock(_qualifiedSingletonAspectName+"."+advice.getIdent()+PROCEED_METHOD+"(");
	        
	        JFormalParameter params[] = advice.getProceedParameters();
	        
	        for (int i = 0; i < params.length; i++) {
	            if(i > 0) gen.writeBlock(",");
				gen.writeBlock(params[i].getIdent());
			}
	        
	        gen.writeBlock(");");
	        gen.writeBlock("}");	        
        }
	    gen.writeBlock("}");
	    
	    JIfStatement ifStatement = (JIfStatement)gen.endBlock()[0];
	    ifStatement.setThenClause(advice.getBody());

	    return ifStatement;
	}
	

	/**
	 * Creates the deploy method for single instance aspects. 
	 *	
	 * By returning null simply notifies registry, that it should take care
	 * of deployment itself, because aspect object is unable to create
	 * correct multiinstance container or thread mapper object.
	 * 
	 * public synchronized Deployable $deploy(Deployable aspectToDeploy)
	 * {
	 *    return null;
	 * }
	 */
	
	private JMethodDeclaration createAspectClassDeployMethod() {
	    String _qualifiedSingletonAspectName =
	        qualifiedSingletonAspectName.replaceAll("/",".");

	    AstGenerator gen = environment.getAstGenerator();

	    gen.writeMethod("public synchronized org.caesarj.runtime.Deployable $deploy(org.caesarj.runtime.Deployable aspectToDeploy)");
	    gen.writeMethod("{ return null; }");

	    return gen.endMethod();
	}
	

	/**
	 * Creates the undeploy method for a single instance aspect.
	 */
	private JMethodDeclaration createAspectClassUndeployMethod() {
	    String _qualifiedSingletonAspectName =
	        qualifiedSingletonAspectName.replaceAll("/",".");

	    AstGenerator gen = environment.getAstGenerator();

	    gen.writeMethod("public synchronized org.caesarj.runtime.Deployable $undeploy()");
	    gen.writeMethod("{");
	    gen.writeMethod("java.util.Set activeRegistries = ");
	    gen.writeMethod("(java.util.Set)org.caesarj.runtime.AspectRegistry.threadLocalRegistries.get();");
	    gen.writeMethod("activeRegistries.remove("+_qualifiedSingletonAspectName+".ajc$perSingletonInstance);");
	    gen.writeMethod("return null;");
	    gen.writeMethod("}");

	    return gen.endMethod();
	}
	
	/**
	 * Creates the $deploySelf(Thread) method for aspect class.
	 * 
	 * public synchronized void $deploySelf(Thread thread)
     * {
 	 *     Registry.ajc.perSingletonInstance.$deploy(this, thread);
 	 *     super.$deploySelf(thread);
     * }
 	 *
 	 **/
	private JMethodDeclaration createDeploySelfMethod() {

		/* "Registry.ajc.perSingletonInstance" expression */
		JExpression registryPrefix = 
			new JTypeNameExpression(where, 
				new CClassNameType(qualifiedSingletonAspectName)
			);

		JExpression singletonPrefix =
			new JNameExpression(where, registryPrefix, PER_SINGLETON_INSTANCE_FIELD);
		
		/* "*.$deploy(this, thread)" expression */
		JExpression[] deployArgs =
		{
			new JThisExpression(where),
			new JNameExpression(where, "thread")
		};
		
		JExpression deployCall = 
			new JMethodCallExpression(where, singletonPrefix, DEPLOY_METHOD, deployArgs);
		
		/* "super.$deploySelf(thread)" expression */
		JExpression superPrefix = new JSuperExpression(where);
		
		JExpression[] superArgs =
		{
			new JNameExpression(where, "thread")
		};
		
		JExpression superCall = 
			new JMethodCallExpression(where, superPrefix, DEPLOY_SELF_METHOD, superArgs);

		/* create method body */
		JStatement[] body = { 
				new JExpressionStatement(where, deployCall, null),
				new JExpressionStatement(where, superCall, null)				
		};
		
		/* create method declaration */
		CType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JFormalParameter[] params =
		{
			new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
					threadType,
					"thread",
					false)
		};

		return new CjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				typeFactory.getVoidType(),
				DEPLOY_SELF_METHOD,
				params,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}
	
	/**
	 * Creates the $undeploySelf() method for aspect class.
	 * 
	 * public synchronized void $undeploySelf(Thread thread)
     * {
 	 *     Registry.ajc.perSingletonInstance.$undeploy();
 	 *     super.$undeploySelf();
     * }
 	 *
 	 **/
	private JMethodDeclaration createUndeploySelfMethod() {

		/* "Registry.ajc.perSingletonInstance" expression */
		JExpression registryPrefix = 
			new JTypeNameExpression(where, 
				new CClassNameType(qualifiedSingletonAspectName)
			);

		JExpression singletonPrefix =
			new JNameExpression(where, registryPrefix, PER_SINGLETON_INSTANCE_FIELD);
		
		/* "*.$undeploy()" expression */
		JExpression deployCall = 
			new JMethodCallExpression(where, singletonPrefix, UNDEPLOY_METHOD, JExpression.EMPTY);
		
		/* "super.$undeploySelf()" expression */
		JExpression superPrefix = new JSuperExpression(where);
				
		JExpression superCall = 
			new JMethodCallExpression(where, superPrefix, UNDEPLOY_SELF_METHOD, JExpression.EMPTY);

		/* create method body */
		JStatement[] body = { 
				new JExpressionStatement(where, deployCall, null),
				new JExpressionStatement(where, superCall, null)				
		};
		
		/* create method declaration */
		return new CjMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				typeFactory.getVoidType(),
				UNDEPLOY_SELF_METHOD,
				JFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	/**
	 * Creates a class, that handles mulitple deployed instances of the same class.
	 */
	public CjClassDeclaration createMultiInstanceAspectClass() {

		CClassNameType stackType = new CClassNameType("java/util/Stack");

		JExpression stackInit =
			new JUnqualifiedInstanceCreation(
				where,
				stackType,
				JExpression.EMPTY);

		JVariableDefinition deployedInstances =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				stackType,
				DEPLOYED_INSTANCES,
				stackInit);

		JVariableDefinition deploymentThread =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				new CClassNameType(QUALIFIED_THREAD_CLASS),
				DEPLOYMENT_THREAD,
				null);

		JFieldDeclaration[] fields = new JFieldDeclaration[2];

		fields[0] =
			new JFieldDeclaration(where, deployedInstances, true, null, null);
		fields[0].setGenerated();
		
		fields[1] =
			new JFieldDeclaration(where, deploymentThread, true, null, null);
		fields[0].setGenerated();

		List methods = new ArrayList();

		methods.add(createMultiInstanceDeployMethod());
		methods.add(createMultiInstanceUndeployMethod());
		methods.add(createSetDeploymentThreadMethod());
		methods.add(createGetDeploymentThreadMethod());
		methods.add(createMultiInstanceGetDeployedInstancesMethod());
		methods.add(createGetThreadLocalDeployedInstancesMethod());

		CjAdviceDeclaration[] adviceMethods = aspectClass.getAdvices();
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
		
		int modifiers = 0;
		// class must be static if it is not outer class
		if (aspectClass.getOwner() != null) 
		{
			modifiers = ACC_STATIC;
		}

		CjClassDeclaration multiInstanceAspectClass =
			new CjDeploymentSupportClassDeclaration(
				where,
				modifiers,
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
	private CjMethodDeclaration createMultiInstanceAdviceMethod(CjAdviceDeclaration advice) {
		JStatement[] statements =
			{ createMultiInstanceAdviceStatement_1(advice)};

		JBlock body = new JBlock(where, statements, null);

		CjMethodDeclaration adviceMethod =
			new CjMethodDeclaration(
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
	private JStatement createMultiInstanceAdviceStatement_1(CjAdviceDeclaration advice) {
		JExpression left =
			new JMethodCallExpression(
				where,
				null,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		CReferenceType threadType = new CClassNameType("java/lang/Thread");
		JExpression prefix = new JTypeNameExpression(where, threadType);
		JExpression right =
			new JMethodCallExpression(
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
	private JStatement createMultiInstanceAdviceStatement_1_1(CjAdviceDeclaration advice) {
		JExpression prefix =
			new JMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression initializer =
			new JMethodCallExpression(
				where,
				prefix,
				"iterator",
				JExpression.EMPTY);

		CType iteratorType = new CClassNameType(QUALIFIED_ITERATOR_CLASS);

		JVariableDefinition var =
			new JVariableDefinition(
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
	private JStatement createMultiInstanceAdviceStatement_1_2(CjAdviceDeclaration advice) {
		JExpression prefix = new JNameExpression(where, null, "iterator");

		JExpression cond =
			new JMethodCallExpression(
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
	private JStatement createMultiInstanceAdviceStatement_1_2_1(CjAdviceDeclaration advice) {

		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);

		JExpression prefix = new JNameExpression(where, null, "iterator");

		JExpression expr =
			new JMethodCallExpression(
				where,
				prefix,
				"next",
				JExpression.EMPTY);

		JExpression initializer = new JCastExpression(where, expr, ifcType);

		JVariableDefinition var =
			new JVariableDefinition(
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
	private JStatement createMultiInstanceAdviceStatement_1_2_2(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new JNameExpression(where, params[i].getIdent());
		}

		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression expr =
			new JMethodCallExpression(where, prefix, advice.getIdent(), args);

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
	private JStatement createMultiInstanceAdviceStatement_1_3(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		List args = new ArrayList();

		for (int i = 0; i < params.length; i++) {
			args.add(new JNameExpression(where, params[i].getIdent()));
		}
		//		args.add(new JNameExpression(where, DEPLOYED_INSTANCES));
		args.add(
			new JMethodCallExpression(
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
	private JStatement createMultiInstanceAdviceStatement_1_4(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new JNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new JMethodCallExpression(
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

	private CjMethodDeclaration createDoAroundMethod(CjAdviceDeclaration advice) {
		JFormalParameter[] adviceParams = advice.getParameters();

		JFormalParameter[] params =
			new JFormalParameter[advice.getParameters().length + 1];

		for (int i = 0; i < adviceParams.length; i++) {
			params[i] =
				new JFormalParameter(
					where,
					JFormalParameter.DES_PARAMETER,
					adviceParams[i].getType(),
					adviceParams[i].getIdent(),
					false);
		}

		CType stack = new CClassNameType("java/util/Stack");
		params[adviceParams.length] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
				stack,
				"stack",
				false);

		JStatement[] body = { createAdviceDoAroundMethodStatement_0(advice)};

		return new CjMethodDeclaration(
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
	private JStatement createAdviceDoAroundMethodStatement_0(CjAdviceDeclaration advice) {
		JNameExpression list = new JNameExpression(where, null, "stack");
		JExpression cond =
			new JMethodCallExpression(where, list, "empty", JExpression.EMPTY);

		JStatement thenClause = createAdviceDoAroundMethodStatement_1(advice);
		JStatement elseClause = createAdviceDoAroundMethodStatement_2(advice);

		return new JIfStatement(where, cond, thenClause, elseClause, null);
	}

	private JStatement createAdviceDoAroundMethodStatement_1(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new JNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new JMethodCallExpression(
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
	private JStatement createAdviceDoAroundMethodStatement_2(CjAdviceDeclaration advice) {
		JExpression list = new JNameExpression(where, null, "stack");
		JFormalParameter[] params = advice.getParameters();

		JExpression[] getArgs = { new JIntLiteral(where, 0)};

		JExpression get =
			new JMethodCallExpression(where, list, "get", getArgs);

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
				args.add(new JNameExpression(where, params[i].getIdent()));
			}

		}

		JExpression aroundCall =
			new JMethodCallExpression(
				where,
				new JCastExpression(
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
	private CjMethodDeclaration createMultiInstanceGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new JNameExpression(where, DEPLOYED_INSTANCES),
					null)};

		return new CjMethodDeclaration(
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
	private CjMethodDeclaration createMultiInstanceDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JFormalParameter[] params =
			{
				 new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false)};

		JStatement[] statements = { createMultiInstanceDeployStatement_1()};

		JBlock body = new JBlock(where, statements, null);

		CjMethodDeclaration deployMethod =
			new CjMethodDeclaration(
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
			new JFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression prefix =
			new JNameExpression(where, null, INSTANCE_TO_DEPLOY);

		JExpression right =
			new JMethodCallExpression(
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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);

		JExpression[] args = new JExpression[1];
		args[0] = new JNameExpression(where, null, INSTANCE_TO_DEPLOY);

		body[0] =
			new JExpressionStatement(
				where,
				new JMethodCallExpression(where, prefix, "push", args),
				null);

		body[1] =
			new JReturnStatement(where, new JThisExpression(where), null);

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

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JExpression initializer =
			new JUnqualifiedInstanceCreation(
				where,
				multiThreadType,
				JExpression.EMPTY);

		JVariableDefinition localVarDef =
			new JVariableDefinition(
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
		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression[] args = { new JThisExpression(where)};

		JExpression methodCall =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

		return new JExpressionStatement(where, methodCall, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * aspectInstance._deploy(instanceToDeploy);
	 */
	private JStatement createMultiInstanceDeployStatement_1_2_3() {
		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression[] args =
			{ new JNameExpression(where, null, INSTANCE_TO_DEPLOY)};

		JExpression methodCall =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

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
			new JNameExpression(where, null, ASPECT_INSTANCE),
			null);

	}

	/**
	 * Creates the undeploy method for multi instance aspects.
	 */
	private CjMethodDeclaration createMultiInstanceUndeployMethod() {
		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JStatement[] statements =
			{
				createMultiInstanceUndeployStatement_1(),
				createMultiInstanceUndeployStatement_2(),
				createMultiInstanceUndeployStatement_3()};

		JBlock body = new JBlock(where, statements, null);

		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			ifcType,
			UNDEPLOY_METHOD,
			JFormalParameter.EMPTY,
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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
		JExpression methodCall =
			new JMethodCallExpression(where, prefix, "pop", JExpression.EMPTY);

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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);

		JExpression left =
			new JMethodCallExpression(
				where,
				prefix,
				"size",
				JExpression.EMPTY);

		JExpression right = new JIntLiteral(where, 2);

		JExpression cond =
			new JRelationalExpression(where, OPE_LT, left, right);

		JMethodCallExpression pop =
			new JMethodCallExpression(where, prefix, "pop", JExpression.EMPTY);

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JExpression cast = new JCastExpression(where, pop, ifcType);

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
		return new JReturnStatement(where, new JThisExpression(where), null);
	}

	/**
	 * Creates the multi thread aspect class which is needed for aspect deployment
	 * out of different threads.
	 */
	public CjClassDeclaration createMultiThreadAspectClass() {

		CReferenceType hashMapType =
			new CClassNameType("java/util/WeakHashMap");

		JExpression initializer =
			new JUnqualifiedInstanceCreation(
				where,
				hashMapType,
				JExpression.EMPTY);

		CType mapType = new CClassNameType("java/util/WeakHashMap");

		JVariableDefinition var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				mapType,
				PER_THREAD_DEPLOYED_INSTANCES,
				initializer);

		JFieldDeclaration perThreadMap =
			new JFieldDeclaration(where, var, true, null, null);
		perThreadMap.setGenerated();
		
		CType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JVariableDefinition deploymentThreadVar =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				threadType,
				DEPLOYMENT_THREAD,
				new JNullLiteral(where));

		JFieldDeclaration deploymentThread =
			new JFieldDeclaration(
				where,
				deploymentThreadVar,
				true,
				null,
				null);
		deploymentThread.setGenerated();
		JFieldDeclaration[] fields = { perThreadMap, deploymentThread };

		List methods = new ArrayList();

		methods.add(createMultiThreadDeployMethod());
		methods.add(createMultiThreadUndeployMethod());
		methods.add(createSetDeploymentThreadMethod());
		methods.add(createGetDeploymentThreadMethod());
		methods.add(createMultiThreadGetDeployedInstancesMethod());
		methods.add(createMultiThreadGetThreadLocalDeployedInstancesMethod());

		CjAdviceDeclaration[] adviceMethods = aspectClass.getAdvices();
		for (int i = 0; i < adviceMethods.length; i++) {
			methods.add(createMultiThreadAdviceMethod(adviceMethods[i]));
		}

		CReferenceType[] interfaces =
			{ new CClassNameType(qualifiedAspectInterfaceName)};

		JPhylum[] initializers = { perThreadMap };

		int modifiers = 0;
		// class must be static if it is not outer class
		if (aspectClass.getOwner() != null) 
		{
			modifiers = ACC_STATIC;
		}
		
		CjClassDeclaration multiThreadClassDeclaration =
			new CjDeploymentSupportClassDeclaration(
				where,
				modifiers,
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
	private CjMethodDeclaration createMultiThreadAdviceMethod(CjAdviceDeclaration advice) {
		JStatement[] statements =
			{
				createMultiThreadAdviceStatement_1(advice),
				createMultiThreadAdviceStatement_2(advice)};

		JBlock body = new JBlock(where, statements, null);

		CjMethodDeclaration adviceMethod =
			new CjMethodDeclaration(
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
	private JStatement createMultiThreadAdviceStatement_1(CjAdviceDeclaration advice) {

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression threadPrefix = new JTypeNameExpression(where, threadType);
		JExpression[] args =
			{
				 new JMethodCallExpression(
					where,
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};

		JExpression prefix =
			new JMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression getMethodCall =
			new JMethodCallExpression(where, prefix, "get", args);

		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);

		JExpression initializer =
			new JCastExpression(where, getMethodCall, ifcType);

		JVariableDefinition var =
			new JVariableDefinition(
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
	private JStatement createMultiThreadAdviceStatement_2(CjAdviceDeclaration advice) {

		JExpression left = new JNameExpression(where, null, ASPECT_INSTANCE);

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
	private JStatement createMultiThreadAdviceStatement_2_1(CjAdviceDeclaration advice) {
		JExpression[] args = new JExpression[advice.getParameters().length];
		for (int i = 0; i < advice.getParameters().length; i++) {

			args[i] =
				new JNameExpression(
					where,
					null,
					advice.getParameters()[i].getIdent());

		}

		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression expr =
			new JMethodCallExpression(where, prefix, advice.getIdent(), args);

		if (advice.getReturnType() == typeFactory.getVoidType()) {
			return new JExpressionStatement(where, expr, null);
		} else {
			return new JReturnStatement(where, expr, null);
		}
	}

	private JStatement createMultiThreadAdviceStatement_2_2(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();

		List args = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			args.add(new JNameExpression(where, params[i].getIdent()));
		}

		CReferenceType singletonType =
			new CClassNameType(qualifiedSingletonAspectName);

		JExpression proceedCallExpr =
			new JMethodCallExpression(
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

	private CjMethodDeclaration createMultiThreadGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new JNameExpression(
						where,
						null,
						PER_THREAD_DEPLOYED_INSTANCES),
					null)};

		return new CjMethodDeclaration(
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
	 * 
	 * public synchronized Deployable $deploy(Deployable aspectToDeploy)
	 * {
	 *    Thread thread = aspectToDeploy.$getDeploymentThread();
	 *    Deployable $aspectInstance = (Deployable)$perThreadDeployedInstances.get(thread);
	 *    if ($aspectInstance != null)
	 *    {
	 *       Deployable cont = $aspectInstance.$deploy(aspectToDeploy);
	 *       if (cont == null)
	 *       {
	 *          cont = new MultiInstanceContainer();
	 *			cont.setDeploymentThread(thread);
	 *			cont.$deploy($aspectInstance);
	 *			cont.$deploy(aspectToDeploy);
	 * 		 }
	 *		 $perThreadDeployedInstances.put(thread, cont);
	 *    }
	 *    else
	 *    {
	 *       $perThreadDeployedInstances.put(thread, aspectToDeploy);
	 *    }
	 *    return this;
	 * }
	 */
	private CjMethodDeclaration createMultiThreadDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JFormalParameter[] params =
			{
				 new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false)};

		JStatement[] statements =
		{
			createMultiThreadDeployStatement_1(),
			createMultiThreadDeployStatement_2(),
			createMultiThreadDeployStatement_3(),
			createMultiThreadDeployStatement_4()
		};

		JBlock body = new JBlock(where, statements, null);

		CjMethodDeclaration deployMethod =
			new CjMethodDeclaration(
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
	 *  Thread thread = aspectToDeploy.$getDeploymentThread();
	 */
	private JStatement createMultiThreadDeployStatement_1() 
	{
		JExpression prefix =
			new JNameExpression(where, null, INSTANCE_TO_DEPLOY);

		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);
		
		JExpression initializer =
			new JMethodCallExpression(
					where,
					prefix,
					GET_DEPLOYMENT_THREAD_METHOD,
					JExpression.EMPTY);

		JVariableDefinition var =
			new JVariableDefinition(
				where,
				0,
				type,
				"thread",
				initializer);

		return new JVariableDeclarationStatement(where, var, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * Deployable $aspectInstance = (Deployable)$perThreadDeployedInstances.get(thread);
	 */
	private JStatement createMultiThreadDeployStatement_2() 
	{
		JExpression args[] = {
			new JNameExpression(where, null, "thread")
		};

		JExpression prefix =
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression getMethodCall =
			new JMethodCallExpression(where, prefix, "get", args);

		CType type = new CClassNameType(CAESAR_DEPLOYABLE_IFC);
		JExpression initializer =
			new JCastExpression(where, getMethodCall, type);

		JVariableDefinition var =
			new JVariableDefinition(
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
	 * if (aspectInstance != null)
	 * 		statement1;
	 * else 
	 * 		statement2
	 */
	private JStatement createMultiThreadDeployStatement_3() 
	{
		JExpression left = new JNameExpression(where, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		return new JIfStatement(
			where,
			cond,
			createMultiThreadDeployStatement_3_1(),
			createMultiThreadDeployStatement_3_2(),
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 * 		statement1;
	 * 		statement2;
	 * 	 	statement3;
	 * }
	 */
	private JStatement createMultiThreadDeployStatement_3_1() 
	{
		JStatement[] body =
		{
            createMultiThreadDeployStatement_3_1_1(),
			createMultiThreadDeployStatement_3_1_2(),
			createMultiThreadDeployStatement_3_1_3(),
		};

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the follwing statement:
	 *
	 * 	Deployable cont = $aspectInstance.$deploy(aspectToDeploy);
	 */
	private JStatement createMultiThreadDeployStatement_3_1_1() 
	{
		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression[] args = { 
			new JNameExpression(where, INSTANCE_TO_DEPLOY) 
		};
		
		JExpression initializer =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

		CType type = new CClassNameType(CAESAR_DEPLOYABLE_IFC);
		
		JVariableDefinition var =
			new JVariableDefinition(
				where,
				0,
				type,
				"cont",
				initializer);

		return new JVariableDeclarationStatement(where, var, null);
	}
	
	/**
	 * Returns the following statement:
	 * 
	 * if (cont == null)
	 * {
	 *    statement1;
	 *    statement2;
	 *    statement3;
	 *    statement4;
	 * }
	 */
	private JStatement createMultiThreadDeployStatement_3_1_2()
	{
		JExpression left =
			new JNameExpression(where, null, "cont");

		JExpression right =
			new JNullLiteral(where);

		JExpression cond = new JEqualityExpression(where, true, left, right);
		
		JStatement[] block =
		{
			createMultiThreadDeployStatement_3_1_2_1(),
			createMultiThreadDeployStatement_3_1_2_2(),
			createMultiThreadDeployStatement_3_1_2_3(),
			createMultiThreadDeployStatement_3_1_2_4()
		};

		return new JIfStatement(
			where,
			cond,
			new JBlock(where, block, null),
			null,
			null);
	}
	
	/**
	 * Returns the following statement:
	 * 
	 *  cont = new MultiInstanceContainer();
	 */
	private JStatement createMultiThreadDeployStatement_3_1_2_1() 
	{
		JExpression left = new JNameExpression(where, null, "cont");

		CReferenceType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		JExpression right =
			new JUnqualifiedInstanceCreation(
				where,
				multiInstanceType,
				JExpression.EMPTY);

		JExpression expr = new JAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Return the following statement:
	 * 
	 * cont.setDeploymentThread(thread);
	 */
	private JStatement createMultiThreadDeployStatement_3_1_2_2() 
	{
		JExpression[] args =
			{ new JNameExpression(where, null, "thread")};

		JExpression prefix = new JNameExpression(where, null, "cont");
		JExpression expr =
			new JMethodCallExpression(
				where,
				prefix,
				SET_DEPLOYMENT_THREAD_METHOD,
				args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * cont.deploy($deployedInstances);
	 */
	private JStatement createMultiThreadDeployStatement_3_1_2_3() 
	{
		JExpression[] args = { new JNameExpression(where, ASPECT_INSTANCE) };
		JExpression prefix = new JNameExpression(where, null, "cont");
		JExpression expr =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * cont.$deploy(aspectToDeploy);
	 */
	private JStatement createMultiThreadDeployStatement_3_1_2_4() 
	{
		JExpression[] args =
			{ new JNameExpression(where, null, INSTANCE_TO_DEPLOY)};
		JExpression prefix = new JNameExpression(where, null, "cont");
		JExpression expr =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}
	
	/**
	 * Returns the following statement:
	 * 
	 * 	$perThreadDeployedInstances.put(thread, cont);	
	 */
	private JStatement createMultiThreadDeployStatement_3_1_3() 
	{
		JExpression[] args = {
			new JNameExpression(where, "thread"),
			new JNameExpression(where, "cont")
		};

		JExpression prefix =
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new JMethodCallExpression(where, prefix, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * perThreadDeployedInstances.put(instanceToDeploy.getDeploymentThread(), instanceToDeploy);
	 */
	private JStatement createMultiThreadDeployStatement_3_2() {

		JExpression prefix =
			new JNameExpression(where, null, INSTANCE_TO_DEPLOY);
		JExpression methodCall =
			new JMethodCallExpression(
				where,
				prefix,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);
		JExpression[] args =
			{
				methodCall,
				new JNameExpression(where, null, INSTANCE_TO_DEPLOY)};

		JExpression map =
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr = new JMethodCallExpression(where, map, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return this;
	 */
	private JStatement createMultiThreadDeployStatement_4() {
		return new JReturnStatement(where, new JThisExpression(where), null);
	}

	/**
	 * Creates the undeploy method for the multi thread aspect.
	 */
	private CjMethodDeclaration createMultiThreadUndeployMethod() {
		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JStatement[] statements =
			{
				createMultiThreadUndeployStatement_1(),
				createMultiThreadUndeployStatement_2(),
				createMultiThreadUndeployStatement_3(),
				createMultiThreadUndeployStatement_4()};

		JBlock body = new JBlock(where, statements, null);

		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			ifcType,
			UNDEPLOY_METHOD,
			JFormalParameter.EMPTY,
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
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		CReferenceType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);
		JExpression thread = new JTypeNameExpression(where, threadType);

		JExpression[] args =
			{
				 new JMethodCallExpression(
					where,
					thread,
					"currentThread",
					JExpression.EMPTY)};

		JExpression getMethodCall =
			new JMethodCallExpression(where, prefix, "get", args);

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JExpression initializer =
			new JCastExpression(where, getMethodCall, ifcType);

		JVariableDefinition var =
			new JVariableDefinition(
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

		JExpression left = new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression cond =
			new JEqualityExpression(
				where,
				false,
				left,
				new JNullLiteral(where));

		JExpression assignmentRight =
			new JMethodCallExpression(
				where,
				left,
				UNDEPLOY_METHOD,
				JExpression.EMPTY);

		JExpression expr =
			new JAssignmentExpression(where, left, assignmentRight);

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
		JExpression left = new JNameExpression(where, null, ASPECT_INSTANCE);

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
			new JMethodCallExpression(
				where,
				threadClass,
				"currentThread",
				JExpression.EMPTY);

		JExpression[] args = { methodCall };

		JExpression prefix =
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new JMethodCallExpression(where, prefix, "remove", args);

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
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression left =
			new JMethodCallExpression(
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
			new JMethodCallExpression(
				where,
				prefix,
				"values",
				JExpression.EMPTY);

		JExpression iteratorCall =
			new JMethodCallExpression(
				where,
				entrySetCall,
				"iterator",
				JExpression.EMPTY);

		JExpression returnExpr =
			new JMethodCallExpression(
				where,
				iteratorCall,
				"next",
				JExpression.EMPTY);

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JExpression castExpr = new JCastExpression(where, returnExpr, ifcType);

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
			new JMethodCallExpression(
				where,
				threadPrefix,
				"currentThread",
				JExpression.EMPTY);

		JExpression[] args =
			{ methodCall, new JNameExpression(where, null, ASPECT_INSTANCE)};

		JExpression prefix =
			new JFieldAccessExpression(where, PER_THREAD_DEPLOYED_INSTANCES);

		JExpression expr =
			new JMethodCallExpression(where, prefix, "put", args);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * return this;
	 */
	private JStatement createMultiThreadUndeployStatement_4() {
		return new JReturnStatement(where, new JThisExpression(where), null);
	}

	/**
	 * Creates the getDeploymentThread method for all implementors
	 * of the aspect interface.
	 */
	private JMethodDeclaration createGetDeploymentThreadMethod() {
		JExpression fieldExpr =
			new JFieldAccessExpression(where, DEPLOYMENT_THREAD);
		JStatement[] body = { new JReturnStatement(where, fieldExpr, null)};

		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);

		return 
			new JMethodDeclaration(
				where,
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				CTypeVariable.EMPTY,
				type,
				GET_DEPLOYMENT_THREAD_METHOD,
				JFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	/**
	 * Creates the setDeploymentThread method for all implementors
	 * of the aspect interface.
	 */
	private JMethodDeclaration createSetDeploymentThreadMethod() {
		CType type = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JExpression fieldExpr =
			new JFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression left =
			new JFieldAccessExpression(where, DEPLOYMENT_THREAD);

		JExpression right =
			new JNameExpression(where, null, DEPLOYMENT_THREAD);

		JExpression assignment = new JAssignmentExpression(where, left, right);

		JStatement[] body =
			{ new JExpressionStatement(where, assignment, null)};

		JFormalParameter[] params =
			{
				 new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
					type,
					DEPLOYMENT_THREAD,
					false)};

		return 
			new JMethodDeclaration(
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
	public CjClassDeclaration createSingletonAspect() {
		CjAdviceDeclaration[] advices = aspectClass.getAdvices();
		JMethodDeclaration[] methods = aspectClass.getMethods();
		List fields = new ArrayList();

		List singletonAspectMethods = new ArrayList();
		JMethodDeclaration[] aspectClassMethods =
			new JMethodDeclaration[methods.length + advices.length];

		CjAdviceDeclaration[] modifiedAdvices =
			new CjAdviceDeclaration[advices.length];

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
		JVariableDefinition deployedInstancesVar =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				ifcType,
				DEPLOYED_INSTANCES,
				null);

		JFieldDeclaration field = new JFieldDeclaration(
									where,
									deployedInstancesVar,
									true,
									null,
									null);
		field.setGenerated(); 
		fields.add( field );

		if (!CModifier.contains(aspectClass.getModifiers(), ACC_ABSTRACT)) {
			singletonAspectMethods.add(createSingletonAjcClinitMethod());
			singletonAspectMethods.add(createAspectOfMethod());
		}

		//create the ajc$perSingletonInstance field
		CType singletonType = new CClassNameType(qualifiedSingletonAspectName);
		JVariableDefinition aspectInstanceVar =
			new JVariableDefinition(
				where,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				singletonType,
				PER_SINGLETON_INSTANCE_FIELD,
				null);

		field = new JFieldDeclaration(where, aspectInstanceVar, true, null, null);
		field.setGenerated();
		fields.add(field);

		//Implement the CaesarSingletonAspectIfc
		CReferenceType[] interfaces =
			{ new CClassNameType(CAESAR_SINGLETON_ASPECT_IFC_CLASS)};

		int modifiers = aspectClass.getModifiers();
		if (aspectClass.getOwner() != null) {
			//the nested singletons need to be static
			modifiers |= ACC_STATIC;
		}
		modifiers = CModifier.notElementsOf(modifiers, ACC_DEPLOYED | ACC_MIXIN);

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
		CjClassDeclaration singletonAspect=null;
		
		
			if((!CModifier.contains(aspectClass.getModifiers(),ACC_ABSTRACT))
			){
				
				//create the aspect
				singletonAspect=
				new CjDeploymentSupportClassDeclaration(
					aspectClass.getTokenReference(),
					modifiers,
					singletonAspectName,
					CTypeVariable.EMPTY,
					null,
					interfaces,
					(JFieldDeclaration[]) fields.toArray(
						new JFieldDeclaration[0]),
					(JMethodDeclaration[]) singletonAspectMethods.toArray(
						new JMethodDeclaration[0]),
					new JTypeDeclaration[0],
					initializers,
					null,
					null,
						//in concrete Aspects, COPY the pointcuts to the singleton. 
						// Only the Pointcutresolver complains.
					//aspectClass.getPointcuts(),
					new CjPointcutDeclaration[0],
					modifiedAdvices,
					aspectClass.getDeclares(),
					aspectClass,
					REGISTRY_EXTENSION);

			singletonAspect.setPerClause(
				CaesarPointcut.createPerSingleton());

			
			//aspectClass.setPointcuts(new PointcutDeclaration[0]);
			}else{
				//create the aspect
				 singletonAspect =
				new CjDeploymentSupportClassDeclaration(
					aspectClass.getTokenReference(),
					modifiers,
					singletonAspectName,
					CTypeVariable.EMPTY,
					null,
					interfaces,
					(JFieldDeclaration[]) fields.toArray(
						new JFieldDeclaration[0]),
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

			singletonAspect.setPerClause(
				CaesarPointcut.createPerSingleton());

			
			aspectClass.setPointcuts(new CjPointcutDeclaration[0]);
			}
		
		aspectClass.setAdvices(new CjAdviceDeclaration[0]);
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
			new JMethodCallExpression(
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
	private CjAdviceDeclaration createSingletonAspectAdviceMethod(CjAdviceDeclaration advice) {
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
	private JStatement createSingletonAdviceStatement_1(CjAdviceDeclaration advice) {

		JExpression left =
			new JMethodCallExpression(
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
	private JStatement createSingletonAdviceStatement_1_1(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();
		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new JNameExpression(where, params[i].getIdent());
		}

		JExpression prefix =
			new JCastExpression(
				where,
				new JMethodCallExpression(
					where,
					null,
					GET_DEPLOYED_INSTANCES_METHOD,
					JExpression.EMPTY),
				new CClassNameType(qualifiedAspectInterfaceName));
		JExpression expr =
			new JMethodCallExpression(where, prefix, advice.getIdent(), args);
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
	private JStatement createSingletonAdviceStatement_1_2(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getProceedParameters();
		JExpression[] args = new JExpression[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = new JNameExpression(where, params[i].getIdent());
		}

		JExpression expr =
			new JMethodCallExpression(
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

	private CjMethodDeclaration createSingletonAjcClinitMethod() {
		JStatement[] body =
			{
				createSingletonClinitMethodStatement_1(),
				createSingletonClinitMethodStatement_2()};
		return new CjMethodDeclaration(
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
			new JNameExpression(where, PER_SINGLETON_INSTANCE_FIELD);
		JExpression right =
			new JUnqualifiedInstanceCreation(
				where,
				new CClassNameType(singletonAspectName),
				JExpression.EMPTY);
		return new JExpressionStatement(
			where,
			new JAssignmentExpression(where, left, right),
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
	private CjMethodDeclaration createSingletonGetDeployedInstancesMethod() {
		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new JNameExpression(where, null, DEPLOYED_INSTANCES),
					null)};
		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			new CClassNameType(CAESAR_DEPLOYABLE_IFC),
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
	private CjMethodDeclaration createSingletonAspectDeployMethod() {

		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);
		CType threadType = new CClassNameType(QUALIFIED_THREAD_CLASS);

		JFormalParameter[] params =
			{
				new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
					ifcType,
					INSTANCE_TO_DEPLOY,
					false),
				new JFormalParameter(
					where,
					JFormalParameter.DES_GENERATED,
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
		return new CjMethodDeclaration(
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
				new JNameExpression(where, INSTANCE_TO_DEPLOY),
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
			new JNameExpression(where, prefix, "threadLocalRegistries");

		JExpression getCall =
			new JMethodCallExpression(
				where,
				threadLocalRegistries,
				"get",
				JExpression.EMPTY);

		JExpression init = new JCastExpression(where, getCall, setType);

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

		JExpression prefix = new JNameExpression(where, "registrySet");
		JExpression[] args = { new JThisExpression(where)};

		JExpression methodCall =
			new JMethodCallExpression(where, prefix, "add", args);

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
		JExpression[] args = { new JNameExpression(where, DEPLOYMENT_THREAD)};
		JExpression prefix = new JNameExpression(where, INSTANCE_TO_DEPLOY);

		JExpression expr =
			new JMethodCallExpression(
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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression varExpr =
			new JNameExpression(where, null, INSTANCE_TO_DEPLOY);
		JExpression right = new JCastExpression(where, varExpr, ifcType);
		JExpression expr = new JAssignmentExpression(where, left, right);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	* Returns the following statement:
	* 
	* Deployable $aspectInstance = $deployedInstances.$deploy($aspectToDeploy);
	* if ($aspectInstance == null)
	* {
	*    if ($deployedInstances.$getDeploymentThread() == thread)
	*    {
	*       $aspectInstance = new MultiInstanceContainer();
	*       $aspectInstance.setDeploymentThread(thread);
	*    } 
	*    else
	*    {
	*       $aspectInstance = new ThreadMapper();
	*    }
	*    $aspectInstance.deploy($deployedInstances);
	*    $aspectInstance.deploy($aspectToDeploy);
	* }
	* $deployedInstances = (Ifc)$aspectInstance;
	*/
	private JStatement createSingletonDeployStatement_4_2() 
	{
		JStatement[] block =
		{
			createSingletonDeployStatement_4_2_1(),
			createSingletonDeployStatement_4_2_2(),
			createSingletonDeployStatement_4_2_3()			
		};
		
		return new JBlock(where, block, null);
	}
	
	/**
	* Returns the following statement:
	* 
	* Deployable $aspectInstance = $deployedInstances.$deploy($aspectToDeploy);
	*/
	private JStatement createSingletonDeployStatement_4_2_1()
	{
		CType ifcType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);
		
		JExpression deployedInstancesField =
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
		
		JExpression[] args =
			{ new JNameExpression(where, null, INSTANCE_TO_DEPLOY)};
		
		JExpression init =
			new JMethodCallExpression(
				where,
				deployedInstancesField,
				DEPLOY_METHOD,
				args);
		
		JVariableDefinition var =
			new JVariableDefinition(where, 0, ifcType, ASPECT_INSTANCE, init);

		return new JVariableDeclarationStatement(where, var, null);		
	}
	
	/**
	* Returns the following statement:
	* 
	* if ($aspectInstance == null)
	* {
	*    statement1;
	*    statement2;
	*    statement3;
	* }
	*/
	private JStatement createSingletonDeployStatement_4_2_2()
	{
		JExpression left =
			new JNameExpression(where, null, ASPECT_INSTANCE);

		JExpression right =
			new JNullLiteral(where);

		JExpression cond = new JEqualityExpression(where, true, left, right);
		
		JStatement[] block =
		{
			createSingletonDeployStatement_4_2_2_1(),
			createSingletonDeployStatement_4_2_2_2(),
			createSingletonDeployStatement_4_2_2_3()			
		};

		return new JIfStatement(
			where,
			cond,
			new JBlock(where, block, null),
			null,
			null);
	}
	
	/**
	* Returns the following statement:
	* 
	*    if ($deployedInstances.$getDeploymentThread() == thread)
	*    {
	*       statement1;
	*    } 
	*    else
	*    {
	*       statement2;
	*    }
	*/
	private JStatement createSingletonDeployStatement_4_2_2_1() 
	{
		JExpression prefix =
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);

		JExpression left =
			new JMethodCallExpression(
				where,
				prefix,
				GET_DEPLOYMENT_THREAD_METHOD,
				JExpression.EMPTY);

		JExpression right =
			new JNameExpression(where, null, DEPLOYMENT_THREAD);
		
		JExpression cond = new JEqualityExpression(where, true, left, right);

		return new JIfStatement(
			where,
			cond,
			createSingletonDeployStatement_4_2_2_1_1(),
			createSingletonDeployStatement_4_2_2_1_2(),
			null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * {
	 *    statement1;
	 *    statement2; 
	 * }
	 */
	private JStatement createSingletonDeployStatement_4_2_2_1_1() 
	{
		JStatement[] body =
		{
			createSingletonDeployStatement_4_2_2_1_1_1(),
			createSingletonDeployStatement_4_2_2_1_1_2()
		};

		return new JBlock(where, body, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * $aspectInstance = new MultiInstanceAspect();
	 */
	private JStatement createSingletonDeployStatement_4_2_2_1_1_1() 
	{
		JExpression left = new JNameExpression(where, null, ASPECT_INSTANCE);

		CReferenceType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		JExpression right =
			new JUnqualifiedInstanceCreation(
				where,
				multiInstanceType,
				JExpression.EMPTY);

		JExpression expr = new JAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Return the following statement:
	 * 
	 * $aspectInstance.setDeploymentThread(deploymentThread);
	 */
	private JStatement createSingletonDeployStatement_4_2_2_1_1_2() 
	{
		JExpression[] args =
			{ new JNameExpression(where, null, DEPLOYMENT_THREAD)};

		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new JMethodCallExpression(
				where,
				prefix,
				SET_DEPLOYMENT_THREAD_METHOD,
				args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * $aspectInstance = new MultiThreadAspect();
	 */
	private JStatement createSingletonDeployStatement_4_2_2_1_2() 
	{
		JExpression left = new JNameExpression(where, null, ASPECT_INSTANCE);
		CReferenceType type =
			new CClassNameType(qualifiedMultiThreadAspectClassName);
		JExpression right =
			new JUnqualifiedInstanceCreation(where, type, JExpression.EMPTY);
		JExpression expr = new JAssignmentExpression(where, left, right);

		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * $aspectInstance.deploy($deployedInstances);
	 */
	private JStatement createSingletonDeployStatement_4_2_2_2() {
		JExpression[] args = { new JFieldAccessExpression(where, DEPLOYED_INSTANCES) };
		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * $aspectInstance.deploy($aspectToDeploy);
	 */
	private JStatement createSingletonDeployStatement_4_2_2_3() {
		JExpression[] args =
			{ new JNameExpression(where, null, INSTANCE_TO_DEPLOY)};
		JExpression prefix = new JNameExpression(where, null, ASPECT_INSTANCE);
		JExpression expr =
			new JMethodCallExpression(where, prefix, DEPLOY_METHOD, args);
		return new JExpressionStatement(where, expr, null);
	}
	
	/**
	 * Returns the following statement:
	 * 
	 * $deployedInstances = (Ifc)$aspectInstance;
	 */
	private JStatement createSingletonDeployStatement_4_2_3()
	{
		JExpression left =
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression varExpr =
			new JNameExpression(where, null, ASPECT_INSTANCE);
		JExpression right = new JCastExpression(where, varExpr, ifcType);
		JExpression expr = new JAssignmentExpression(where, left, right);
		return new JExpressionStatement(where, expr, null);
	}

	/**
	* Creates the undeploy method for singleton aspect.
	*/
	private CjMethodDeclaration createSingletonAspectUndeployMethod() {

		JStatement[] body = { createSingletonUndeployStatement_2()};
		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_SYNCHRONIZED,
			CTypeVariable.EMPTY,
			typeFactory.getVoidType(),
			UNDEPLOY_METHOD,
			JFormalParameter.EMPTY,
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
			new JFieldAccessExpression(where, DEPLOYED_INSTANCES);
		CType ifcType = new CClassNameType(qualifiedAspectInterfaceName);
		JExpression right =
			new JCastExpression(
				where,
				new JMethodCallExpression(
					where,
					left,
					UNDEPLOY_METHOD,
					JExpression.EMPTY),
				ifcType);
		JExpression expr = new JAssignmentExpression(where, left, right);
		return new JExpressionStatement(where, expr, null);
	}

	private CjMethodDeclaration createAspectOfMethod() {

		CType singletonType = new CClassNameType(qualifiedSingletonAspectName);
		JExpression expr =
			new JFieldAccessExpression(
				where,
				null,
				PER_SINGLETON_INSTANCE_FIELD);
		JStatement[] body = { new JReturnStatement(where, expr, null)};
		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_STATIC,
			CTypeVariable.EMPTY,
			singletonType,
			ASPECT_OF_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	private JMethodDeclaration createGetThreadLocalDeployedInstancesMethod() {

		CType deployableType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JStatement[] body =
			{ new JReturnStatement(where, new JThisExpression(where), null)};

		return 
			new JMethodDeclaration(
				where,
				ACC_PUBLIC,
				CTypeVariable.EMPTY,
				deployableType,
				GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
				JFormalParameter.EMPTY,
				CReferenceType.EMPTY,
				new JBlock(where, body, null),
				null,
				null);
	}

	private JMethodDeclaration createMultiThreadGetThreadLocalDeployedInstancesMethod() {

		CType deployableType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);
		JExpression[] args =
			{
				 new JMethodCallExpression(
					where,
					new JTypeNameExpression(
						where,
						new CClassNameType(QUALIFIED_THREAD_CLASS)),
					"currentThread",
					JExpression.EMPTY)};

		JExpression getDeployedInstancesCall =
			new JMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression methodCall =
			new JMethodCallExpression(
				where,
				getDeployedInstancesCall,
				"get",
				args);

		JStatement[] body =
			{
				 new JReturnStatement(
					where,
					new JCastExpression(where, methodCall, deployableType),
					null)};

		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC,
			CTypeVariable.EMPTY,
			deployableType,
			GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
			JFormalParameter.EMPTY,
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

		CType deployableType = new CClassNameType(CAESAR_DEPLOYABLE_IFC);

		JStatement[] body =
			{ createSingletonGetThreadLocalDeployedInstancesStatement_1()};

		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC,
			CTypeVariable.EMPTY,
			deployableType,
			GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(where, body, null),
			null,
			null);
	}

	private JStatement createSingletonGetThreadLocalDeployedInstancesStatement_1() {
		JExpression left =
			new JMethodCallExpression(
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
			new JMethodCallExpression(
				where,
				null,
				GET_DEPLOYED_INSTANCES_METHOD,
				JExpression.EMPTY);

		JExpression expr =
			new JMethodCallExpression(
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
	private JMethodDeclaration createProceedMethod(CjAdviceDeclaration advice) {
		CjProceedDeclaration proceedMethodDeclaration =
			new CjProceedDeclaration(
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
	private void createAdviceMethodName(CjAdviceDeclaration adviceDeclaration) {
		String ident =
			CaesarNameMangler.adviceName(
				aspectClass.getCClass().getQualifiedName(),
				adviceDeclaration.getKind(),
				adviceDeclaration.getTokenReference().getLine());
		adviceDeclaration.setIdent(ident);
	}

	private CjClassDeclaration createAroundClosure(CjAdviceDeclaration advice) {
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
			JFieldDeclaration field = new JFieldDeclaration(where, var, true, null, null);
			field.setGenerated(); 
			fields.add(field);
		}

		CType stack = new CClassNameType("java/util/Stack");
		JVariableDefinition var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				stack,
				"stack",
				new JNullLiteral(where));
		JFieldDeclaration field = new JFieldDeclaration(where, var, true, null, null);
		field.setGenerated(); 
		fields.add(field);

		CType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		var =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				multiInstanceType,
				"multiInstanceContainer",
				new JNullLiteral(where));
		field = new JFieldDeclaration(where, var, true, null, null);
		field.setGenerated();
		fields.add(field);

		JMethodDeclaration[] methods =
			{ createClosureConstructor(advice), createRunMethod(advice)};

		CjClassDeclaration closure =
			new CjClassDeclaration(
				where,
				0,
				(advice.getIdent() + "$MultiInstanceAroundClosure").intern(),
				CTypeVariable.EMPTY,
				superClass,
				null,
				CReferenceType.EMPTY,
				(JFieldDeclaration[]) fields.toArray(new JFieldDeclaration[0]),
				methods,
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);

		return closure;
	}

	private JConstructorDeclaration createClosureConstructor(CjAdviceDeclaration advice) {
		JFormalParameter[] adviceParameters = advice.getParameters();

		JFormalParameter[] params =
			new JFormalParameter[adviceParameters.length + 2];

		for (int i = 0; i < adviceParameters.length; i++) {
			params[i] =
				new JFormalParameter(
					where,
					JFormalParameter.DES_PARAMETER,
					adviceParameters[i].getType(),
					adviceParameters[i].getIdent(),
					false);
		}

		CType stack = new CClassNameType("java/util/Stack");
		params[adviceParameters.length] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
				stack,
				"stack",
				false);

		CType multiInstanceType =
			new CClassNameType(qualifiedMultiInstanceAspectClassName);
		params[adviceParameters.length + 1] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
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
	private CjMethodDeclaration createRunMethod(CjAdviceDeclaration advice) {
		CReferenceType objectType = new CClassNameType("java/lang/Object");
		CArrayType arrayType = new CArrayType(objectType, 1);
		JFormalParameter[] objArray =
			{
				 new JFormalParameter(
					where,
					JFormalParameter.DES_PARAMETER,
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

		return new CjMethodDeclaration(
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

		JExpression prefix = new JNameExpression(where, null, "stack");
		JExpression init =
			new JMethodCallExpression(
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
		JExpression prefix = new JNameExpression(where, null, "clone");

		JExpression[] args = { new JIntLiteral(where, 0)};
		JExpression methodCall =
			new JMethodCallExpression(where, prefix, "remove", args);

		return new JExpressionStatement(where, methodCall, null);

	}

	/**
	 * multiInstanceContainer.doaround..();
	 */
	private JStatement createRunStatement_3(CjAdviceDeclaration advice) {
		JFormalParameter[] params = advice.getParameters();

		List args = new ArrayList();

		for (int i = 0; i < params.length; i++) {
			args.add(new JNameExpression(where, params[i].getIdent()));
		}
		args.add(new JNameExpression(where, "clone"));

		JExpression prefix =
			new JNameExpression(where, "multiInstanceContainer");

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
		CjAdviceDeclaration advice,
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
