package org.caesarj.compiler.ast;

import java.util.EmptyStackException;
import java.util.Stack;

import org.caesarj.compiler.aspectj.CaesarFormalBinding;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CCompilationUnitContext;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

public class FjClassContext
	extends CClassContext
	implements FjAdditionalContext {

	protected CaesarFormalBinding[] bindings;

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

	public CaesarFormalBinding[] getBindings() {
		if (bindings == null) {
			bindings = new CaesarFormalBinding[0];
		}

		return bindings;
	}

	public void setBindings(CaesarFormalBinding[] formals) {
		bindings =  formals ;
	}

	public CCompilationUnitContext getParentCompilationUnitContext() {
		return (CCompilationUnitContext) parent;
	}


}
