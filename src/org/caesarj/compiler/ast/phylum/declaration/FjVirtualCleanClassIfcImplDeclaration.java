package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class FjVirtualCleanClassIfcImplDeclaration
	extends FjCleanClassIfcImplDeclaration
	implements FjResolveable {

	protected JClassDeclaration ownerDecl;

	public FjVirtualCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifires,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		JClassDeclaration ownerDecl,
		FjCleanClassDeclaration baseDecl ) {
		super( tokenReference, ident, 
			modifires | FJC_VIRTUAL | ACC_PUBLIC | ACC_STATIC, 
			interfaces, methods, baseDecl );
		this.ownerDecl = ownerDecl;
	}

	public JClassDeclaration getOwnerDeclaration() {
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
