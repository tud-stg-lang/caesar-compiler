package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;

public class FjVirtualCleanClassInterfaceDeclaration
	extends FjCleanClassInterfaceDeclaration
	implements FjResolveable {

	protected FjClassDeclaration ownerDecl;

	public FjVirtualCleanClassInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjClassDeclaration owner,
		FjCleanClassDeclaration baseDecl ) {
		super(tokenReference, ident, modifiers | FJC_VIRTUAL, interfaces, methods, baseDecl );
		this.ownerDecl = owner;
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

	public void setSuperClass(CReferenceType superType) {
		CReferenceType[] newInterfaces = new CReferenceType[ 2 ];	
		newInterfaces[ 0 ] = interfaces[ 0 ];
		newInterfaces[ 1 ] = new CClassNameType( FjConstants.toIfcName( superType.toString() ) );
		interfaces = newInterfaces; 
	}	
	/**
	 * Returns the allowed modifiers for this kind of interface.
	 * @see familyj.compiler.FjInterfaceDeclaration#getAllowedModifiers()
	 * Walter
	 */
	protected int getAllowedModifiers()
	{
		return super.getAllowedModifiers() | FJC_VIRTUAL;
	}
}
