package org.caesarj.compiler.ast;

import org.caesarj.compiler.PositionedError;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.util.MessageDescription;

public interface FjResolveable {
	FjClassDeclaration getOwnerDeclaration();
	void setSuperClass( CReferenceType superType );
	FjCleanClassDeclaration getBaseClass();
	void check(CContext context, boolean cond, MessageDescription description, Object param) throws PositionedError;
}
