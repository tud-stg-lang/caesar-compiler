package org.caesarj.compiler.ast;

import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;

public class FjSourceMethod extends CSourceMethod {

	protected FjFamily[] families;

	public FjSourceMethod(
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		CType[] paramTypes,
		CReferenceType[] exceptions,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JBlock body,
		FjFamily[] families) {
		super(
			owner,
			modifiers,
			ident,
			returnType,
			paramTypes,
			exceptions,
			typeVariables,
			deprecated,
			synthetic,
			body);
		this.families = families;
	}

	public FjFamily[] getFamilies() {
		return families;
	}
}
