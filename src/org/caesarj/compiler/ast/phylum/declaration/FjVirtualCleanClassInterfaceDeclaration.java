package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class FjVirtualCleanClassInterfaceDeclaration
	extends FjCleanClassInterfaceDeclaration
	implements FjResolveable {

	protected JClassDeclaration ownerDecl;

	public FjVirtualCleanClassInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		JClassDeclaration owner,
		FjCleanClassDeclaration baseDecl ) {
		super(tokenReference, ident, modifiers | FJC_VIRTUAL, interfaces, methods, baseDecl );
		this.ownerDecl = owner;
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
