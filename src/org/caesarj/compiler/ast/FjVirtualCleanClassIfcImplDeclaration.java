package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;

public class FjVirtualCleanClassIfcImplDeclaration
	extends FjCleanClassIfcImplDeclaration
	implements FjResolveable {

	protected FjClassDeclaration ownerDecl;

	public FjVirtualCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifires,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjClassDeclaration ownerDecl,
		FjCleanClassDeclaration baseDecl ) {
		super( tokenReference, ident, 
			modifires | FJC_VIRTUAL | ACC_PUBLIC | ACC_STATIC, 
			interfaces, methods, baseDecl );
		this.ownerDecl = ownerDecl;
	}

	public FjClassDeclaration getOwnerDeclaration() {
		return ownerDecl;
	}

	public void join(CContext context) throws PositionedError {
		FjVirtualClassDeclaration.resolveQualifiedSuperClass( context, this );
		try {
			super.join(context);
		} catch( PositionedError e ) {
			// an error occuring here will occur in the
			// base class this class is derived from, too
		}
	}
}
