package org.caesarj.compiler.ast;

import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.compiler.TokenReference;

public interface FjOverrideable {
	public CClass getCClass();
	public void setSuperClass( CReferenceType superType );		
	public String toSuperClass( String name );
	public TokenReference getTokenReference();
}
