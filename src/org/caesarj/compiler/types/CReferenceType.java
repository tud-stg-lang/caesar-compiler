/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: CReferenceType.java,v 1.6 2004-06-15 16:42:04 aracic Exp $
 */

package org.caesarj.compiler.types;

import java.util.Hashtable;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CBadClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents class type in the type structure
 */
public class CReferenceType extends CType {

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Construct a class type
	 * @param	clazz		the class that will represent this type
	 */
	protected CReferenceType() {
		super(TID_CLASS);

		this.clazz = BAC_CLASS;
		this.arguments = new CReferenceType[][] { EMPTY };
	}

	/**
	 * Construct a class type
	 * @param	clazz		the class that will represent this type
	 */
	public CReferenceType(CClass clazz) {
		super(TID_CLASS);

		this.clazz = clazz;
		this.arguments = new CReferenceType[][] { EMPTY };

		if (!(this instanceof CArrayType)) {
			allCReferenceType.put(clazz.getQualifiedName(), this);
		}
	}

	public static CReferenceType lookup(String qualifiedName) {
		if (qualifiedName.indexOf('/') >= 0) {
			CReferenceType type =
				(CReferenceType) allCReferenceType.get(qualifiedName);

			if (type == null) {
				type = new CClassNameType(qualifiedName, false);
				allCReferenceType.put(qualifiedName, type);
			}

			return type;
		}
		else {
			return new CClassNameType(qualifiedName, false);
		}
	}

	public boolean isChecked() {
		return clazz != BAC_CLASS;
	}

	protected void setClass(CClass clazz) {
		this.clazz = clazz;
	}

	// ----------------------------------------------------------------------
	// ACCESSORS
	// ----------------------------------------------------------------------

	/**
	 * equals
	 */
	//   public boolean equals(CType other) {
	//     return (!other.isClassType() || other.isArrayType()) ?
	//       false :
	//       ((CReferenceType)other).getCClass() == getCClass();
	//   }

	/**
	 * Transforms this type to a string
	 */
	public String toString() {
		if (clazz.isNested()) {
			return clazz.getIdent() + printArgs();
		}
		else {
			return getQualifiedName().replace('/', '.') + printArgs();
		}
	}

	private String printArgs() {
		if (arguments == null || !getCClass().isGenericClass()) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append('<');
		for (int i = 0; i < arguments[arguments.length - 1].length; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(arguments[arguments.length - 1][i]);
		}
		buffer.append('>');

		return buffer.toString();
	}

	/**
	 * Appends the VM signature of this type to the specified buffer.
	 */
	public void appendSignature(SimpleStringBuffer buffer) {
		buffer.append('L');
		buffer.append(getQualifiedName());
		buffer.append(';');
	}
	/**
	 * @return the short name of this class
	 */
	public String getIdent() {
		return getCClass().getIdent();
	}

    /**
     *
     */
    public String getQualifiedName() {
        return getCClass().getQualifiedName();
    }

    /**
     *
     */
    public String getImplQualifiedName() {
        return getCClass().getImplQualifiedName();
    }

	/**
	 * Returns the stack size used by a value of this type.
	 */
	public int getSize() {
		return 1;
	}

	/**
	 * Check if a type is a reference
	 * @return	is it a type that accept null value
	 */
	public boolean isReference() {
		return true;
	}

	/**
	 * Check if a type is a class type
	 * @return	is it a subtype of ClassType ?
	 */
	public boolean isClassType() {
		return true;
	}

	/**
	 * Returns the class object associated with this type
	 *
	 * If this type was never checked (read from class files)
	 * check it!
	 *
	 * @return the class object associated with this type
	 */
	public CClass getCClass() {
		// !!! graf 000213
		// !!! should have been checked (see JFieldAccessExpression)
		verify(clazz != BAC_CLASS);
		// !!! graf 000213
		if (clazz == null) {
			if (this == CStdType.Object) {
				throw new InconsistencyException("java.lang.Object is not in the classpath !!!");
			}
			clazz = CStdType.Object.getCClass();
		}

		return clazz;
	}

