package org.caesarj.compiler.ast;

import java.util.EmptyStackException;
import java.util.Stack;

import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CInterfaceContext;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

public class FjInterfaceContext extends CInterfaceContext
	implements FjAdditionalContext {

	public FjInterfaceContext(
		CContext parent,
		KjcEnvironment environment,
		CSourceClass clazz,
		JTypeDeclaration decl) {
		super(parent, environment, clazz, decl);
		fjAdditionalContextStack = new Stack();
	}
	
	public CClassContext getParent() {
		return this;
	}
	
	protected Stack fjAdditionalContextStack;
	public void pushContextInfo( Object o ) {
		fjAdditionalContextStack.push( o );
	}
	public Object popContextInfo() {
		try {
			return fjAdditionalContextStack.pop();
		} catch( EmptyStackException e ) {
			return null;
		}
	}
	public Object peekContextInfo() {
		try {
			return peekContextInfo( 0 );
		} catch( Throwable t ) {
			return null;
		}
	}	
	public Object peekContextInfo( int howDeep ) {
		try {
			return fjAdditionalContextStack.elementAt(
				fjAdditionalContextStack.size() - howDeep - 1 );
		} catch( Throwable t ) {
			return null;
		}
	}
}