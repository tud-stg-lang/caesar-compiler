package org.caesarj.compiler.ast;

import org.caesarj.kjc.CType;
import org.caesarj.kjc.JExpression;
import org.caesarj.compiler.TokenReference;

public class FjFamilyVariableDefinition extends FjVariableDefinition {

	public FjFamilyVariableDefinition(
		TokenReference where,
		int modifiers,
		CType type,
		String ident,
		JExpression initializer,
		FjFamily family ) {
		super(where, modifiers, type, ident, initializer);
		if( family != null )
			FjFamilyContext.getInstance().setFamilyOf( this, family );
	}
}
