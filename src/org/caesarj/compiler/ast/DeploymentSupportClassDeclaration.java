package org.caesarj.compiler.ast;

import org.aspectj.weaver.patterns.Declare;

import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * This class declaration is only for the generated deployment support classes.
 * 
 * @author J?rgen Hallpap
 */
public class DeploymentSupportClassDeclaration extends FjClassDeclaration {

	private FjClassDeclaration crosscuttingClass;

	private String postfix;

	public DeploymentSupportClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		FjClassDeclaration crosscuttingClass,
		String postfix) {
		this(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			PointcutDeclaration.EMPTY,
			AdviceDeclaration.EMPTY,
			null,
			crosscuttingClass,
			postfix);
	}

	public DeploymentSupportClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		PointcutDeclaration[] pointcuts,
		AdviceDeclaration[] advices,
		Declare[] declares,
		FjClassDeclaration crosscuttingClass,
		String postfix) {
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			null,
			null,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			pointcuts,
			advices,
			declares);

		this.crosscuttingClass = crosscuttingClass;
		this.postfix = postfix;
	}

	/**
	 * Sets the superclass of this deployment class if needed.
	 */
	public void checkInterface(CContext context) throws PositionedError {

		//Add the superClass only to those classes, whose crosscuttingClass
		//has a crosscutting superClass
		if (crosscuttingClass.getSuperClass() != null
			&& (crosscuttingClass.getSuperClass().getCClass().getModifiers()
				& ACC_CROSSCUTTING)
				!= 0) {

			String superClassName =
				crosscuttingClass.getSuperClass().getIdent() + postfix;

			try {

				superClass =
					(CReferenceType) new CClassNameType(
						superClassName.intern()).checkType(
						self);

			} catch (UnpositionedError e) {

				context.reportTrouble(e.addPosition(getTokenReference()));

			}

		}

		super.checkInterface(context);
	}

}
