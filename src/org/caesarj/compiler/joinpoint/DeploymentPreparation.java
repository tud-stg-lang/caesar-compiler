/*
 * Created on 08.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarNameMangler;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.statement.*;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.*;
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
		List newInners = new ArrayList();

		for (int i = 0; i < cd.getInners().length; i++)
		{

			newInners.add(cd.getInners()[i]);

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

		cd.setInners(
			(JTypeDeclaration[]) newInners.toArray(new JTypeDeclaration[0]));

		//Important! Regenerate the interface of the enclosing class.				
		String prefix = cd.getCClass().getPackage().replace('.', '/') + "/";
		cd.generateInterface(environment.getClassReader(), cd.getOwner(), prefix);
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
			CTypeVariable.EMPTY,
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
			CTypeVariable.EMPTY,
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
