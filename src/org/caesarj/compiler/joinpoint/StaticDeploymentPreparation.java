/*
 * Created on Mar 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

/**
 * @author Vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StaticDeploymentPreparation implements CaesarConstants {
	
	CompilerBase compiler;
	KjcEnvironment environment;
	CaesarTypeSystem typeSys;
		
	public StaticDeploymentPreparation(CompilerBase compiler, KjcEnvironment environment) {
		this.compiler = compiler;
		this.environment = environment;
		this.typeSys = environment.getCaesarTypeSystem();
	}
	
	public void prepareForStaticDeployment(JCompilationUnit cu) {
		List newTypeDeclarations = new ArrayList();
		JTypeDeclaration typeDeclarations[] = cu.getInners();
		
		for (int i = 0; i < typeDeclarations.length; i++) {
			if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {
				
				CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];
				
				if (caesarClass.isStaticallyDeployed()) {
					prepareForStaticDeployment(caesarClass);
			 	}
			}
		}				
	}
	
	public CjClassDeclaration findRegistryClass(CjVirtualClassDeclaration caesarClass) {
		String qualifiedName = caesarClass.getMixinIfcDeclaration().getCClass().getQualifiedName();
        CaesarTypeNode node = typeSys.getCaesarTypeGraph().getType(new JavaQualifiedName(qualifiedName));
        
        for (Iterator it = node.getMixinList().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            if (item.needsAspectRegistry()) {
            	CjVirtualClassDeclaration decl = item.getTypeDecl().getCorrespondingClassDeclaration();
            	return decl.getRegistryClass();
            }
        }
		return null;
	}
	
	/*
	 * Creates static initalization block:
	 * 
	 * {
	 *    DeploySupport.deployBlock(<field>);
	 * }
	 * 
	 */
	public static JClassBlock createStaticFieldDeployBlock(
		TokenReference where,
		CjClassDeclaration classDeclaration,
		JFieldDeclaration fieldDeclaration) {

		JExpression prefix =
            new JTypeNameExpression(
                where,
                new CClassNameType(CAESAR_DEPLOY_SUPPORT_CLASS));

        JExpression deployStatementCall =
            new JMethodCallExpression(
                where,
                prefix,
                "deployLocal",
                new JExpression[] {new JNameExpression(where, fieldDeclaration.getVariable().getIdent())});

		JStatement[] body = { new JExpressionStatement(where, deployStatementCall, null) };

		return new JClassBlock(where, true, body);
	}
	
	/*
	 * Creates static initalization block:
	 * 
	 * {
	 *    DeploySupport.deployBlock(<field>);
	 * }
	 * 
	 */
	private JClassBlock createStaticClassDeployBlock(
			TokenReference where,
			CjClassDeclaration classDeclaration,
			JFieldDeclaration fieldDeclaration) { 
			
		AstGenerator gen = environment.getAstGenerator();
		
		String srcClassName = Utils.getClassSourceName(classDeclaration.getCClass().getQualifiedName());
			
		/* Format advice body */
	    String[] block = new String[] {
	    	"{",
	    		"$staticInstance = new " + srcClassName + "(null);",
				"org.caesarj.runtime.DeploySupport.deployLocal($staticInstance);",								
			"}"
	    };
	    
	    gen.writeBlock(block);
	    	    	     
		JStatement[] body = gen.endBlock("static-deploy-block");
		return new JClassBlock(where, true, body);
	}
	
	/**
	 * Insert class block to ensure that one class is loaded whenever another class is loaded
	 * 
	 * @param fromClass		class triggering loading
	 * @param toClass		class to be loaded
	 */
	public void insertClassTouchBlock(CjClassDeclaration fromClass, CjClassDeclaration toClass) {
		String srcClassName = Utils.getClassSourceName(toClass.getCClass().getQualifiedName());
		
	    AstGenerator gen = environment.getAstGenerator();
	    		
		String[] block = new String[] {
			"{",
				"try ",
				"{",
					"java.lang.Class.forName(\"" + srcClassName + "\");",
				"}",
				"catch (java.lang.ClassNotFoundException e) { }",
			"}"	
		};
		    
		gen.writeBlock(block);	     
		JStatement[] body = gen.endBlock("class-touch-block");
		
		fromClass.addClassBlock(new JClassBlock(fromClass.getTokenReference(), true, body));
	}

	/**
	 * Prepare Caesar class for static deployment
	 * 
	 * @param cd	class to be prepared
	 */
	public void prepareForStaticDeployment(CjVirtualClassDeclaration cd)
	{
		CType singletonType = new CClassNameType(cd.getIdent());
		JVariableDefinition aspectInstanceVar =
			new JVariableDefinition(
				TokenReference.NO_REF,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				singletonType,
				STATIC_INSTANCE_FIELD,
				null);
		
		JFieldDeclaration field = new JFieldDeclaration(
										cd.getTokenReference(),
										aspectInstanceVar,
										true,
										null,
										null);
		field.setGenerated();
		
		/* add the field to the registry class, because it is automatically loaded */
		cd.addField(field);
				
		cd.addClassBlock(createStaticClassDeployBlock(
			cd.getTokenReference(),
			cd,
			field));
		
		CjClassDeclaration regClass = findRegistryClass(cd);
		if (regClass == null) {
			compiler.reportTrouble(
	            new PositionedError(
	            	cd.getTokenReference(),
					CaesarMessages.DEPLOYED_CLASS_NOT_CROSSCUTTING 
                )
            );
		}
		
		insertClassTouchBlock(regClass, cd);
	}
}
