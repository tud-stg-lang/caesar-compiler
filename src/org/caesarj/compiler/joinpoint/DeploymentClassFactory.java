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
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
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
	
	private String packagePrefix;
	private String singletonAspectName;
	private String aspectInterfaceName;
	
	/* names for AST */
	private String qualifiedAspectInterfaceName;
	private String qualifiedSingletonAspectName;
	
	/* names for source code snippets */
	private String srcAspectClassName;
	private String srcSingletonAspectName;
	private String srcAspectInterfaceName;

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

	/**
	 * Initialize generated names
	 */
	private void initNames() {
		
		String packageName = aspectClass.getCClass().getPackage();
		String qualifiedAspectClassName = aspectClass.getCClass().getQualifiedName();
		
		srcAspectClassName = qualifiedAspectClassName.replace('/', '.');
		srcAspectClassName = srcAspectClassName.replace('$', '.');
		
		this.packagePrefix = packageName.length() > 0 ? packageName + "/" : "";

		/* Initialize generated class and interface names */
		this.aspectInterfaceName =
			aspectClass.getIdent() + ASPECT_IFC_EXTENSION;
		this.qualifiedAspectInterfaceName =
			qualifiedAspectClassName + ASPECT_IFC_EXTENSION;
		this.srcAspectInterfaceName =
			srcAspectClassName + ASPECT_IFC_EXTENSION;

	    this.singletonAspectName = 
	    	aspectClass.getIdent() + REGISTRY_EXTENSION;
		this.qualifiedSingletonAspectName =
			qualifiedAspectClassName + REGISTRY_EXTENSION;
		this.srcSingletonAspectName =
			srcAspectClassName + REGISTRY_EXTENSION;

		/* Initialize advice method names */
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
			{ new CClassNameType(CAESAR_ASPECT_IFC)};

		CjInterfaceDeclaration aspectInterface =
			new CjInterfaceDeclaration(
				aspectClass.getTokenReference(),
				ACC_PUBLIC,
				aspectInterfaceName,
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
        
		//add support methods
		List newMethods = new ArrayList();
        newMethods.add(createDeploySelfMethod());
		newMethods.add(createUndeploySelfMethod());
		
		aspectClass.addMethods(
			(JMethodDeclaration[]) newMethods.toArray(
				new JMethodDeclaration[0]));
	}

	/**
	 * Create the appropriate advice method for the aspect class.
	 * That means, creates a "normal" method with the former advice body.
	 */
	private JMethodDeclaration createAspectClassAdviceMethod(CjAdviceDeclaration advice) {
		
		return new CjAdviceMethodDeclaration(
		    			advice,
						advice.getTokenReference(),
						ACC_PUBLIC | ACC_SYNCHRONIZED,
						advice.getReturnType(),
						advice.getIdent(),
						advice.getParameters(),
						advice.getExceptions(),
						advice.getBody(),
						null,
						null);
	}
	
	/**
	 * Creates the $deploySelf(..) method for aspect class.
	 */
	private JMethodDeclaration createDeploySelfMethod() {
		
	    AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public void $deploySelf(" + SRC_ASPECT_DEPLOYER_IFC + " depl)",
			"{",
 	      		"depl.$deployOn(" + srcSingletonAspectName + ".ajc$perSingletonInstance, this);",
				"super.$deploySelf(depl);",
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    return gen.endMethod();
	}
	
	/**
	 * Creates the $undeploySelf(..) method for aspect class.
	 */
	private JMethodDeclaration createUndeploySelfMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public void $undeploySelf(" + SRC_ASPECT_DEPLOYER_IFC + " depl)",
			"{",
 	      		"depl.$undeployFrom(" + srcSingletonAspectName + ".ajc$perSingletonInstance, this);",
				"super.$undeploySelf(depl);",
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    return gen.endMethod();
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
		List inners = new ArrayList();

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

			if (advices[i].isAroundAdvice()) {
				singletonAspectMethods.add(createProceedMethod(advices[i]));
				inners.add(createAroundClosure(advices[i]));
				modifiedAdvices[i] = createSingletonAspectAroundAdviceMethod(advices[i]);
			}
			else {
				modifiedAdvices[i] = createSingletonAspectAdviceMethod(advices[i]);
			}
		}

		/* create the deploy and undeploy method */
		singletonAspectMethods.add(createSingletonGetAspectContainerMethod());
		singletonAspectMethods.add(createSingletonSetAspectContainerMethod());
		
		/* create the $aspectContainer field */
		CType containerType = new CClassNameType(ASPECT_CONTAINER_IFC);
		JVariableDefinition deployedInstancesVar =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				containerType,
				ASPECT_CONTAINER_FIELD,
				null);

		JFieldDeclaration field = new JFieldDeclaration(
									where,
									deployedInstancesVar,
									true,
									null,
									null);
		field.setGenerated(); 
		fields.add( field );

		singletonAspectMethods.add(createSingletonAjcClinitMethod());
		singletonAspectMethods.add(createAspectOfMethod());
		
		// create the ajc$perSingletonInstance field
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

		// Implement the CaesarSingletonAspectIfc
		CReferenceType[] interfaces =
			{ new CClassNameType(CAESAR_ASPECT_REGISTRY_IFC_CLASS)};

		int modifiers = aspectClass.getModifiers();
		if (aspectClass.getOwner() != null) {
			//the nested singletons need to be static
			modifiers |= ACC_STATIC;
		}
		modifiers = CModifier.notElementsOf(modifiers, ACC_DEPLOYED | ACC_MIXIN);

		// create class initializer
		JPhylum[] initializers = new JPhylum[1];
		initializers[0] = createSingletonAspectClinit();
		
		// create the singleton aspect
		CjClassDeclaration singletonAspect = new CjDeploymentSupportClassDeclaration(
			aspectClass.getTokenReference(),
			modifiers,
			singletonAspectName,
			null,
			interfaces,
			(JFieldDeclaration[]) fields.toArray(
				new JFieldDeclaration[0]),
			(JMethodDeclaration[]) singletonAspectMethods.toArray(
				new JMethodDeclaration[0]),
			(JTypeDeclaration[]) inners.toArray(new JTypeDeclaration[0]),
			initializers,
			null,
			null,
			new CjPointcutDeclaration[0],
			modifiedAdvices,
			aspectClass.getDeclares(),
			aspectClass,
			REGISTRY_EXTENSION);

		singletonAspect.setPerClause(
				CaesarPointcut.createPerSingleton());
		
		aspectClass.setAdvices(new CjAdviceDeclaration[0]);
		aspectClass.setDeclares(null);
		aspectClass.setMethods(aspectClassMethods);

		singletonAspect.generateInterface(
			environment.getClassReader(),
			aspectClass.getOwner(),
			packagePrefix);

		return singletonAspect;
	}
	
	/**
	 * Creates the $getAspectContainer(..) method for registry class.
	 */
	private JMethodDeclaration createSingletonGetAspectContainerMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public " + SRC_ASPECT_CONTAINER_IFC + " $getAspectContainer()",
			"{",
 	      		"return $aspectContainer;",
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    return gen.endMethod();
	}
	
	/**
	 * Creates the $setAspectContainer(..) method for registry class.
	 */
	private JMethodDeclaration createSingletonSetAspectContainerMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public void $setAspectContainer(" + SRC_ASPECT_CONTAINER_IFC + " cont)",
			"{",
 	      		"$aspectContainer = cont;",
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    return gen.endMethod();
	}

	/**
	 * Create class initialization block for registry class 
	 */
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
		
		AstGenerator gen = environment.getAstGenerator();
		
		/* Format line for advice method call on variable 'aspObj' */
		String strAdviceMethodCall = "aspObj." + advice.getIdent() + "(";
		
		JFormalParameter[] params = advice.getParameters();
		for (int i1 = 0; i1 < params.length; i1++) {
			if (i1 > 0) {
				strAdviceMethodCall += ", ";
			}
			strAdviceMethodCall += params[i1].getIdent();			
		}
		strAdviceMethodCall += ");";
		
		/* Format advice body */
	    String[] block = new String[] {
	    	"{",
				"if ($aspectContainer != null)",
				"{",
					"java.util.Iterator it = $aspectContainer.$getInstances();",
					"if (it != null)",
					"{",
						"while (it.hasNext())",
						"{",
							srcAspectInterfaceName + " aspObj = (" + srcAspectInterfaceName + ")it.next();",
							strAdviceMethodCall,
						"}",
					"}",
				"}",
			"}"	
	    };
	    
	    gen.writeBlock(block);
	     
		JStatement[] body = gen.endBlock();
		advice.setBody(new JBlock(where, body, null));
		return advice;
	}
	
	/**
	 * Creates an AdviceDeclaration, that has the same interface
	 * as the given advice but dispatches calls the aspect instance.
	 */
	private CjAdviceDeclaration createSingletonAspectAroundAdviceMethod(CjAdviceDeclaration advice) {
	
		AstGenerator gen = environment.getAstGenerator();
		
		/* Format line for proceed call */
		String strProceedCall = "";
		
		if (advice.getReturnType() != typeFactory.getVoidType())
			strProceedCall += "return ";
	    
		strProceedCall += srcSingletonAspectName + "." + advice.getIdent() + PROCEED_METHOD + "(";
	    
	    JFormalParameter params[] = advice.getProceedParameters();
	    
	    for (int i1 = 0; i1 < params.length; i1++) { 
	    	if (i1 > 0) {
	    		strProceedCall += ", ";
			}
	    	if (params[i1].getIdent() == "aroundClosure") {
	    		strProceedCall += "$closure";
	    	}
	    	else {
	    		strProceedCall += params[i1].getIdent();
	    	}
		}
	    strProceedCall += ");";
	    
	    /* Format line for closure construction */
		String strClosureConstruction = "new " + advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION + "(";
		
		JFormalParameter adviceParams[] = advice.getParameters();
		
		for (int i1 = 0; i1 < adviceParams.length; i1++) {
			if (i1 > 0) {
				strClosureConstruction += ", ";
			}
			strClosureConstruction += adviceParams[i1].getIdent();			
		}
		strClosureConstruction += ", $it);";
	    
	    /* Format advice body */
	    String[] block = new String[] {
	    	"{",
	    		SRC_AROUND_CLOSURE_CLASS + " $closure = aroundClosure;",
				"if ($aspectContainer != null)",
				"{",
					"java.util.Iterator $it = $aspectContainer.$getInstances();",
					"if ($it != null)",
					"{",
						"$closure = " + strClosureConstruction, 
					"}",
				"}",
				strProceedCall,				
			"}",
	    };
	    
	    gen.writeBlock(block);
	     
		JStatement[] body = gen.endBlock();
		advice.setBody(new JBlock(where, body, null));
		return advice;
	}
	
	/**
	 * Creates method for registry singleton initialization
	 */
	private JMethodDeclaration createSingletonAjcClinitMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
				
		String[] body = new String[] {
			"private static void ajc$clinit()",
	    	"{",
				"ajc$perSingletonInstance = new " + srcSingletonAspectName + "();",
				"try ",
				"{",
					"java.lang.Class.forName(\"" + srcAspectClassName + "\");",
				"}",
				"catch (java.lang.ClassNotFoundException e) { }",
			"}"	
	    };
	    
	    gen.writeMethod(body);	     
		return gen.endMethod();
	}

	/**
	 * Creates aspectOf method for registry class
	 */
	private JMethodDeclaration createAspectOfMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
		
		String[] body = new String[] {
			"public static " + srcSingletonAspectName + " aspectOf()",
			"{",
				"return ajc$perSingletonInstance;",
			"}"	
		};
		
		gen.writeMethod(body);	     
		return gen.endMethod();
	}
	
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

	//also implement the other support method here, instead of in CaesarSourceClass

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
	
	/**
	  * Create around advice closure class, which is able to iterate over multiple
	  * deployed aspect instances.
	  */
	private CjClassDeclaration createAroundClosure(CjAdviceDeclaration advice) {
		
		CReferenceType superClass =
			new CClassNameType(AROUND_CLOSURE_CLASS);

		/* create field for every advice parameter */
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

		/* create iterator field to iterate over aspect objects */
		CType iterator = new CClassNameType("java/util/Iterator");
		JVariableDefinition var1 =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				iterator,
				"$iter",
				new JNullLiteral(where));
		JFieldDeclaration field1 = new JFieldDeclaration(where, var1, true, null, null);
		field1.setGenerated(); 
		fields.add(field1);
		
		/* create next call closure field */
		CType closure = new CClassNameType(advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION);
		JVariableDefinition var2 =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				closure,
				"$nextCall",
				new JNullLiteral(where));
		JFieldDeclaration field2 = new JFieldDeclaration(where, var2, true, null, null);
		field2.setGenerated(); 
		fields.add(field2);
		
		/* create aspect object instance field */
		CType aspectIfc = new CClassNameType(this.qualifiedAspectInterfaceName);
		JVariableDefinition var3 =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				aspectIfc,
				"$aspObj",
				new JNullLiteral(where));
		JFieldDeclaration field3 = new JFieldDeclaration(where, var3, true, null, null);
		field3.setGenerated(); 
		fields.add(field3);

		/* create constructor and run method */
		JMethodDeclaration[] methods = { 
				createClosureConstructor(advice), 
				createRunMethod(advice)
		};
		
		/* create closure class */
		CjClassDeclaration closureDecl =
			new CjClassDeclaration(
				where,
				0,
				(advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION).intern(),
				superClass,
				null,
				CReferenceType.EMPTY,
				(JFieldDeclaration[]) fields.toArray(new JFieldDeclaration[0]),
				methods,
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);

		return closureDecl;
	}

	/**
	 * Create constructor for the multi-instance around closure class
	 */
	private JConstructorDeclaration createClosureConstructor(CjAdviceDeclaration advice) {
		
		JFormalParameter[] adviceParameters = advice.getParameters();

		JFormalParameter[] params =
			new JFormalParameter[adviceParameters.length + 1];

		/* Add all advice method parameters to the constructor */
		for (int i = 0; i < adviceParameters.length; i++) {
			params[i] =
				new JFormalParameter(
					where,
					JFormalParameter.DES_PARAMETER,
					adviceParameters[i].getType(),
					adviceParameters[i].getIdent(),
					false);
		}

		/* Add additional iterator parameter to the constructor */
		CType iterator = new CClassNameType("java/util/Iterator");
		params[adviceParameters.length] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
				iterator,
				"$iter",
				false);

		/* Generate constructor statements, which initialize class fields with the parameter values */ 
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
		
		/* Format line for closure construction */
		String strNextClosureConstruction = "$nextCall = new " 
					+ advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION + "(";
		
		for (int i1 = 0; i1 < adviceParameters.length; i1++) {
			if (i1 > 0) {
				strNextClosureConstruction += ", ";
			}
			strNextClosureConstruction += adviceParameters[i1].getIdent();			
		}
		strNextClosureConstruction += ", $iter);";
		
		/* Generate additional initalization code */
		AstGenerator gen = environment.getAstGenerator();
		
		String[] block = new String[] {
	    	"{",
	    		"if ($iter.hasNext())",
			    "{",
					"$aspObj = (" + srcAspectInterfaceName + ")$iter.next();",
				    strNextClosureConstruction,
				"}",
				"else",
				"{",
					"$aspObj = null;",
				"}",
			"}"
	    };
	    
	    gen.writeBlock(block);
	    JStatement[] additionalStm = gen.endBlock();
	    statements.add(additionalStm[0]); /* Only one statement is generated */

		/* Create constructor declaration */
		JConstructorBlock body =
			new JConstructorBlock(
				where,
				new JConstructorCall(where, false, JExpression.EMPTY),
				(JStatement[]) statements.toArray(new JStatement[0]));

		return new JConstructorDeclaration(
			where,
			ACC_PUBLIC,
			(advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION).intern(),
			params,
			CReferenceType.EMPTY,
			body,
			null,
			null,
			typeFactory);
	}

	/**
	 * Create run(..) method for the multi-instance around closure class
	 */
	private JMethodDeclaration createRunMethod(CjAdviceDeclaration advice) {
		
		AstGenerator gen = environment.getAstGenerator();
		
		/* Format line for advice method call on variable 'aspObj' */
		String strAdviceMethodCall = "$aspObj." + advice.getIdent() + "(";
		
		int proceedParamCnt = advice.getProceedParameters().length;
		
		JFormalParameter[] params = advice.getParameters();
		for (int i1 = 0; i1 < params.length; i1++) {
			if (i1 > 0) {
				strAdviceMethodCall += ", ";
			}
			if (params[i1].getIdent() == "aroundClosure") {
				strAdviceMethodCall += "$nextCall";
			}
			else if (i1 < proceedParamCnt) { /* normal advice parameter */
				strAdviceMethodCall += castFromObject(params[i1].getType(), "arg["+i1+"]");
			}
			else { /* joinpoint reflection parameter */
				strAdviceMethodCall += params[i1].getIdent();
			}
		}
		strAdviceMethodCall += ")";
		
		if (advice.getReturnType() != typeFactory.getVoidType()) {
			if (advice.getReturnType().isPrimitive()) {
				strAdviceMethodCall = createPrimTypeWrapper(advice.getReturnType(), strAdviceMethodCall);
			}
			strAdviceMethodCall = "$retval = " + strAdviceMethodCall;
		}
		strAdviceMethodCall += ";";
		
		/* Format advice body */
		String[] body = new String[] {
			"public Object run(Object[] arg) throws Throwable",
			"{",
			 	"if ($aspObj == null)",
				"{",
		     		"return aroundClosure.run(arg);",
				"}",
				"else",
				"{",
					"Object $retval = null;",
					strAdviceMethodCall,
					"return $retval;",
				"}",
			 "}"
		};
				
		gen.writeMethod(body);
		return gen.endMethod();
	}
	
	/**
	 * Wraps the primitive value of the expr in a ReferenceType.
	 */
	private String createPrimTypeWrapper(CType type, String strExpr) {
		
		if (type instanceof CIntType) {
			return "new java.lang.Integer(" + strExpr + ")";
		}

		if (type instanceof CFloatType) {
			return "new java.lang.Float(" + strExpr + ")";
		}

		if (type instanceof CDoubleType) {
			return "new java.lang.Double(" + strExpr + ")";
		}

		if (type instanceof CByteType) {
			return "new java.lang.Byte(" + strExpr + ")";
		}

		if (type instanceof CCharType) {
			return "new java.lang.Character(" + strExpr + ")";
		}

		if (type instanceof CBooleanType) {
			return "new java.lang.Boolean(" + strExpr + ")";
		}

		if (type instanceof CLongType) {
			return "new java.lang.Long(" + strExpr + ")";
		}

		if (type instanceof CShortType) {
			return "new java.lang.Short(" + strExpr + ")";
		}

		return null;
	}
	
	/**
	 * Wraps the primitive value of the expr in a ReferenceType.
	 */
	private String castFromObject(CType type, String strExpr) {
		
		if (!type.isPrimitive()) {
			return "((" + type.toString() + ")(" + strExpr + "))";
		}
		
		if (type instanceof CIntType) {
			return "((java.lang.Integer)(" + strExpr + ")).intValue()";
		}

		if (type instanceof CFloatType) {
			return "((java.lang.Float)(" + strExpr + ")).floatValue()";
		}

		if (type instanceof CDoubleType) {
			return "((java.lang.Double)(" + strExpr + ")).doubleValue()";
		}

		if (type instanceof CByteType) {
			return "((java.lang.Byte)(" + strExpr + ")).byteValue()";
		}

		if (type instanceof CCharType) {
			return "((java.lang.Character)(" + strExpr + ")).charValue()";
		}

		if (type instanceof CBooleanType) {
			return "((java.lang.Boolean)(" + strExpr + ")).booleanValue()";
		}

		if (type instanceof CLongType) {
			return "((java.lang.Long)(" + strExpr + ")).longValue()";
		}

		if (type instanceof CShortType) {
			return "((java.lang.Short)(" + strExpr + ")).shortValue()";
		}

		return null;
	}
}
