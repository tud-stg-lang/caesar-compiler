package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.PositionedError;

public interface FjResolveable {
	JClassDeclaration getOwnerDeclaration();
	void setSuperClass( CReferenceType superType );
	FjCleanClassDeclaration getBaseClass();
	void check(CContext context, boolean cond, MessageDescription description, Object param) throws PositionedError;
}
