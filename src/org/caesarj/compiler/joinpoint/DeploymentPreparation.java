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
 * $Id: DeploymentPreparation.java,v 1.26 2005-03-22 10:20:10 gasiunas Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
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
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * @author Ostermann
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeploymentPreparation implements CaesarConstants {
	private DeploymentPreparation(CjClassDeclaration cd, KjcEnvironment environment) {
		this.cd = cd;
		this.environment = environment;
	}
	private CjClassDeclaration cd;
	private KjcEnvironment environment;
	/**
	 * Generates for every nested crosscutting class the corresponding deployment support classes.
	 */
	public static void prepareForDynamicDeployment(CompilerBase compiler, JCompilationUnit cu) {
		List newTypeDeclarations = new ArrayList();
		JTypeDeclaration typeDeclarations[] = cu.getInners();
		CContext ownerCtx = cu.createContext(compiler);
		
		for (int i = 0; i < typeDeclarations.length; i++) {
			
			newTypeDeclarations.add(typeDeclarations[i]);

			if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

				CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];

				if (caesarClass.isCrosscutting()) {

					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							caesarClass,
							cu.getEnvironment());
					
					//	add the deployment support classes to the enclosing class
					CjInterfaceDeclaration aspectIfc = utils.createAspectInterface();
					newTypeDeclarations.add(aspectIfc);
					
					CjClassDeclaration registryCls = utils.createSingletonAspect();
					newTypeDeclarations.add(registryCls);
					
					// modify the aspect class									
					utils.modifyAspectClass();
					
					//join the modified and new classes
					try {
						aspectIfc.join(ownerCtx);
						registryCls.join(ownerCtx);	
						caesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}

					if (caesarClass.isStaticallyDeployed()) {
						new DeploymentPreparation(caesarClass, cu.getEnvironment()).prepareForStaticDeployment(registryCls);					
					}
				}
				
				if (caesarClass.getInners().length > 0) {
					//consider nested types
					new DeploymentPreparation(caesarClass, cu.getEnvironment()).prepareForDynamicDeployment(cu.getEnvironment());
				}
			}
		}
		if (newTypeDeclarations.size() > typeDeclarations.length) {
			cu.setInners((JTypeDeclaration[]) newTypeDeclarations.toArray(new JTypeDeclaration[0]));
			rejoinMixinInterfaces(cu.getInners(), ownerCtx);
		}
	}
	
	private void prepareForDynamicDeployment(KjcEnvironment environment)
	{
	    List newInners = new LinkedList();
	    CContext ownerCtx = (CContext)cd.getTypeContext();
	    
		for (int i = 0; i < cd.getInners().length; i++)
		{
			if (cd.getInners()[i] instanceof CjVirtualClassDeclaration)
			{
				//create support classes for each crosscutting inner class
				CjVirtualClassDeclaration innerCaesarClass =
					(CjVirtualClassDeclaration) cd.getInners()[i];
				if (innerCaesarClass.isCrosscutting())
				{
					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							innerCaesarClass,
							environment);

					//add the deployment support classes to the enclosing class
					CjInterfaceDeclaration aspectIfc = utils.createAspectInterface();
					newInners.add(aspectIfc);
					
					CjClassDeclaration registryCls = utils.createSingletonAspect();
					newInners.add(registryCls);
					
					//modify the aspect class		
					utils.modifyAspectClass();
					
					//join the modified and new classes
					try {
						aspectIfc.join(ownerCtx);
						registryCls.join(ownerCtx);
						innerCaesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}
				}

				//handle the inners of the inners
				JTypeDeclaration[] innersInners = innerCaesarClass.getInners();
				for (int j = 0; j < innersInners.length; j++)
				{
					if (innersInners[j] instanceof CjClassDeclaration)
					{
						CjClassDeclaration currentInnerInner =
							(CjClassDeclaration) innersInners[j];
						new DeploymentPreparation(currentInnerInner, environment).prepareForDynamicDeployment(environment);
					}
				}
			}
		}

		if (newInners.size() > 0)
		{
			// add new declarations as inners to cd
			// note that addInners will update the export object in cd
			cd.addInners((JTypeDeclaration[])newInners.toArray(new JTypeDeclaration[0]));
			rejoinMixinInterfaces(cd.getInners(), ownerCtx);
		}
	}
	
	/**
	 * Rejoin the mixin interfaces of the crosscutting Caesar classes
	 * 
	 * @param decl			array of type declarations
	 * @param ownerCtx		owner context
	 */
	private static void rejoinMixinInterfaces(JTypeDeclaration[] decl, CContext ownerCtx) {
		for (int i = 0; i < decl.length; i++) {
			if (decl[i] instanceof CjVirtualClassDeclaration) {
				CjVirtualClassDeclaration caesarClass =	(CjVirtualClassDeclaration)decl[i];
				if (caesarClass.isCrosscutting()) {
					try {
						caesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}	
				}
			}
		}
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
	    	    	     
		JStatement[] body = gen.endBlock();
		return new JClassBlock(where, true, body);
	}

	private void prepareForStaticDeployment(CjClassDeclaration registryDecl)
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
	}
}
