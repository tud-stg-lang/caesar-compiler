package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CReferenceType;

public class FjOverridingCleanClassIfcImplDeclaration
	extends FjVirtualCleanClassIfcImplDeclaration implements FjOverrideable {

	public FjOverridingCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjClassDeclaration owner,
		FjCleanClassDeclaration baseDecl ) {
		super( tokenReference, ident, interfaces, methods, owner, baseDecl );
		modifiers = modifiers | FJC_OVERRIDE;
	}

	public String toSuperClass(String name) {
		return FjConstants.toProxyName( name );
	}
}
