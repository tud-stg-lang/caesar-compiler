package org.caesarj.compiler.joinpoint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.ast.FjSourceClass;
import org.caesarj.compiler.ast.FjSourceField;
import org.caesarj.compiler.ast.PrivilegedField;
import org.caesarj.compiler.ast.PrivilegedMethod;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CMethod;

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
		String aspectSig = CaesarBcelWorld.getInstance().resolve(aspect).getSignature(),
				declaringSig = 
					CaesarBcelWorld.getInstance().resolve(method.getOwner()).getSignature();
		CaesarMember member = 
			CaesarMember.ResolvedMember(
				CaesarMember.METHOD,
				declaringSig,
				method.getModifiers(),
				method.getIdent(),
				method.getSignature());
				
		CaesarMember	resolvedMethod = 
			CaesarMember.privilegedAccessMethodForMethod(aspectSig,member);

		privilegedMethod.setIdent(resolvedMethod.getName());
		privilegedMethod.setModifiers(resolvedMethod.getModifiers());

		return privilegedMethod;
	}

	/**
	 * Gets the accessed fields and methods as ResolvedMembers.
	 * Needed for Attribute creation.
	 * 
	 * @return CaesarMember[]
	 */
	public CaesarMember[]	getAccessedMembers(){
		CaesarMember[] accessedMembers =
			new CaesarMember[privilegedFields.size()
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
