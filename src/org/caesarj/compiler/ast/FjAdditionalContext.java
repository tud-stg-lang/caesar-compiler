package org.caesarj.compiler.ast;

import org.caesarj.compiler.context.CClassContext;

public interface FjAdditionalContext {
	void pushContextInfo( Object o );
	Object popContextInfo();
	Object peekContextInfo();
	Object peekContextInfo( int howDeep );
	CClassContext getParent();
}
