/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: DeploymentClassFactory.java,v 1.50 2005-09-21 15:15:57 thiago Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarNameMangler;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjDeploymentSupportClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjProceedDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CArrayType;
import org.caesarj.compiler.types.CBooleanType;
import org.caesarj.compiler.types.CByteType;
import org.caesarj.compiler.types.CCharType;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CDoubleType;
import org.caesarj.compiler.types.CFloatType;
import org.caesarj.compiler.types.CIntType;
import org.caesarj.compiler.types.CLongType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CShortType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

/**
 * This factory creates the support classes for dynamic deployment.
 * 
 * @author J�rgen Hallpap
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
		
		srcAspectClassName = Utils.getClassSourceName(qualifiedAspectClassName);
		
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
	}

	/**
	 * Creates the Aspect Interface.
	 */
	public CjInterfaceDeclaration createAspectInterface(CjAdviceDeclaration[] advices) {

		CjMethodDeclaration[] methods =
			new CjMethodDeclaration[advices.length];

		for (int i = 0; i < advices.length; i++) {
			methods[i] = createInterfaceAdviceMethod(advices[i]);			
		}

		CReferenceType[] superInterfaces =
			{ typeFactory.createType(CAESAR_ASPECT_IFC, true)};

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

		/* generate export information for the new interface */
		CClass owner = aspectClass.getOwner();
		aspectInterface.generateInterface(
			environment.getClassReader(),
			owner,
			owner == null ? packagePrefix : owner.getQualifiedName() + '$');

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
			advice.getAdviceMethodIdent(),
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
				typeFactory.createType(qualifiedAspectInterfaceName, false));
        
        aspectClass.getMixinIfcDeclaration().addInterface(            
        		typeFactory.createType(CAESAR_ASPECT_IFC, true));
        
		//add support methods
		List newMethods = new ArrayList();
        newMethods.add(createGetAspectRegistryMethod());
				
		aspectClass.addMethods(
			(JMethodDeclaration[]) newMethods.toArray(
				new JMethodDeclaration[0]));		
	}
	
	/**
	 * Transform advices of aspect class to simple methods
	 */
	public void generateAdviceMethods() {
		// transform advices to methods
		CjAdviceDeclaration[] advices = aspectClass.getAdvices();
						
		List adviceMethods = new LinkedList();
		
		for (int i = 0; i < advices.length; i++) {
			// add the advice method to the aspect class
			createAdviceMethodName(advices[i]);
			adviceMethods.add(createAspectClassAdviceMethod(advices[i]));
			if (advices[i].isAroundAdvice()) {
				adviceMethods.add(createAbstractAspectClassProceedMethod(advices[i]));
			}
		}
		
		updateAspectClassMethods(adviceMethods);
	}
	
	private void updateAspectClassMethods(List newMethods) {
		
		JMethodDeclaration[] methods = aspectClass.getMethods();
		
		/* override methods with the same name */
		for (int i1 = 0; i1 < methods.length; i1++) {
			for (Iterator it = newMethods.iterator(); it.hasNext(); ) {
				JMethodDeclaration meth = (JMethodDeclaration)it.next();
				if (methods[i1].getIdent().equals(meth.getIdent())) {
					methods[i1] = meth;
					newMethods.remove(meth);
					break;
				}
			}
		}		
				
		JMethodDeclaration[] aspectClassMethods =
			new JMethodDeclaration[methods.length + newMethods.size()];

		System.arraycopy(methods, 0, 
				aspectClassMethods, newMethods.size(), methods.length);
		System.arraycopy(newMethods.toArray(new JMethodDeclaration[0]), 0, 
				aspectClassMethods, 0, newMethods.size());
				
		aspectClass.setMethods(aspectClassMethods);		
	}
	
	/**
	 *  Clean crosscutting information from original classes 
	 */
	public void cleanCrosscuttingInfo() {
		aspectClass.deactivateAdvices();
		aspectClass.deactivatePointcuts();
		aspectClass.setDeclares(null);
	}

	/**
	 * Create the appropriate advice method for the aspect class.
	 * That means, creates a "normal" method with the former advice body.
	 */
	private JMethodDeclaration createAspectClassAdviceMethod(CjAdviceDeclaration advice) {
		
		JMethodDeclaration decl =
			new CjAdviceMethodDeclaration(
		    			advice,
						advice.getTokenReference(),
						ACC_PUBLIC | ACC_SYNCHRONIZED,
						advice.getReturnType(),
						advice.getAdviceMethodIdent(),
						advice.getParameters(),
						advice.getExceptions(),
						advice.getBody(),
						null,
						null);
		decl.setGenerated();
		return decl;
	}
	
	/**
	 * Create abstract procceed method for the aspect class.
	 */
	private JMethodDeclaration createAbstractAspectClassProceedMethod(CjAdviceDeclaration advice) {
		
		JMethodDeclaration decl =
			new CjMethodDeclaration(
    			advice.getTokenReference(),
				ACC_PUBLIC | ACC_ABSTRACT,
				advice.getReturnType(),
				advice.getAdviceMethodIdent() + PROCEED_METHOD,
				advice.getProceedParameters(),
				advice.getExceptions(),
				null,
				null,
				null);
		decl.setGenerated();
		return decl;
	}
	
	/**
	 * Create concrete proceed method for the aspect class
	 */
	private JMethodDeclaration createConcreteAspectClassProceedMethod(CjAdviceDeclaration advice) {
		
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
	    	strProceedCall += params[i1].getIdent();
		}
	    strProceedCall += ");";
	    
		String[] block = new String[] {
			"{",
				strProceedCall,
			"}"	
	    };
	    
	    AstGenerator gen = environment.getAstGenerator();
	    gen.writeBlock(block);
	     
		JStatement[] body = gen.endBlock("proceed-method");
		
		JMethodDeclaration decl =
			new CjMethodDeclaration(
    			advice.getTokenReference(),
				ACC_PUBLIC | ACC_SYNCHRONIZED,
				advice.getReturnType(),
				advice.getAdviceMethodIdent() + PROCEED_METHOD,
				advice.getProceedParameters(),
				advice.getExceptions(),
				new JBlock(where, body, null),
				null,
				null);
		decl.setGenerated();
		return decl;
	}
	
	/**
	 * Creates the $getAspectRegistry(..) method for aspect class.
	 */
	private JMethodDeclaration createGetAspectRegistryMethod() {
		
	    AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public " + SRC_ASPECT_REGISTRY_IFC + " $getAspectRegistry()",
			"{",
 	      		"return " + srcSingletonAspectName + ".ajc$perSingletonInstance;",				
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    JMethodDeclaration decl = gen.endMethod("getAspectRegistry");
	    decl.setGenerated();
	    return decl;
	}	

	/**
	 * Creates the singleton aspect,the class which is needed by the weaver.
	 * It manages the deployment of aspects and dispatches the 
	 * advice method calls to the deployed instances.
	 */
	public CjClassDeclaration createSingletonAspect(CjPointcutDeclaration[] pointcuts, CjAdviceDeclaration[] advices, CaesarDeclare[] declares) {
		
		List fields = new ArrayList();
		List inners = new ArrayList();

		List singletonAspectMethods = new ArrayList();
		List aspectClassMethods = new ArrayList();
		
		CjAdviceDeclaration[] modifiedAdvices =
			new CjAdviceDeclaration[advices.length];

		for (int i = 0; i < advices.length; i++) {
			if (advices[i].isAroundAdvice()) {
				singletonAspectMethods.add(createProceedMethod(advices[i]));
				aspectClassMethods.add(createConcreteAspectClassProceedMethod(advices[i]));
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
		singletonAspectMethods.add(createSingletonSetSingleAspectMethod());
		
		/* create the $aspectContainer field */
		JVariableDefinition deployedInstancesVar =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				JLocalVariable.DES_GENERATED,
				new CClassNameType(ASPECT_CONTAINER_IFC),
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
		
		/* create the $singleAspect field */
		JVariableDefinition singleAspectVar =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				JLocalVariable.DES_GENERATED,
				new CClassNameType(qualifiedAspectInterfaceName),
				"$singleAspect",
				null);

		JFieldDeclaration field2 = new JFieldDeclaration(
									where,
									singleAspectVar,
									true,
									null,
									null);
		field.setGenerated(); 
		fields.add( field2 );

		singletonAspectMethods.add(createSingletonAjcClinitMethod());
		singletonAspectMethods.add(createAspectOfMethod());
		
		// create the ajc$perSingletonInstance field
		CType singletonType = new CClassNameType(qualifiedSingletonAspectName);
		JVariableDefinition aspectInstanceVar =
			new JVariableDefinition(
				where,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				JLocalVariable.DES_GENERATED,
				singletonType,
				PER_SINGLETON_INSTANCE_FIELD,
				null);

		field = new JFieldDeclaration(where, aspectInstanceVar, true, null, null);
		field.setGenerated();
		fields.add(field);

		// Implement the CaesarSingletonAspectIfc
		CReferenceType[] interfaces =
			{ typeFactory.createType(CAESAR_ASPECT_REGISTRY_IFC_CLASS, true)};

		int modifiers = ACC_PUBLIC;
		if (aspectClass.getOwner() != null) {
			//the nested singletons need to be static
			modifiers |= ACC_STATIC;
		}
		
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
			(JFieldDeclaration[]) fields.toArray(new JFieldDeclaration[0]),
			(JMethodDeclaration[]) singletonAspectMethods.toArray(new JMethodDeclaration[0]),
			(JTypeDeclaration[]) inners.toArray(new JTypeDeclaration[0]),
			initializers,
			null,
			null,
			pointcuts,
			modifiedAdvices,
			declares,
			aspectClass,
			REGISTRY_EXTENSION);

		singletonAspect.setPerClause(
				CaesarPointcut.createPerSingleton());
		
		/* generate export information for the new class */
		CClass owner = aspectClass.getOwner();
		singletonAspect.generateInterface(
			environment.getClassReader(),
			owner,
			owner == null ? packagePrefix : owner.getQualifiedName() + '$');
		
		if (!aspectClassMethods.isEmpty()) {
			updateAspectClassMethods(aspectClassMethods);
		}

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
	    return gen.endMethod("getAspectContainer");
	}
	
	/**
	 * Creates the $setSingleAspect() method for registry class.
	 */
	private JMethodDeclaration createSingletonSetSingleAspectMethod() {
		
		AstGenerator gen = environment.getAstGenerator();
	    
	    String[] body = new String[] {
	    	"public void $setSingleAspect(Object aspObj)",
			"{",
 	      		"$singleAspect = (" + srcAspectInterfaceName +")aspObj;",
			"}"	
	    };
	    
	    gen.writeMethod(body);
	    return gen.endMethod("getAspectContainer");
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
	    return gen.endMethod("setAspectContainer");
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
		String strAdviceMethodCall = advice.getAdviceMethodIdent() + "(";
		
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
					"if ($singleAspect != null)",
					"{",
						"$singleAspect." + strAdviceMethodCall,						
					"}",
					"else",
					"{",
						"Object[] inst = $aspectContainer.$getInstances();",
					    "if (inst != null)",
						"{",
							"for (int i1 = 0; i1 < inst.length; i1++)",
							"{",
								srcAspectInterfaceName + " aspObj = (" + srcAspectInterfaceName + ")inst[i1];",
								"aspObj." + strAdviceMethodCall,
							"}",
						"}",
					"}",
				"}",
			"}"	
	    };
	    
	    gen.writeBlock(block);
	     
		JStatement[] body = gen.endBlock("simple-advice");
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
		strClosureConstruction += ", $inst, 0);";
	    
	    /* Format advice body */
	    String[] block = new String[] {
	    	"{",
	    		SRC_AROUND_CLOSURE_CLASS + " $closure = aroundClosure;",
				"if ($aspectContainer != null)",
				"{",
					"Object[] $inst = $aspectContainer.$getInstances();",
					"if ($inst != null)",
					"{",
						"$closure = " + strClosureConstruction, 
					"}",
				"}",
				strProceedCall,				
			"}",
	    };
	    
	    gen.writeBlock(block);
	     
		JStatement[] body = gen.endBlock("around-advice");
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
		return gen.endMethod("ajc-clinit");
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
		return gen.endMethod("aspectof");
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
		adviceDeclaration.setAdviceMethodIdent(ident);
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
		CType iterator = new CArrayType(new CClassNameType("java/lang/Object"), 1);
		JVariableDefinition var1 =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				JLocalVariable.DES_GENERATED,
				iterator,
				"$inst",
				new JNullLiteral(where));
		JFieldDeclaration field1 = new JFieldDeclaration(where, var1, true, null, null);
		field1.setGenerated(); 
		fields.add(field1);
		
		/* create iterator field to iterate over aspect objects */
		JVariableDefinition var1a =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				JLocalVariable.DES_GENERATED,
				typeFactory.getPrimitiveType(TypeFactory.PRM_INT),
				"$ind",
				null);
		JFieldDeclaration field1a = new JFieldDeclaration(where, var1a, true, null, null);
		field1a.setGenerated(); 
		fields.add(field1a);
		
		/* create next call closure field */
		CType closure = new CClassNameType(advice.getIdent() + MULTI_INST_CLOSURE_EXTENSION);
		JVariableDefinition var2 =
			new JVariableDefinition(
				where,
				ACC_PRIVATE,
				JLocalVariable.DES_GENERATED,
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
				JLocalVariable.DES_GENERATED,
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

		if (advice.getOriginalClass() != null) {
			closureDecl.setOriginalCompUnit(advice.getOriginalClass().getCompilationUnit());
		}
		return closureDecl;
	}

	/**
	 * Create constructor for the multi-instance around closure class
	 */
	private JConstructorDeclaration createClosureConstructor(CjAdviceDeclaration advice) {
		
		JFormalParameter[] adviceParameters = advice.getParameters();

		JFormalParameter[] params =
			new JFormalParameter[adviceParameters.length + 2];

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
		CType iterator = new CArrayType(new CClassNameType("java/lang/Object"), 1);
		params[adviceParameters.length] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
				iterator,
				"$inst",
				false);
		
		params[adviceParameters.length + 1] =
			new JFormalParameter(
				where,
				JFormalParameter.DES_PARAMETER,
				typeFactory.getPrimitiveType(TypeFactory.PRM_INT),
				"$ind",
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
		strNextClosureConstruction += ", $inst, $ind+1);";
		
		/* Generate additional initalization code */
		AstGenerator gen = environment.getAstGenerator();
		
		String[] block = new String[] {
	    	"{",
	    		"if ($ind < $inst.length)",
			    "{",
					"$aspObj = (" + srcAspectInterfaceName + ")$inst[$ind];",
				    strNextClosureConstruction,
				"}",
				"else",
				"{",
					"$aspObj = null;",
				"}",
			"}"
	    };
	    
	    gen.writeBlock(block);
	    JStatement[] additionalStm = gen.endBlock("around-closure");
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
		String strAdviceMethodCall = "$aspObj." + advice.getAdviceMethodIdent() + "(";
		
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
		return gen.endMethod("around-closure-run");
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
