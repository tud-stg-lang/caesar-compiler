package org.caesarj.compiler.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.TypeX;

import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;

/**
 * Type comment.
 * 
 * @author Jürgen Hallpap
 */
public class PrivilegedMethod extends FjSourceMethod {

	private ResolvedMember resolvedMember;

	private CMethod baseMethod;

	/**
	 * Constructor for PrivilegedMethod.
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
	public PrivilegedMethod(CMethod baseMethod) {
		super(
			baseMethod.getOwner(),
			baseMethod.getModifiers(),
			baseMethod.getIdent(),
			baseMethod.getReturnType(),
			baseMethod.getParameters(),
			CReferenceType.EMPTY,
			CTypeVariable.EMPTY,
			false,
			true,
			new JBlock(
				TokenReference.NO_REF,
				JBlock.EMPTY,
				new JavaStyleComment[0]),
			((FjSourceMethod) baseMethod).getFamilies());

		this.baseMethod = baseMethod;
	}

	public ResolvedMember getResolvedMember() {
		if (resolvedMember == null) {
			TypeX declaringType =
				CaesarBcelWorld.getInstance().resolve(getOwner());

			resolvedMember =
				new ResolvedMember(
					Member.METHOD,
					declaringType,
					getModifiers(),
					baseMethod.getIdent(),
					getSignature());
		}

		return resolvedMember;
	}

}
