package org.caesarj.compiler.ast;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CType;

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
