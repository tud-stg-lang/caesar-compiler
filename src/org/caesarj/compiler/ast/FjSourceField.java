package org.caesarj.compiler.ast;

import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CSourceField;
import org.caesarj.kjc.CType;

public class FjSourceField extends CSourceField {

	protected FjFamily family;

	public FjSourceField(
		CClass owner,
		int modifiers,
		String ident,
		CType type,
		boolean deprecated,
		boolean synthetic,
		FjFamily family) {
		super(owner, modifiers, ident, type, deprecated, synthetic);
		this.family = family;
	}
	
	public FjFamily getFamily() {
		return family;
	}
}