	public CType getErasure(CTypeContext context) throws UnpositionedError {
		return new CErasedReferenceType(getCClass());
	}
	// ----------------------------------------------------------------------
	// INTERFACE CHECKING
	// ----------------------------------------------------------------------

	/**
	 * check that type is valid
	 * necessary to resolve String into java/lang/String
	 * @param	context		the context (may be be null)
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CType checkType(CTypeContext context) throws UnpositionedError {
		return this;
	}

	// ----------------------------------------------------------------------
	// BODY CHECKING
	// ----------------------------------------------------------------------

	/**
	 * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
	 * @param	dest		the destination type
	 * @return	true iff the conversion is valid
	 */
	public boolean isAssignableTo(CTypeContext context, CType dest) {
		return isAssignableTo(context, dest, false);
	}
	/**
	 * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
	 * @param	dest		the destination type
	 * @return	true iff the conversion is valid
	 */
	public boolean isAssignableTo(
		CTypeContext context,
		CType dest,
		boolean inst) {
		if (!(dest.isClassType() && !dest.isArrayType())) {
			return false;
		}
		return isAssignableTo(context, dest, dest.getArguments(), inst);
	}
	public boolean isAssignableTo(
		CTypeContext context,
		CType dest,
		CReferenceType[] substitution,
		boolean inst) {
		if (!(dest.isClassType() && !dest.isArrayType())) {
			return false;
		}
		if (dest.isTypeVariable()) {
			if (!inst) {
				return false;
			}
			else {
				CReferenceType[] destBounds =
					((CTypeVariable) dest).getBounds();

				if (destBounds.length == 0) {
					return true; // bound = java.lang.Object
				}
				for (int i = 0; i < destBounds.length; i++) {
					if (!isAssignableTo(context,
						destBounds[i],
						substitution)) {
						return false;
					}
				}
				return true;
			}
		}
		else {
			return getCClass().descendsFrom(
				((CReferenceType) dest),
				arguments[arguments.length - 1],
				substitution);
		}
		//  return true;
	}

	public boolean isAssignableTo(
		CTypeContext context,
		CType dest,
		CReferenceType[] substitution) {
		if (!(dest.isClassType() && !dest.isArrayType())) {
			return false;
		}
		if (dest.isTypeVariable()) {
			dest = substitution[((CTypeVariable) dest).getIndex()];
		}
		if (dest.isTypeVariable()) {
			return false;
		}
		else {
			//      return getCClass().descendsFrom(((CReferenceType)dest), CReferenceType.EMPTY_ARG, substitution)
			return getCClass().descendsFrom(
				((CReferenceType) dest).getCClass());
		}
		//  return true;
	}

