package org.caesarj.compiler.export;

import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.UnpositionedError;

public class FjSourceMethod extends CSourceMethod {

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
		JBlock body) {
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
	}

	/**
	 * Checks if the modifiers (provided and expected) are changed in the 
	 * overriding.
	 */
	public void checkOverriding(
		CTypeContext context,
		CMethod superMethod,
		CReferenceType[] substitution)
		throws UnpositionedError
	{
		if (CModifier.contains(getModifiers(), CCI_EXPECTED | CCI_PROVIDED)
			&& ! CModifier.contains(
					getModifiers() & superMethod.getModifiers(),
					CCI_EXPECTED | CCI_PROVIDED))
			throw new UnpositionedError(
				CaesarMessages.CI_METHOD_FLAGS_CHANGED_IN_OVERRIDING,
				this);

		super.checkOverriding(context, superMethod, substitution);
	}	
}
