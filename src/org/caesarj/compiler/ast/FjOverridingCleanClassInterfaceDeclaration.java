package org.caesarj.compiler.ast;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.TokenReference;

public class FjOverridingCleanClassInterfaceDeclaration
	extends FjVirtualCleanClassInterfaceDeclaration implements FjOverrideable  {

	public FjOverridingCleanClassInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		JClassDeclaration ownerDecl,
		CaesarClassDeclaration baseDecl ) {
		super(tokenReference, ident, modifiers | FJC_OVERRIDE, interfaces, 
			methods, ownerDecl, baseDecl );
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
	public CReferenceType getProviding()
	{
		return baseDecl.getProviding();
	}

}