	/** equals */
	public boolean equals(CType other) {
		if (other == this) {
			return true;
		}
		if (!other.isClassType()
			|| other.isArrayType()
			|| other.isTypeVariable()
			|| ((CReferenceType) other).getCClass() != getCClass()
			|| other.isGenericType() != isGenericType()) {
			return false;
		}
		CReferenceType[] otherArgs = other.getArguments();

		if (otherArgs.length != arguments[arguments.length - 1].length) {
			return false;
		}

		for (int i = 0; i < arguments[arguments.length - 1].length; i++) {
			if (!otherArgs[i].equals(arguments[arguments.length - 1][i])) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(CType other, CReferenceType[] substitution) {
		if (other.isTypeVariable()) {
			other = substitution[((CTypeVariable) other).getIndex()];
		}
		if (other == this) {
			return true;
		}
		if (!other.isClassType()
			|| other.isArrayType()
			|| other.isTypeVariable()
			|| (other.isGenericType() != isGenericType())
			|| ((CReferenceType) other).getCClass() != getCClass()) {
			return false;
		}
		CReferenceType[] otherArgs = other.getArguments();

		if (otherArgs.length != arguments[arguments.length - 1].length) {
			return false;
		}
		for (int i = 0; i < arguments[arguments.length - 1].length; i++) {
			CReferenceType arg = arguments[arguments.length - 1][i];

			if (arg.isTypeVariable()) {
				arg = substitution[((CTypeVariable) arg).getIndex()];
			}
			if (!arg.equals(otherArgs[i], substitution)) {
				return false;
			}
		}
		return true;
	}

	public CReferenceType[] getArguments() {
		return CReferenceType.EMPTY;
	}
	public CReferenceType[][] getAllArguments() {
		return new CReferenceType[][] { CReferenceType.EMPTY };
	}

	/**
	 * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
	 * @param	dest		the destination type
	 * @return	true iff the conversion is valid
	 */
	public boolean isCastableTo(CType dest) {
		// test for array first because array types are classes

		if (getCClass().isInterface()) {
			if (!dest.isClassType()) {
				return false;
			}
			else if (dest.getCClass().isInterface()) {
				// if T is an interface type and if T and S contain methods
				// with the same signature but different return types,
				// then a compile-time error occurs.
				//!!! graf 000512: FIXME: implement this test
				return true;
			}
			else if (!dest.getCClass().isFinal()) {
				return true;
			}
			else {
				return dest.getCClass().descendsFrom(getCClass());
			}
		}
		else {
			// this is a class type
			if (dest.isArrayType()) {
				return equals(CStdType.Object);
			}
			else if (!dest.isClassType()) {
				return false;
			}
			else if (dest.getCClass().isInterface()) {
				if (!getCClass().isFinal()) {
					return true;
				}
				else {
					return getCClass().descendsFrom(dest.getCClass());
				}
			}
			else {
				return getCClass().descendsFrom(dest.getCClass())
					|| dest.getCClass().descendsFrom(getCClass());
			}
		}
	}

	/**
	 *
	 */
	public boolean isCheckedException(CTypeContext context) {
		return !isAssignableTo(
			context,
			context.getTypeFactory().createReferenceType(
				TypeFactory.RFT_RUNTIMEEXCEPTION))
			&& !isAssignableTo(context,
				context.getTypeFactory().createReferenceType(
					TypeFactory.RFT_ERROR));
	}

	public CReferenceType createSubstitutedType(
		CClass local,
		CReferenceType prefixType,
		CReferenceType[][] substitution) {
		CReferenceType[][] arguments;
		CReferenceType[][] prefixArgs =
			prefixType == null
				? CReferenceType.EMPTY_ARG
				: prefixType.getAllArguments();

		if (!getCClass().isNested() || prefixType == null) {
			arguments = new CReferenceType[][] { getArguments()};
		}
		else {
			arguments = new CReferenceType[prefixArgs.length + 1][];
			System.arraycopy(prefixArgs, 0, arguments, 0, prefixArgs.length);
			arguments[prefixArgs.length] = getArguments();
		}
		if (prefixType != null) {
			substitution = prefixType.getAllArguments();
			local = prefixType.getCClass();
		}

		if (arguments.length > 0) {
			int index = arguments.length - 1;
			CReferenceType[] subArgs =
				new CReferenceType[arguments[index].length];

			for (int i = 0; i < arguments[index].length; i++) {
				CReferenceType type = arguments[index][i];

				if (type.isTypeVariable()) {
					subArgs[i] =
						local.getSubstitution(
							(CTypeVariable) type,
							substitution);
				}
				else if (type.isGenericType()) {
					subArgs[i] =
						((CClassOrInterfaceType) type).createSubstitutedType(
							local,
							null,
							substitution);
				}
				else {
					subArgs[i] = type;
				}
			}

			arguments[index] = subArgs;
		}
		CClassOrInterfaceType type =
			new CClassOrInterfaceType(getCClass(), arguments);

		type.setChecked(true);

		return type;
	}
	// ----------------------------------------------------------------------
	// INITIALIZERS METHODS
	// ----------------------------------------------------------------------

	public static void init(CompilerBase compiler) {
		allCReferenceType = new Hashtable(2000);
	}

	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	public static final CReferenceType[] EMPTY = new CReferenceType[0];
	public static final CReferenceType[][] EMPTY_ARG = new CReferenceType[0][];

	private static Hashtable allCReferenceType = new Hashtable(2000);
	private static final CClass BAC_CLASS = new CBadClass("<NOT YET DEFINED>");

	private CClass clazz;
	protected CReferenceType[][] arguments;
}
