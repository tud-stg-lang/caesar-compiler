package org.caesarj.compiler.export;

import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.TokenReference;

/**
 * Type comment.
 * 
 * @author Jürgen Hallpap
 */
public class CCjPrivilegedMethod extends CSourceMethod {

	private CaesarMember resolvedMember;

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
	public CCjPrivilegedMethod(CMethod baseMethod) {
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
				new JavaStyleComment[0])
			);

		this.baseMethod = baseMethod;
	}

	public CaesarMember getResolvedMember() {
		if (resolvedMember == null) {
			String	declaringSig = 
				CaesarBcelWorld.getInstance().resolve(getOwner()).getSignature();
			resolvedMember =
				CaesarMember.ResolvedMember(
					CaesarMember.METHOD,
					declaringSig,
					getModifiers(),
					baseMethod.getIdent(),
					getSignature());
		}

		return resolvedMember;
	}

}
