package org.caesarj.compiler.ast;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;



/**
 * This visitor implementation visits almost 
 * every node in the Caesar AST.
 */
public abstract class BodyVisitor extends DeclarationVisitor {

	/**
	 * Constructor for CaesarVisitor.
	 */
	public BodyVisitor() {
		super();
	}

	public void visitClassBody(
		JTypeDeclaration[] decls,
		JMethodDeclaration[] methods,
		JPhylum[] body) {

		for (int i = 0; i < methods.length; i++) {
			methods[i].accept(this);
		}
	}

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
			
		for (int i = 0; i < decls.length; i++) {
			decls[i].accept(this);
		}	

		for (int i = 0; i < methods.length; i++) {
			methods[i].accept(this);
		}	
	}

	public void visitMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body) {

		if (body != null) {
			body.accept(this);
		}
	}

	public void visitKopiMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JBlock ensure,
		JBlock require) {

		if (body != null) {
			body.accept(this);
		}
	}
	public void visitWhileStatement(
		JWhileStatement self,
		JExpression cond,
		JStatement body) {

		cond.accept(this);

		body.accept(this);
	}

	public void visitVariableDeclarationStatement(
		JVariableDeclarationStatement self,
		JVariableDefinition[] vars) {

		for (int i = 0; i < vars.length; i++) {
			vars[i].accept(this);
		}
	}

	public void visitVariableDefinition(
		JVariableDefinition self,
		int modifiers,
		CType type,
		String ident,
		JExpression expr) {

		if (expr != null) {
			expr.accept(this);
		}
	}

	public void visitTryCatchStatement(
		JTryCatchStatement self,
		JBlock tryClause,
		JCatchClause[] catchClauses) {

		tryClause.accept(this);

		for (int i = 0; i < catchClauses.length; i++) {
			catchClauses[i].accept(this);
		}
	}

	public void visitTryFinallyStatement(
		JTryFinallyStatement self,
		JBlock tryClause,
		JBlock finallyClause) {

		tryClause.accept(this);

		if (finallyClause != null) {
			finallyClause.accept(this);
		}
	}

	public void visitThrowStatement(JThrowStatement self, JExpression expr) {
		expr.accept(this);
	}

	public void visitSynchronizedStatement(
		JSynchronizedStatement self,
		JExpression cond,
		JStatement body) {

		cond.accept(this);
		body.accept(this);
	}

	public void visitSwitchStatement(
		JSwitchStatement self,
		JExpression expr,
		JSwitchGroup[] body) {

		expr.accept(this);

		for (int i = 0; i < body.length; i++) {
			body[i].accept(this);
		}
	}

	public void visitReturnStatement(JReturnStatement self, JExpression expr) {
		if (expr != null) {
			expr.accept(this);
		}
	}

	public void visitLabeledStatement(
		JLabeledStatement self,
		String label,
		JStatement stmt) {
		stmt.accept(this);
	}

	public void visitIfStatement(
		JIfStatement self,
		JExpression cond,
		JStatement thenClause,
		JStatement elseClause) {
		cond.accept(this);

		thenClause.accept(this);

		if (elseClause != null) {
			elseClause.accept(this);
		}
	}

	public void visitForStatement(
		JForStatement self,
		JStatement init,
		JExpression cond,
		JStatement incr,
		JStatement body) {

		if (init != null) {
			init.accept(this);
		}

		if (cond != null) {
			cond.accept(this);
		}

		if (incr != null) {
			incr.accept(this);
		}

		body.accept(this);

	}

	public void visitCompoundStatement(
		JCompoundStatement self,
		JStatement[] body) {

		visitCompoundStatement(body);
	}

	public void visitCompoundStatement(JStatement[] body) {
		for (int i = 0; i < body.length; i++) {
			body[i].accept(this);
		}
	}

	public void visitExpressionStatement(
		JExpressionStatement self,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitExpressionListStatement(
		JExpressionListStatement self,
		JExpression[] expr) {

		for (int i = 0; i < expr.length; i++) {
			expr[i].accept(this);
		}
	}

	public void visitDoStatement(
		JDoStatement self,
		JExpression cond,
		JStatement body) {

		body.accept(this);
		cond.accept(this);
	}

	public void visitBlockStatement(
		JBlock self,
		JStatement[] body,
		JavaStyleComment[] comments) {

		visitCompoundStatement(body);
	}

	public void visitTypeDeclarationStatement(
		JTypeDeclarationStatement self,
		JTypeDeclaration decl) {

		decl.accept(this);
	}



	// ----------------------------------------------------------------------
	// EXPRESSIONS
	// ----------------------------------------------------------------------

	public void visitUnaryPlusExpression(
		JUnaryExpression self,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitUnaryMinusExpression(
		JUnaryExpression self,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitBitwiseComplementExpression(
		JUnaryExpression self,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitLogicalComplementExpression(
		JUnaryExpression self,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitThisExpression(JThisExpression self, JExpression prefix) {
		if (prefix != null) {
			prefix.accept(this);
		}
	}

	public void visitShiftExpression(
		JShiftExpression self,
		int oper,
		JExpression left,
		JExpression right) {

		left.accept(this);
		right.accept(this);

	}

	public void visitRelationalExpression(
		JRelationalExpression self,
		int oper,
		JExpression left,
		JExpression right) {

		left.accept(this);
		right.accept(this);
	}

	public void visitPrefixExpression(
		JPrefixExpression self,
		int oper,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitPostfixExpression(
		JPostfixExpression self,
		int oper,
		JExpression expr) {

		expr.accept(this);
	}

	public void visitParenthesedExpression(
		JParenthesedExpression self,
		JExpression expr) {

		expr.accept(this);

	}

	public void visitQualifiedAnonymousCreation(
		JQualifiedAnonymousCreation self,
		JExpression prefix,
		String ident,
		JExpression[] params,
		JClassDeclaration decl) {

		prefix.accept(this);
		visitArgs(params);
	}

	public void visitQualifiedInstanceCreation(
		JQualifiedInstanceCreation self,
		JExpression prefix,
		String ident,
		JExpression[] params) {

		prefix.accept(this);
		visitArgs(params);
	}

	public void visitUnqualifiedAnonymousCreation(
		JUnqualifiedAnonymousCreation self,
		CReferenceType type,
		JExpression[] params,
		JClassDeclaration decl) {

		visitArgs(params);

	}

	public void visitUnqualifiedInstanceCreation(
		JUnqualifiedInstanceCreation self,
		CReferenceType type,
		JExpression[] params) {

		visitArgs(params);

	}

	public void visitNewArrayExpression(
		JNewArrayExpression self,
		CType type,
		JExpression[] dims,
		JArrayInitializer init) {

		for (int i = 0; i < dims.length; i++) {

			if (dims[i] != null) {
				dims[i].accept(this);
			}

		}
		if (init != null) {
			init.accept(this);
		}
	}

	public void visitNameExpression(
		JNameExpression self,
		JExpression prefix,
		String ident) {

		if (prefix != null) {
			prefix.accept(this);

		}

	}

	public void visitBinaryExpression(
		JBinaryExpression self,
		String oper,
		JExpression left,
		JExpression right) {

		left.accept(this);

		right.accept(this);
	}

	public void visitMethodCallExpression(
		JMethodCallExpression self,
		JExpression prefix,
		String ident,
		JExpression[] args) {

		if (prefix != null) {
			prefix.accept(this);

		}
		visitArgs(args);

	}

	public void visitInstanceofExpression(
		JInstanceofExpression self,
		JExpression expr,
		CType dest) {

		expr.accept(this);
	}

	public void visitEqualityExpression(
		JEqualityExpression self,
		boolean equal,
		JExpression left,
		JExpression right) {

		left.accept(this);

		right.accept(this);
	}

	public void visitConditionalExpression(
		JConditionalExpression self,
		JExpression cond,
		JExpression left,
		JExpression right) {

		cond.accept(this);

		left.accept(this);

		right.accept(this);
	}

	public void visitCompoundAssignmentExpression(
		JCompoundAssignmentExpression self,
		int oper,
		JExpression left,
		JExpression right) {

		left.accept(this);
		right.accept(this);
	}

	public void visitFieldExpression(
		JFieldAccessExpression self,
		JExpression left,
		String ident) {

		if (left != null) {
			left.accept(this);
		}

	}

	/**
	 * prints a class expression
	 */
	public void visitClassExpression(
		JClassExpression self,
		CType type,
		JExpression prefix,
		int bounds) {

		if (prefix != null) {
			prefix.accept(this);
		}

	}

	public void visitCastExpression(
		JCastExpression self,
		JExpression expr,
		CType type) {

		expr.accept(this);
	}

	public void visitUnaryPromoteExpression(
		JUnaryPromote self,
		JExpression expr,
		CType type) {

		expr.accept(this);

	}

	public void visitBitwiseExpression(
		JBitwiseExpression self,
		int oper,
		JExpression left,
		JExpression right) {

		left.accept(this);
		right.accept(this);
	}

	public void visitAssignmentExpression(
		JAssignmentExpression self,
		JExpression left,
		JExpression right) {

		left.accept(this);

		right.accept(this);
	}

	public void visitArrayLengthExpression(
		JArrayLengthExpression self,
		JExpression prefix) {
		prefix.accept(this);

	}

	public void visitArrayAccessExpression(
		JArrayAccessExpression self,
		JExpression prefix,
		JExpression accessor) {

		prefix.accept(this);

		accessor.accept(this);

	}

	public void visitSwitchLabel(JSwitchLabel self, JExpression expr) {

		if (expr != null) {

			expr.accept(this);

		}
	}

	public void visitSwitchGroup(
		JSwitchGroup self,
		JSwitchLabel[] labels,
		JStatement[] stmts) {
		for (int i = 0; i < labels.length; i++) {
			labels[i].accept(this);
		}

		for (int i = 0; i < stmts.length; i++) {

			stmts[i].accept(this);
		}

	}

	public void visitCatchClause(
		JCatchClause self,
		JFormalParameter exception,
		JBlock body) {

		exception.accept(this);

		body.accept(this);
	}

	public void visitArgs(JExpression[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				args[i].accept(this);
			}
		}
	}

	public void visitConstructorCall(
		JConstructorCall self,
		boolean functorIsThis,
		JExpression[] params) {

		visitArgs(params);

	}

	public void visitArrayInitializer(
		JArrayInitializer self,
		JExpression[] elems) {

		for (int i = 0; i < elems.length; i++) {
			elems[i].accept(this);
		}

	}


}
