package org.caesarj.compiler.ast;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;




/**
 * @author andreas
 * 
 * Adds my new types to kopi's visitor.
 */
public abstract class FjVisitor implements KjcVisitor {

	protected Owner owner;

	protected class Owner {
		Object reference;
		public void set( Object o ) {
			if (o == null
				|| o instanceof FjClassDeclaration
				|| o instanceof JCompilationUnit
				|| o instanceof CciInterfaceDeclaration)
				reference = o;
			else
				throw new IllegalArgumentException( "illegal owner: " + o.getClass().getName() );
		}
		
		/**
		 * 
		 * @return a reference to the owner.
		 */
		public Object get() {
			return reference;
		}

		public void append( JTypeDeclaration decl ) {

			if( reference instanceof FjClassDeclaration )
				((FjClassDeclaration) reference).append( decl );
			else if( reference instanceof JCompilationUnit )
				((JCompilationUnit) reference).append( decl );
		}
		public String getQualifiedName() {

			if( reference instanceof FjClassDeclaration )
				return ((FjClassDeclaration) reference).getCClass().getQualifiedName() + "$";
			else if( reference instanceof JCompilationUnit )
				return new String();
			else
				return null;
		}
	}
	
	public static class PositionedErrorHolder extends RuntimeException {
		public PositionedError error;
		public PositionedErrorHolder( PositionedError error ) {
			this.error = error;
		}
	}
	
