package org.caesarj.compiler.ast;

import java.util.EmptyStackException;
import java.util.Stack;

import org.aspectj.weaver.patterns.FormalBinding;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CCompilationUnitContext;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

public class FjClassContext
	extends CClassContext
	implements FjAdditionalContext {

	protected FormalBinding[] bindings;

	public FjClassContext(
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
	public void pushContextInfo(Object o) {
		fjAdditionalContextStack.push(o);
	}
	public Object popContextInfo() {
		try {
			return fjAdditionalContextStack.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	}
	public Object peekContextInfo() {
		try {
			return peekContextInfo(0);
		} catch (Exception e) {
			return null;
		}
	}
	public Object peekContextInfo(int howDeep) {
		try {
			return fjAdditionalContextStack.elementAt(
				fjAdditionalContextStack.size() - howDeep - 1);
		} catch (Exception e) {
			return null;
		}
	}

	public FormalBinding[] getBindings() {
		if (bindings == null) {
			bindings = new FormalBinding[0];
		}

		return bindings;
	}

	public void setBindings(FormalBinding[] formals) {
		this.bindings = formals;
	}

	public CCompilationUnitContext getParentCompilationUnitContext() {
		return (CCompilationUnitContext) parent;
	}

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.CTypeContext#lookupClass(org.caesarj.kjc.CClass, java.lang.String)
	 */
	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError
	{
		// TODO Auto-generated method stub
		return super.lookupClass(caller, name);
	}

}
