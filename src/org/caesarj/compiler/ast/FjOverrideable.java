package org.caesarj.compiler.ast;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.TokenReference;

public interface FjOverrideable {
	public CClass getCClass();
	public void setSuperClass( CReferenceType superType );		
	public String toSuperClass( String name );
	public TokenReference getTokenReference();
	public CReferenceType getProviding();
}