	public FjVisitor() {
		this.owner = new Owner();
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
			if( self instanceof FjOverrideClassDeclaration )
				visitFjOverrideClassDeclaration(
					(FjOverrideClassDeclaration) self,
					modifiers,
					ident,
					typeVariables,
					superClass,
					interfaces,
					body,
					methods,
					decls );
			else if( self instanceof FjVirtualClassDeclaration )
				visitFjVirtualClassDeclaration(
					(FjVirtualClassDeclaration) self,
					modifiers,
					ident,
					typeVariables,
					superClass,
					interfaces,
					body,
					methods,
					decls );
			else if (self instanceof CciWeaveletClassDeclaration)
				visitCciWeaveletClassDeclaration(
					(CciWeaveletClassDeclaration) self,
					modifiers,
					ident,
					typeVariables,
					superClass,
					interfaces,
					body,
					methods,
					decls );					
			else if( self instanceof FjCleanClassDeclaration )
				visitFjCleanClassDeclaration(
					(FjCleanClassDeclaration) self,
					modifiers,
					ident,
					typeVariables,
					superClass,
					interfaces,
					body,
					methods,
					decls );
			else if( self instanceof FjClassDeclaration )
				visitFjClassDeclaration(
					(FjClassDeclaration) self,
					modifiers,
					ident,
					typeVariables,
					superClass,
					interfaces,
					body,
					methods,
					decls );
			else {
			}
	}
	
	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCompilationUnit(JCompilationUnit, JPackageName, JPackageImport[], JClassImport[], JTypeDeclaration[])
	 */
	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations) {
		
		owner.set( self );
		for( int i = 0; i < typeDeclarations.length; i++ ) {
			typeDeclarations[i].accept( this );
		}
		owner.set(null);

	}

	public void visitFjClassDeclaration(
		FjClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		Object oldOwner = this.owner.get();
		owner.set( self );
		for( int i = 0; i < decls.length; i++ ) {
			decls[i].accept( this );
		}
		owner.set( oldOwner );

	}

	public void visitCciWeaveletClassDeclaration(
		CciWeaveletClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {
		visitFjCleanClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls
		);
	}
	
	public void visitFjCleanClassDeclaration(
		FjCleanClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {
		visitFjClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls
		);
	}

	public void visitFjVirtualClassDeclaration(
		FjVirtualClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {
		visitFjCleanClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls
		);
	}
	
	public void visitFjOverrideClassDeclaration(
		FjOverrideClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {
		visitFjVirtualClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls
		);
	}
	
	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitClassBody(JTypeDeclaration[], JMethodDeclaration[], JPhylum[])
	 */
	public void visitClassBody(
		JTypeDeclaration[] decls,
		JMethodDeclaration[] methods,
		JPhylum[] body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitInnerClassDeclaration(JClassDeclaration, int, String, String, CReferenceType[], JTypeDeclaration[], JPhylum[], JMethodDeclaration[])
	 */
	public void visitInnerClassDeclaration(
		JClassDeclaration self,
		int modifiers,
		String ident,
		String superClass,
		CReferenceType[] interfaces,
		JTypeDeclaration[] decls,
		JPhylum[] body,
		JMethodDeclaration[] methods) {
	}

	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods)
	{
	}

	public void visitFieldDeclaration(
		JFieldDeclaration self,
		int modifiers,
		CType type,
		String ident,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitMethodDeclaration(JMethodDeclaration, int, CTypeVariable[], CType, String, JFormalParameter[], CReferenceType[], JBlock)
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
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitKopiMethodDeclaration(JMethodDeclaration, int, CType, String, JFormalParameter[], CReferenceType[], JBlock, JBlock, JBlock)
	 */
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
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitConstructorDeclaration(JConstructorDeclaration, int, String, JFormalParameter[], CReferenceType[], JConstructorBlock)
	 */
	public void visitConstructorDeclaration(
		JConstructorDeclaration self,
		int modifiers,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JConstructorBlock body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitWhileStatement(JWhileStatement, JExpression, JStatement)
	 */
	public void visitWhileStatement(
		JWhileStatement self,
		JExpression cond,
		JStatement body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitVariableDeclarationStatement(JVariableDeclarationStatement, JVariableDefinition[])
	 */
	public void visitVariableDeclarationStatement(
		JVariableDeclarationStatement self,
		JVariableDefinition[] vars) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitVariableDefinition(JVariableDefinition, int, CType, String, JExpression)
	 */
	public void visitVariableDefinition(
		JVariableDefinition self,
		int modifiers,
		CType type,
		String ident,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitTryCatchStatement(JTryCatchStatement, JBlock, JCatchClause[])
	 */
	public void visitTryCatchStatement(
		JTryCatchStatement self,
		JBlock tryClause,
		JCatchClause[] catchClauses) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitTryFinallyStatement(JTryFinallyStatement, JBlock, JBlock)
	 */
	public void visitTryFinallyStatement(
		JTryFinallyStatement self,
		JBlock tryClause,
		JBlock finallyClause) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitThrowStatement(JThrowStatement, JExpression)
	 */
	public void visitThrowStatement(JThrowStatement self, JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitSynchronizedStatement(JSynchronizedStatement, JExpression, JStatement)
	 */
	public void visitSynchronizedStatement(
		JSynchronizedStatement self,
		JExpression cond,
		JStatement body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitSwitchStatement(JSwitchStatement, JExpression, JSwitchGroup[])
	 */
	public void visitSwitchStatement(
		JSwitchStatement self,
		JExpression expr,
		JSwitchGroup[] body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitReturnStatement(JReturnStatement, JExpression)
	 */
	public void visitReturnStatement(JReturnStatement self, JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitLabeledStatement(JLabeledStatement, String, JStatement)
	 */
	public void visitLabeledStatement(
		JLabeledStatement self,
		String label,
		JStatement stmt) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitIfStatement(JIfStatement, JExpression, JStatement, JStatement)
	 */
	public void visitIfStatement(
		JIfStatement self,
		JExpression cond,
		JStatement thenClause,
		JStatement elseClause) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitForStatement(JForStatement, JStatement, JExpression, JStatement, JStatement)
	 */
	public void visitForStatement(
		JForStatement self,
		JStatement init,
		JExpression cond,
		JStatement incr,
		JStatement body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCompoundStatement(JCompoundStatement, JStatement[])
	 */
	public void visitCompoundStatement(
		JCompoundStatement self,
		JStatement[] body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitExpressionStatement(JExpressionStatement, JExpression)
	 */
	public void visitExpressionStatement(
		JExpressionStatement self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitExpressionListStatement(JExpressionListStatement, JExpression[])
	 */
	public void visitExpressionListStatement(
		JExpressionListStatement self,
		JExpression[] expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitEmptyStatement(JEmptyStatement)
	 */
	public void visitEmptyStatement(JEmptyStatement self) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitDoStatement(JDoStatement, JExpression, JStatement)
	 */
	public void visitDoStatement(
		JDoStatement self,
		JExpression cond,
		JStatement body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitContinueStatement(JContinueStatement, String)
	 */
	public void visitContinueStatement(JContinueStatement self, String label) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBreakStatement(JBreakStatement, String)
	 */
	public void visitBreakStatement(JBreakStatement self, String label) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitConstructorBlockStatement(JBlock, JExpression, JStatement[], JavaStyleComment[])
	 */
	public void visitConstructorBlockStatement(
		JBlock self,
		JExpression constructorCall,
		JStatement[] body,
		JavaStyleComment[] comments) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBlockStatement(JBlock, JStatement[], JavaStyleComment[])
	 */
	public void visitBlockStatement(
		JBlock self,
		JStatement[] body,
		JavaStyleComment[] comments) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitTypeDeclarationStatement(JTypeDeclarationStatement, JTypeDeclaration)
	 */
	public void visitTypeDeclarationStatement(
		JTypeDeclarationStatement self,
		JTypeDeclaration decl) {
	}


	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitUnaryPlusExpression(JUnaryExpression, JExpression)
	 */
	public void visitUnaryPlusExpression(
		JUnaryExpression self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitUnaryMinusExpression(JUnaryExpression, JExpression)
	 */
	public void visitUnaryMinusExpression(
		JUnaryExpression self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBitwiseComplementExpression(JUnaryExpression, JExpression)
	 */
	public void visitBitwiseComplementExpression(
		JUnaryExpression self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitLogicalComplementExpression(JUnaryExpression, JExpression)
	 */
	public void visitLogicalComplementExpression(
		JUnaryExpression self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitTypeNameExpression(JTypeNameExpression, CType)
	 */
	public void visitTypeNameExpression(JTypeNameExpression self, CType type) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitThisExpression(JThisExpression, JExpression)
	 */
	public void visitThisExpression(JThisExpression self, JExpression prefix) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitSuperExpression(JSuperExpression)
	 */
	public void visitSuperExpression(JSuperExpression self) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitShiftExpression(JShiftExpression, int, JExpression, JExpression)
	 */
	public void visitShiftExpression(
		JShiftExpression self,
		int oper,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitRelationalExpression(JRelationalExpression, int, JExpression, JExpression)
	 */
	public void visitRelationalExpression(
		JRelationalExpression self,
		int oper,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitPrefixExpression(JPrefixExpression, int, JExpression)
	 */
	public void visitPrefixExpression(
		JPrefixExpression self,
		int oper,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitPostfixExpression(JPostfixExpression, int, JExpression)
	 */
	public void visitPostfixExpression(
		JPostfixExpression self,
		int oper,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitParenthesedExpression(JParenthesedExpression, JExpression)
	 */
	public void visitParenthesedExpression(
		JParenthesedExpression self,
		JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitQualifiedAnonymousCreation(JQualifiedAnonymousCreation, JExpression, String, JExpression[], JClassDeclaration)
	 */
	public void visitQualifiedAnonymousCreation(
		JQualifiedAnonymousCreation self,
		JExpression prefix,
		String ident,
		JExpression[] params,
		JClassDeclaration decl) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitQualifiedInstanceCreation(JQualifiedInstanceCreation, JExpression, String, JExpression[])
	 */
	public void visitQualifiedInstanceCreation(
		JQualifiedInstanceCreation self,
		JExpression prefix,
		String ident,
		JExpression[] params) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitUnqualifiedAnonymousCreation(JUnqualifiedAnonymousCreation, CReferenceType, JExpression[], JClassDeclaration)
	 */
	public void visitUnqualifiedAnonymousCreation(
		JUnqualifiedAnonymousCreation self,
		CReferenceType type,
		JExpression[] params,
		JClassDeclaration decl) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitUnqualifiedInstanceCreation(JUnqualifiedInstanceCreation, CReferenceType, JExpression[])
	 */
	public void visitUnqualifiedInstanceCreation(
		JUnqualifiedInstanceCreation self,
		CReferenceType type,
		JExpression[] params) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitNewArrayExpression(JNewArrayExpression, CType, JExpression[], JArrayInitializer)
	 */
	public void visitNewArrayExpression(
		JNewArrayExpression self,
		CType type,
		JExpression[] dims,
		JArrayInitializer init) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitNameExpression(JNameExpression, JExpression, String)
	 */
	public void visitNameExpression(
		JNameExpression self,
		JExpression prefix,
		String ident) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBinaryExpression(JBinaryExpression, String, JExpression, JExpression)
	 */
	public void visitBinaryExpression(
		JBinaryExpression self,
		String oper,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitMethodCallExpression(JMethodCallExpression, JExpression, String, JExpression[])
	 */
	public void visitMethodCallExpression(
		JMethodCallExpression self,
		JExpression prefix,
		String ident,
		JExpression[] args) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitLocalVariableExpression(JLocalVariableExpression, String)
	 */
	public void visitLocalVariableExpression(
		JLocalVariableExpression self,
		String ident) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitInstanceofExpression(JInstanceofExpression, JExpression, CType)
	 */
	public void visitInstanceofExpression(
		JInstanceofExpression self,
		JExpression expr,
		CType dest) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitEqualityExpression(JEqualityExpression, boolean, JExpression, JExpression)
	 */
	public void visitEqualityExpression(
		JEqualityExpression self,
		boolean equal,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitConditionalExpression(JConditionalExpression, JExpression, JExpression, JExpression)
	 */
	public void visitConditionalExpression(
		JConditionalExpression self,
		JExpression cond,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCompoundAssignmentExpression(JCompoundAssignmentExpression, int, JExpression, JExpression)
	 */
	public void visitCompoundAssignmentExpression(
		JCompoundAssignmentExpression self,
		int oper,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitFieldExpression(JFieldAccessExpression, JExpression, String)
	 */
	public void visitFieldExpression(
		JFieldAccessExpression self,
		JExpression left,
		String ident) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitClassExpression(JClassExpression, CType, JExpression, int)
	 */
	public void visitClassExpression(
		JClassExpression self,
		CType type,
		JExpression prefix,
		int bounds) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCastExpression(JCastExpression, JExpression, CType)
	 */
	public void visitCastExpression(
		JCastExpression self,
		JExpression expr,
		CType type) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitUnaryPromoteExpression(JUnaryPromote, JExpression, CType)
	 */
	public void visitUnaryPromoteExpression(
		JUnaryPromote self,
		JExpression expr,
		CType type) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBitwiseExpression(JBitwiseExpression, int, JExpression, JExpression)
	 */
	public void visitBitwiseExpression(
		JBitwiseExpression self,
		int oper,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitAssignmentExpression(JAssignmentExpression, JExpression, JExpression)
	 */
	public void visitAssignmentExpression(
		JAssignmentExpression self,
		JExpression left,
		JExpression right) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitArrayLengthExpression(JArrayLengthExpression, JExpression)
	 */
	public void visitArrayLengthExpression(
		JArrayLengthExpression self,
		JExpression prefix) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitArrayAccessExpression(JArrayAccessExpression, JExpression, JExpression)
	 */
	public void visitArrayAccessExpression(
		JArrayAccessExpression self,
		JExpression prefix,
		JExpression accessor) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitComments(JavaStyleComment[])
	 */
	public void visitComments(JavaStyleComment[] comments) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitComment(JavaStyleComment)
	 */
	public void visitComment(JavaStyleComment comment) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitJavadoc(JavadocComment)
	 */
	public void visitJavadoc(JavadocComment comment) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitSwitchLabel(JSwitchLabel, JExpression)
	 */
	public void visitSwitchLabel(JSwitchLabel self, JExpression expr) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitSwitchGroup(JSwitchGroup, JSwitchLabel[], JStatement[])
	 */
	public void visitSwitchGroup(
		JSwitchGroup self,
		JSwitchLabel[] labels,
		JStatement[] stmts) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCatchClause(JCatchClause, JFormalParameter, JBlock)
	 */
	public void visitCatchClause(
		JCatchClause self,
		JFormalParameter exception,
		JBlock body) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitFormalParameters(JFormalParameter, boolean, CType, String)
	 */
	public void visitFormalParameters(
		JFormalParameter self,
		boolean isFinal,
		CType type,
		String ident) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitConstructorCall(JConstructorCall, boolean, JExpression[])
	 */
	public void visitConstructorCall(
		JConstructorCall self,
		boolean functorIsThis,
		JExpression[] params) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitArrayInitializer(JArrayInitializer, JExpression[])
	 */
	public void visitArrayInitializer(
		JArrayInitializer self,
		JExpression[] elems) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitBooleanLiteral(boolean)
	 */
	public void visitBooleanLiteral(boolean value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitByteLiteral(byte)
	 */
	public void visitByteLiteral(byte value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitCharLiteral(char)
	 */
	public void visitCharLiteral(char value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitDoubleLiteral(double)
	 */
	public void visitDoubleLiteral(double value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitFloatLiteral(float)
	 */
	public void visitFloatLiteral(float value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitIntLiteral(int)
	 */
	public void visitIntLiteral(int value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitLongLiteral(long)
	 */
	public void visitLongLiteral(long value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitShortLiteral(short)
	 */
	public void visitShortLiteral(short value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitStringLiteral(String)
	 */
	public void visitStringLiteral(String value) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitNullLiteral()
	 */
	public void visitNullLiteral() {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitPackageName(String)
	 */
	public void visitPackageName(String name) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitPackageImport(String)
	 */
	public void visitPackageImport(String name) {
	}

	/**
	 * @see org.caesarj.kjc.KjcVisitor#visitClassImport(String)
	 */
	public void visitClassImport(String name) {
	}
}
