package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;

/**
 * It represents the source methods. It extends the FjSourceMethod to insert 
 * checks when the method is overriden.
 *  
 * @author Walter Augusto Werner
 */
public class CciSourceMethod extends FjSourceMethod
{

	/**
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param body
	 * @param families
	 */
	public CciSourceMethod(
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
		FjFamily[] families)
	{
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
			body,
			families);
	}

	/**
	 * Checks if the modifiers (provided and expected) are changed in the 
	 * overriding.
	 *  
	 * @see at.dms.kjc.CMethod#checkOverriding(at.dms.kjc.CTypeContext, at.dms.kjc.CMethod, at.dms.kjc.CReferenceType[])
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
