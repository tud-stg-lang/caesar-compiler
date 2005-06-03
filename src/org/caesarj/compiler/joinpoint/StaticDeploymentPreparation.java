/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
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
 * $Id: StaticDeploymentPreparation.java,v 1.6 2005-06-03 08:24:47 klose Exp $
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
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
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
 * TODO [documentation]
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
	
	public CjClassDeclaration findRegistryClass(String qualifiedName) {
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
	public JClassBlock createStaticFieldDeployBlock(
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
	 * Creates aspectOf method for registry class
	 */
	private JMethodDeclaration createAspectOfMethod(String classIdent) {
		
		AstGenerator gen = environment.getAstGenerator();
		
		String[] body = new String[] {
			"public static " + classIdent + " aspect()",
			"{",
				"return " + STATIC_INSTANCE_FIELD + ";",
			"}"	
		};
		
		gen.writeMethod(body);	     
		return gen.endMethod("aspectof");
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
				cd.getTokenReference(),
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				JLocalVariable.DES_GENERATED,
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
		
		cd.addField(field);
				
		cd.addClassBlock(createStaticClassDeployBlock(
			cd.getTokenReference(),
			cd,
			field));
		
		cd.addMethod(createAspectOfMethod(cd.getIdent()));
		
		CjClassDeclaration regClass = findRegistryClass(cd.getMixinIfcDeclaration().getCClass().getQualifiedName());
		if (regClass == null) {
			compiler.reportTrouble(
	            new PositionedError(
	            	cd.getTokenReference(),
					CaesarMessages.DEPLOYED_CLASS_NOT_CROSSCUTTING 
                )
            );
			return;
		}
		
		insertClassTouchBlock(regClass, cd);
	}
}
