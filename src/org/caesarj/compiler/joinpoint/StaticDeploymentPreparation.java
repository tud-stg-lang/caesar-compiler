/*
 * Created on Mar 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.joinpoint;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

/**
 * @author Vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StaticDeploymentPreparation implements CaesarConstants {
	
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
	private static JClassBlock createStaticClassDeployBlock(
			TokenReference where,
			CjClassDeclaration classDeclaration,
			JFieldDeclaration fieldDeclaration,
			KjcEnvironment environment) { 
			
		AstGenerator gen = environment.getAstGenerator();
		
		String srcClassName = classDeclaration.getCClass().getQualifiedName();
		srcClassName = srcClassName.replace('/', '.');
		srcClassName = srcClassName.replace('$', '.');
			
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

	public static void prepareForStaticDeployment(
			CjVirtualClassDeclaration cd,
			KjcEnvironment environment)
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
			field,
			environment));
	}

}
