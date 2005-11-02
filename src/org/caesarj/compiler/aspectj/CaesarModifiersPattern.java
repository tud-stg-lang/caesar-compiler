package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.ModifiersPattern;

public class CaesarModifiersPattern extends ModifiersPattern {

	protected int required;
	protected int forbidden;
	
	public CaesarModifiersPattern(int requiredModifiers, int forbiddenModifiers) {
		super(requiredModifiers, forbiddenModifiers);
		this.required = requiredModifiers;
		this.forbidden = forbiddenModifiers;
	}
	
	public boolean hasRequired(int modifiers) {
		return (modifiers & required) == modifiers;
	}
}
