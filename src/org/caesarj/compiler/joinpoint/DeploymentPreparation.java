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
 * $Id: DeploymentPreparation.java,v 1.24 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarNameMangler;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjProceedDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * @author Ostermann
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeploymentPreparation implements CaesarConstants {
	private DeploymentPreparation(CjClassDeclaration cd) {
		this.cd = cd;
	}
	private CjClassDeclaration cd;
	/**
	 * Generates for every nested crosscutting class the corresponding deployment support classes.
	 */
	public static void prepareForDynamicDeployment(KjcEnvironment environment, JCompilationUnit cu) {
		List newTypeDeclarations = new ArrayList();
		JTypeDeclaration typeDeclarations[] = cu.getInners();
		for (int i = 0; i < typeDeclarations.length; i++) {
			
			newTypeDeclarations.add(typeDeclarations[i]);

			if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

				CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];

				if (caesarClass.isCrosscutting() && (!caesarClass.isStaticallyDeployed()) ) {

					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							caesarClass,
							environment);

					//modify the aspect class									
					utils.modifyAspectClass();

					//add the deployment support classes to the enclosing class							
					newTypeDeclarations.add(utils.createAspectInterface());
					newTypeDeclarations.add(utils.createSingletonAspect());
				}

				if (caesarClass.getInners().length > 0) {
					//consider nested types
					new DeploymentPreparation(caesarClass).prepareForDynamicDeployment(environment);
				}
			}
		}
		cu.setInners((JTypeDeclaration[]) newTypeDeclarations.toArray(new JTypeDeclaration[0]));	

	}
	private void prepareForDynamicDeployment(KjcEnvironment environment)
	{
	    List newInners = new LinkedList();
	    
		for (int i = 0; i < cd.getInners().length; i++)
		{
			if (cd.getInners()[i] instanceof CjClassDeclaration)
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

					//modify the aspect class		
					utils.modifyAspectClass();

					//add the deployment support classes to the enclosing class
					newInners.add(utils.createAspectInterface());
					newInners.add(utils.createSingletonAspect());
				}

				//handle the inners of the inners
				JTypeDeclaration[] innersInners = innerCaesarClass.getInners();
				for (int j = 0; j < innersInners.length; j++)
				{
					if (innersInners[j] instanceof CjClassDeclaration)
					{
						CjClassDeclaration currentInnerInner =
							(CjClassDeclaration) innersInners[j];
						new DeploymentPreparation(currentInnerInner).prepareForDynamicDeployment(environment);
					}
				}
			}

		}

		String prefix = cd.getCClass().getQualifiedName() + '$';
		
		// IVICA: refresh the class without recreating the CjSourceClass
		if(newInners.size() > 0)
		{
			for (Iterator it = newInners.iterator(); it.hasNext();) 
			{
	            JTypeDeclaration decl = (JTypeDeclaration)it.next();
	
	            decl.generateInterface(
			        environment.getClassReader(), cd.getCClass(), prefix);
	        } 
			
			// add new declarations as inners to cd
			// note that addInners will update the export object in cd
			cd.addInners((JTypeDeclaration[])newInners.toArray(new JTypeDeclaration[0]));
		}
	}

	public static void prepareForStaticDeployment(CContext context, CjClassDeclaration cd) {
		new DeploymentPreparation(cd).prepareForStaticDeployment(context);
	}
	private void prepareForStaticDeployment(CContext context)
	{
		for (int i = 0; i < cd.getAdvices().length; i++)
		{
			createAdviceMethodName(cd.getAdvices()[i]);

			if (cd.getAdvices()[i].isAroundAdvice())
			{
				//create a proceed method for around advices
				cd.addMethod(createProceedMethod(cd.getAdvices()[i]));
			}
		}

		CType singletonType = new CClassNameType(cd.getIdent());
		JVariableDefinition aspectInstanceVar =
			new JVariableDefinition(
				TokenReference.NO_REF,
				ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
				singletonType,
				PER_SINGLETON_INSTANCE_FIELD,
				null);
		JFieldDeclaration field = new JFieldDeclaration(
										cd.getTokenReference(),
										aspectInstanceVar,
										true,
										null,
										null);
		field.setGenerated();
		cd.addField(field);
		cd.addMethod(createSingletonAjcClinitMethod(context.getTypeFactory()));

		cd.addMethod(createAspectOfMethod());
		cd.addClassBlock(createSingletonAspectClinit());
	}
	
	/**
	 * Creates the proceed method for around advices.
	 * */
	private JMethodDeclaration createProceedMethod(CjAdviceDeclaration advice)
	{
		CjProceedDeclaration proceedMethodDeclaration =
			new CjProceedDeclaration(
				advice.getTokenReference(),
				advice.getReturnType(),
				advice.getIdent() + CaesarConstants.PROCEED_METHOD,
				advice.getProceedParameters(),
				advice.getIdent());
		//attach proceed-method to the adviceDeclaration
		advice.setProceedMethodDeclaration(proceedMethodDeclaration);
		return proceedMethodDeclaration;
	}
	
	
	/**
	 * Changes the name of the given advice.
	 */
	protected void createAdviceMethodName(CjAdviceDeclaration adviceDeclaration)
	{
		String ident =
			CaesarNameMangler.adviceName(
				cd.getCClass().getQualifiedName(),
				adviceDeclaration.getKind(),
				adviceDeclaration.getTokenReference().getLine());
		adviceDeclaration.setIdent(ident);
	}
	
	protected CjMethodDeclaration createSingletonAjcClinitMethod(TypeFactory typeFactory)
	{
		JStatement[] body = { createSingletonClinitMethodStatement_1()};
		return new CjMethodDeclaration(
			TokenReference.NO_REF,
			ACC_PRIVATE | ACC_STATIC,
			typeFactory.getVoidType(),
			AJC_CLINIT_METHOD,
			JFormalParameter.EMPTY,
			CReferenceType.EMPTY,
			new JBlock(TokenReference.NO_REF, body, null),
			null,
			null);
	}
	private CjMethodDeclaration createAspectOfMethod() {

		CType singletonType = new CClassNameType(cd.getCjSourceClass().getQualifiedName());
		JExpression expr =
			new JFieldAccessExpression(
				TokenReference.NO_REF,
				null,
				PER_SINGLETON_INSTANCE_FIELD);
		JStatement[] body = { new JReturnStatement(TokenReference.NO_REF, expr, null)};
		return new CjMethodDeclaration(
			TokenReference.NO_REF,
			ACC_PUBLIC | ACC_STATIC,
			singletonType,
			ASPECT_OF_METHOD,
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
			new JNameExpression(
				TokenReference.NO_REF,
				PER_SINGLETON_INSTANCE_FIELD);
		
		JExpression[] constrArgs = new JExpression[1];
		constrArgs[0] = new JNullLiteral(TokenReference.NO_REF);
		
		JExpression right =
			new JUnqualifiedInstanceCreation(
				TokenReference.NO_REF,
				new CClassNameType(cd.getIdent()),
				constrArgs);
		return new JExpressionStatement(
			TokenReference.NO_REF,
			new JAssignmentExpression(TokenReference.NO_REF, left, right),			null);
	}

	protected JClassBlock createSingletonAspectClinit()
	{

		CReferenceType type =
			new CClassNameType(cd.getCjSourceClass().getQualifiedName());
		JExpression prefix = new JTypeNameExpression(cd.getTokenReference(), type);

		JExpression expr =
			new JMethodCallExpression(
				cd.getTokenReference(),
				prefix,
				AJC_CLINIT_METHOD,
				JExpression.EMPTY);

		JStatement[] body =
			{ new JExpressionStatement(cd.getTokenReference(), expr, null)};

		return new JClassBlock(cd.getTokenReference(), true, body);
	}

	

}
