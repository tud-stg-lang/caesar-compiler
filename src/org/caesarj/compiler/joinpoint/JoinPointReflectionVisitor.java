package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.BodyVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.TokenReference;

/**
 * Visits the AST down to expression granularity.
 * It extends the interface of advice methods, if they make use of Join Point Reflection.
 * It also determines the corresponding extraArgumentFlags for the advice.
 * 
 * @author Jürgen Hallpap
 */
public class JoinPointReflectionVisitor
	extends BodyVisitor
	implements CaesarConstants  {

	private boolean needsThisJoinPoint = false;
	private boolean needsThisJoinPointStaticPart = false;
	private boolean needsThisEnclosingJoinPointStaticPart = false;

	/**
	 * Constructor for CaesarWeavingPreparationVisitor.
	 */
	public JoinPointReflectionVisitor() {
		super();
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitClassDeclaration(JClassDeclaration, int, String, CTypeVariable[], String, CReferenceType[], JPhylum[], JMethodDeclaration[], JTypeDeclaration[])
	 */
	public void visitClassDeclaration(
		CjClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		for (int i = 0; i < body.length; i++) {
			if (self instanceof CjClassDeclaration
				&& body[i] instanceof JFieldDeclaration) {

				JFieldDeclaration field = (JFieldDeclaration) body[i];
				if ((field.getVariable().getModifiers() & ACC_DEPLOYED) != 0) {
					((CjClassDeclaration) self).addClassBlock(
						createStaticDeployBlock(
							field.getTokenReference(),
							(CjClassDeclaration) self,
							field));

				}

			}
		}

		super.visitClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls);
		if (self instanceof CjClassDeclaration) {
			CjClassDeclaration clazz = (CjClassDeclaration) self;
			CjAdviceDeclaration[] advices = clazz.getAdvices();
			for (int i = 0; i < advices.length; i++) {
				advices[i].accept(this);
			}
		}

	}

	/**
	 * Adds an additional parameter to the advice-methods, if needed.
	 */
	public void visitMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body) {

		if (self instanceof CjAdviceDeclaration) {

			CjAdviceDeclaration adviceDec = (CjAdviceDeclaration) self;
			List adviceParameters = new ArrayList();
			//include the old parameters
			for (int i = 0; i < parameters.length; i++) {
				adviceParameters.add(parameters[i]);
			}

			if (body != null) {
				//visit the body first, in order to determine
				//if and which kind of join point reflection is needed
				body.accept(this);
				if (needsThisJoinPointStaticPart) {

					adviceDec.setExtraArgumentFlag(
						CaesarConstants.ThisJoinPointStaticPart);
					JFormalParameter extraParameter =
						new JFormalParameter(
							TokenReference.NO_REF,
							JFormalParameter.DES_GENERATED,
							new CClassNameType(JOIN_POINT_STATIC_PART_CLASS),
							THIS_JOIN_POINT_STATIC_PART,
							false);
					adviceParameters.add(extraParameter);
				}

				if (needsThisJoinPoint) {

					adviceDec.setExtraArgumentFlag(CaesarConstants.ThisJoinPoint);
					JFormalParameter extraParameter =
						new JFormalParameter(
							TokenReference.NO_REF,
							JFormalParameter.DES_GENERATED,
							new CClassNameType(JOIN_POINT_CLASS),
							THIS_JOIN_POINT,
							false);
					adviceParameters.add(extraParameter);
				}

				if (needsThisEnclosingJoinPointStaticPart) {

					adviceDec.setExtraArgumentFlag(
						CaesarConstants.ThisEnclosingJoinPointStaticPart);
					JFormalParameter extraParameter =
						new JFormalParameter(
							TokenReference.NO_REF,
							JFormalParameter.DES_GENERATED,
							new CClassNameType(JOIN_POINT_STATIC_PART_CLASS),
							THIS_ENCLOSING_JOIN_POINT_STATIC_PART,
							false);
					adviceParameters.add(extraParameter);
				} //				determineExtraArgumentFlags(adviceDec);
			} //reset the flags
			needsThisJoinPoint = false;
			needsThisEnclosingJoinPointStaticPart = false;
			needsThisEnclosingJoinPointStaticPart = false;
			adviceDec.setParameters(
				(JFormalParameter[]) adviceParameters.toArray(
					new JFormalParameter[0]));
		}
	} 
	
	/**
	 * Visits all NameExpressions.
	 * Set the correspondig flag if a thisJoinPoint,thisJoinPointStaticPart
	 * or thisEnclosingJoinPointStaticPart Expression is found.
	 */
	public void visitNameExpression(
		JNameExpression self,
		JExpression prefix,
		String ident) {

		if (ident.equals(THIS_JOIN_POINT)) {

			needsThisJoinPoint = true;
		} else if (ident.equals(THIS_JOIN_POINT_STATIC_PART)) {

			needsThisJoinPointStaticPart = true;
		} else if (ident.equals(THIS_ENCLOSING_JOIN_POINT_STATIC_PART)) {

			needsThisEnclosingJoinPointStaticPart = true;
		}

	}

	/*
	 * Creates static initalization block:
	 * 
	 * {
	 *    <field>.$deploySelf(java.lang.Thread.currentThread());
	 * }
	 * 
	 */
	private JClassBlock createStaticDeployBlock(
		TokenReference where,
		CjClassDeclaration classDeclaration,
		JFieldDeclaration fieldDeclaration) {

		JExpression fieldExpr =
			new JNameExpression(
				where,
				null,
				fieldDeclaration.getVariable().getIdent());

		JExpression threadPrefix =
			new JTypeNameExpression(
				where,
				new CClassNameType(QUALIFIED_THREAD_CLASS));

		JExpression[] args =
			{
				new CjMethodCallExpression(
					where,
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};

		JExpression expr =
			new CjMethodCallExpression(where, fieldExpr, DEPLOY_SELF_METHOD, args);

		JStatement[] body = { new JExpressionStatement(where, expr, null)};

		return new JClassBlock(where, true, body);
	}

}