package org.caesarj.compiler.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.TypeX;

import org.caesarj.kjc.CField;
import org.caesarj.kjc.CMethod;
import org.caesarj.compiler.ast.FjSourceClass;
import org.caesarj.compiler.ast.FjSourceField;
import org.caesarj.compiler.ast.PrivilegedField;
import org.caesarj.compiler.ast.PrivilegedMethod;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;

/**
 * Handles the privileged access to invisible (private, protected, package-access) fields and methods.
 * 
 * @author Jürgen Hallpap
 */
public class PrivilegedAccessHandler {

	/** Map: CField -> PrivilegedField*/
	private Map privilegedFields = new HashMap();

	/** Map: CMethod -> PrivilegedMethod*/
	private Map privilegedMethods = new HashMap();

	/** The associated privileged aspect.*/
	private FjSourceClass aspect;

	/**
	 * Constructor for CaesarPrivilegeHandler.
	 */
	public PrivilegedAccessHandler() {
		super();
	}

	/**
	 * Gets to the given CField the corresponding PrivilegedField.
	 * 
	 * @param field
	 * @return PrivilegedField
	 */
	public PrivilegedField getPrivilegedAccessField(CField field) {
		PrivilegedField priviligedField =
			(PrivilegedField) privilegedFields.get(field);

		if (priviligedField == null) {
			priviligedField =
				new PrivilegedField(
					field,
					aspect,
					((FjSourceField) field).getFamily());
			privilegedFields.put(field, priviligedField);
		}

		return priviligedField;
	}

	/**
	 * Gets to the given CMethod the corresponding PrivilegedMethod.
	 * 
	 * @param method
	 * @return CMethod
	 */
	public CMethod getPrivilegedAccessMethod(CMethod method) {
		CMethod privilegedMethod =
			(PrivilegedMethod) privilegedMethods.get(method);

		if (privilegedMethod == null) {
			privilegedMethod = new PrivilegedMethod(method);
			privilegedMethods.put(method, privilegedMethod);
		}

		TypeX aspectType = CaesarBcelWorld.getInstance().resolve(aspect);
		TypeX declaringType =
			CaesarBcelWorld.getInstance().resolve(method.getOwner());

		ResolvedMember member =
			new ResolvedMember(
				Member.METHOD,
				declaringType,
				method.getModifiers(),
				method.getIdent(),
				method.getSignature());

		ResolvedMember resolvedMethod =
			AjcMemberMaker.privilegedAccessMethodForMethod(aspectType, member);

		privilegedMethod.setIdent(resolvedMethod.getName());
		privilegedMethod.setModifiers(resolvedMethod.getModifiers());

		return privilegedMethod;
	}

	/**
	 * Gets the accessed fields and methods as ResolvedMembers.
	 * Needed for Attribute creation.
	 * 
	 * @return ResolvedMember[]
	 */
	public ResolvedMember[] getAccessedMembers() {

		ResolvedMember[] accessedMembers =
			new ResolvedMember[privilegedFields.size()
				+ privilegedMethods.size()];

		Iterator iterator = privilegedFields.values().iterator();
		int i = 0;
		for (; iterator.hasNext(); i++) {
			PrivilegedField privField = (PrivilegedField) iterator.next();

			accessedMembers[i] = privField.getResolvedMember();

		}

		iterator = privilegedMethods.values().iterator();
		for (; iterator.hasNext(); i++) {
			PrivilegedMethod privMethod = (PrivilegedMethod) iterator.next();

			accessedMembers[i] = privMethod.getResolvedMember();

		}

		return accessedMembers;
	}

	/**
	 * Sets the aspect.
	 * 
	 * @param aspect
	 */
	public void setAspect(FjSourceClass aspect) {
		this.aspect = aspect;
	}

}
