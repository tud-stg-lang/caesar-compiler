package org.caesarj.compiler.ast;

import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.TypeX;

import org.caesarj.kjc.CField;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.CVoidType;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;

/**
 * A privileged field encapsulates a non-visible field and 
 * provides the corresponding access-methods.
 * 
 * @author Jürgen Hallpap
 */
public class PrivilegedField extends CField {

	/** the encapsulated non-visible baseField.*/
	private CField baseField;

	/** the reader access-method.*/
	private CMethod reader;

	/** the writer access-method.*/
	private CMethod writer;

	/** the resolved-member for attribute creation*/
	private ResolvedMember resolvedMember;

	/** the declaring type*/
	private TypeX declaringType;

	/**
	 * Constructor for PrivilegedField.
	 * @param baseField
	 * @param aspect
	 */
	public PrivilegedField(CField baseField, FjSourceClass aspect) {
		super(
			baseField.getOwner(),
			baseField.getModifiers(),
			baseField.getIdent(),
			baseField.getType(),
			baseField.isDeprecated(),
			baseField.isSynthetic());

		this.baseField = baseField;

		this.declaringType = CaesarBcelWorld.getInstance().resolve(owner);

		TypeX aspectType = CaesarBcelWorld.getInstance().resolve(aspect);

		Member field =
			new Member(
				Member.FIELD,
				declaringType,
				getModifiers(),
				getIdent(),
				getType().getSignature());

		ResolvedMember readerMember =
			AjcMemberMaker.privilegedAccessMethodForFieldGet(aspectType, field);

		CType[] readerParameterTypes = { getOwnerType()};
		reader =
			new FjSourceMethod(
				owner,
				readerMember.getModifiers(),
				readerMember.getName(),
				getType(),
				readerParameterTypes,
				CReferenceType.EMPTY,
				CTypeVariable.EMPTY,
				false,
				true,
				null,
				new FjFamily[0]);

		ResolvedMember writerMember =
			AjcMemberMaker.privilegedAccessMethodForFieldSet(aspectType, field);

		CType[] writerParameterTypes = { getOwnerType(), getType()};
		writer =
			new FjSourceMethod(
				owner,
				writerMember.getModifiers(),
				writerMember.getName(),
				new CVoidType(),
				writerParameterTypes,
				CReferenceType.EMPTY,
				CTypeVariable.EMPTY,
				false,
				true,
				null,
				new FjFamily[0]);

	}

	/**
	 * Gets the appropriate accessor.
	 * 
	 * @param isReadAccess
	 * @return CMethod
	 */
	public CMethod getAccessMethod(boolean isReadAccess) {
		if (isReadAccess)
			return reader;
		else
			return writer;
	}

	/**
	 * Gets the resolvedMember.
	 * 
	 * @return ResolvedMember
	 */
	public ResolvedMember getResolvedMember() {
		if (resolvedMember == null) {
			resolvedMember =
				new ResolvedMember(
					Member.FIELD,
					declaringType,
					getModifiers(),
					getIdent(),
					getType().getSignature());
		}

		return resolvedMember;
	}

}
