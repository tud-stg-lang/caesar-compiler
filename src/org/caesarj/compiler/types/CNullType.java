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
 * $Id: CNullType.java,v 1.2 2004-10-15 11:12:54 aracic Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CBadClass;
import org.caesarj.compiler.export.CClass;

/**
 * This class represents null class type in the type structure
 */
public class CNullType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   */
  public CNullType() {
    super(new CBadClass("<NULL TYPE>"));
    type = TID_NULL;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    return dest.isReference();
  }
  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] s) {
    return isAssignableTo(context, dest);
  }
  public boolean isAssignableTo(CTypeContext context, CType dest, boolean i) {
    return isAssignableTo(context, dest);
  }

  public boolean equals(CType other) {
    if (other == this) {
      return true;
    }
    if (!other.isClassType() 
        || other.isArrayType()
        || other.isTypeVariable()
        || ((CReferenceType)other).getCClass() != getCClass()) {
      return false;
    } else {
      return true;
    }
  }

  public boolean equals(CType other, CReferenceType[] substitution) {
    return equals(other);
  }

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CType dest) {
    return dest.isReference();
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
    return CStdType.Object.getCClass();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Transforms this type to a string
   */
  public String toString() {
    return "null-literal";
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public int getSize() {
    return 1;
  }
}
