package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CReferenceType;

public class FjOverridingCleanClassIfcImplDeclaration
	extends FjVirtualCleanClassIfcImplDeclaration implements FjOverrideable {

	public FjOverridingCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjClassDeclaration owner,
		FjCleanClassDeclaration baseDecl ) {
		super( tokenReference, ident, modifiers | FJC_OVERRIDE, 
			interfaces, methods, owner, baseDecl );
	}

	public String toSuperClass(String name) {
		return FjConstants.toProxyName( name );
	}
}
