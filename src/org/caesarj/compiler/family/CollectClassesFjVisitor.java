package org.caesarj.compiler.family;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjVisitor;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;

public class CollectClassesFjVisitor extends FjVisitor {

	protected Hashtable allClasses;
	protected Hashtable markedClasses;
	protected boolean transformationIsDone;

	public CollectClassesFjVisitor() {
		super();
		allClasses = new Hashtable();
		markedClasses = new Hashtable();
		transformationIsDone = false;
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

		if( collectClass( self ) )
			// collect the class
			allClasses.put( self.getCClass().getQualifiedName(), self );

		super.visitFjClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls);
	}

	protected FjClassDeclaration findNext() throws PositionedError {
		Enumeration e = allClasses.elements();
		while( e.hasMoreElements() ) {
			FjClassDeclaration current =
				(FjClassDeclaration) e.nextElement();
			
			String classKey = current.getCClass().getQualifiedName();
			if( markedClasses.get( classKey ) != null )
				continue;
						
			if( returnClass( current ) ) {
				// mark this class
				markedClasses.put( classKey, current );
				return current;				
			}
		}
		return null;
	}

	protected boolean returnClass( FjClassDeclaration decl ) throws PositionedError {
		return true;
	}

	protected boolean collectClass( FjClassDeclaration decl ) {
		return true;
	}
	
	public Vector transform() throws PositionedError {
		return new Vector();
	}
}