package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.compiler.ast.BodyVisitor;
import org.caesarj.compiler.ast.CaesarClassDeclaration;
import org.caesarj.compiler.ast.FjFormalParameter;
import org.caesarj.compiler.ast.FjMethodCallExpression;
import org.caesarj.compiler.ast.FjNameExpression;
import org.caesarj.compiler.ast.JBlock;
import org.caesarj.compiler.ast.JClassBlock;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.JExpression;
import org.caesarj.compiler.ast.JExpressionStatement;
import org.caesarj.compiler.ast.JFieldDeclaration;
import org.caesarj.compiler.ast.JFormalParameter;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JNameExpression;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JStatement;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.ast.JTypeNameExpression;
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
		JClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		for (int i = 0; i < body.length; i++) {
			if (self instanceof JClassDeclaration
				&& body[i] instanceof JFieldDeclaration) {

				JFieldDeclaration field = (JFieldDeclaration) body[i];
				if ((field.getVariable().getModifiers() & ACC_DEPLOYED) != 0) {
					((JClassDeclaration) self).addClassBlock(
						createStaticDeployBlock(
							field.getTokenReference(),
							(JClassDeclaration) self,
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
		if (self instanceof CaesarClassDeclaration) {
			CaesarClassDeclaration clazz = (CaesarClassDeclaration) self;
			AdviceDeclaration[] advices = clazz.getAdvices();
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

		if (self instanceof AdviceDeclaration) {

			AdviceDeclaration adviceDec = (AdviceDeclaration) self;
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
					FjFormalParameter extraParameter =
						new FjFormalParameter(
							TokenReference.NO_REF,
							JFormalParameter.DES_GENERATED,
							new CClassNameType(JOIN_POINT_STATIC_PART_CLASS),
							THIS_JOIN_POINT_STATIC_PART,
							false);
					adviceParameters.add(extraParameter);
				}

				if (needsThisJoinPoint) {

					adviceDec.setExtraArgumentFlag(CaesarConstants.ThisJoinPoint);
					FjFormalParameter extraParameter =
						new FjFormalParameter(
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
					FjFormalParameter extraParameter =
						new FjFormalParameter(
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

	private JClassBlock createStaticDeployBlock(
		TokenReference where,
		JClassDeclaration classDeclaration,
		JFieldDeclaration fieldDeclaration) {

		JExpression fieldExpr =
			new FjNameExpression(
				where,
				null,
				fieldDeclaration.getVariable().getIdent());

		JExpression prefix =
			new FjMethodCallExpression(
				where,
				fieldExpr,
				GET_SINGLETON_ASPECT_METHOD,
				JExpression.EMPTY);

		JExpression threadPrefix =
			new JTypeNameExpression(
				where,
				new CClassNameType(QUALIFIED_THREAD_CLASS));

		JExpression[] args =
			{
				fieldExpr,
				new FjMethodCallExpression(
					where,
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};

		JExpression expr =
			new FjMethodCallExpression(where, prefix, DEPLOY_METHOD, args);

		JStatement[] body = { new JExpressionStatement(where, expr, null)};

		return new JClassBlock(where, true, body);
	}

}