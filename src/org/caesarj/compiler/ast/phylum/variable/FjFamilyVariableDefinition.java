package org.caesarj.compiler.ast.phylum.variable;

import org.caesarj.compiler.ast.FjFamilyContext;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.family.FjFamily;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

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
