package org.caesarj.compiler.ast;

import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CReferenceType;

public class FjOverridingCleanClassInterfaceDeclaration
	extends FjVirtualCleanClassInterfaceDeclaration implements FjOverrideable  {

	public FjOverridingCleanClassInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjClassDeclaration ownerDecl,
		FjCleanClassDeclaration baseDecl ) {
		super(tokenReference, ident, interfaces, methods, ownerDecl, baseDecl );
		modifiers = modifiers | FJC_OVERRIDE;
	}

	public String toSuperClass(String name) {
		return name;
	}
	
	/**
	 * It returns the allowed modifiers for this kind of interface
	 * @see familyj.compiler.FjInterfaceDeclaration#getAllowedModifiers()
	 */
	protected int getAllowedModifiers()
	{
		return super.getAllowedModifiers() | FJC_OVERRIDE;
	}
}
