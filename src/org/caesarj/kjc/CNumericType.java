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
 * $Id: CNumericType.java,v 1.1 2003-07-05 18:29:40 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.compiler.UnpositionedError;

/**
 * This class represents java and kopi numericals types
 * Such as byte, short, int, long, float, double
 */
public abstract class CNumericType extends CPrimitiveType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	typeID		the ident (int value) of this type
   */
  protected CNumericType(int typeID) {
    super(typeID);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this type.
   */
  public abstract String toString();

  /**
   * Returns the VM signature of this type.
   */
  public abstract String getSignature();

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  protected abstract void appendSignature(SimpleStringBuffer buffer);

  /**
   * Returns the stack size used by a value of this type.
   */
  public abstract int getSize();

  /**
   * Is this type ordinal ?
   */
  public abstract boolean isOrdinal();

  /**
   * Is this a floating point type ?
   */
  public abstract boolean isFloatingPoint();

  /**
   * Is this a numeric type ?
   */
  public boolean isNumeric() {
    return true;
  }

  // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    return this;
  }

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public abstract boolean isAssignableTo(CTypeContext context, CType dest);

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CType dest) {
    return dest.isNumeric();
  }

  /**
   * unaryPromote
   * search the type corresponding to the type after computation
   * @param t1 the type
   * @return the corresponding type after operation
   */
  public static CType unaryPromote(CExpressionContext context, CType t1) {

    TypeFactory         tf = context.getTypeFactory();

    if (t1 == tf.getPrimitiveType(TypeFactory.PRM_BYTE) 
        || t1 == tf.getPrimitiveType(TypeFactory.PRM_SHORT) 
        || t1 == tf.getPrimitiveType(TypeFactory.PRM_CHAR)) {
     return context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT);
    } else {
      return t1;
    }

  }

  /**
   * binaryPromote
   * search the type corresponding to the promotion of the two types
   * @param t1 the first type
   * @param t2 the second type
   * @return the corresponding type or null
   */
  public static CType binaryPromote(CExpressionContext context, CType t1, CType t2) {
    verify(t1.isNumeric() && t2.isNumeric());

    TypeFactory         tf = context.getTypeFactory();
    CType               primDouble = tf.getPrimitiveType(TypeFactory.PRM_DOUBLE);
    CType               primFloat = tf.getPrimitiveType(TypeFactory.PRM_FLOAT);
    CType               primLong = tf.getPrimitiveType(TypeFactory.PRM_LONG);

    if (t1 == primDouble) {
      return primDouble;
    } else if (t2 == primDouble) {
      return primDouble;
    } else if (t1 == primFloat) {
      return primFloat;
    } else if (t2 == primFloat) {
      return primFloat;
    } else if (t1 == primLong) {
      return primLong;
    } else if (t2 == primLong) {
      return primLong;
    } else {
      return tf.getPrimitiveType(TypeFactory.PRM_INT);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a bytecode sequence to convert a value of this type to the
   * specified destination type.
   * @param	dest		the destination type
   * @param	code		the code sequence
   */
  public abstract void genCastTo(CNumericType dest, GenerationContext context);
}
