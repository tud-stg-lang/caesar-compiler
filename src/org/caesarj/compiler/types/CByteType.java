/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
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
 * $Id: CByteType.java,v 1.2 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.SimpleStringBuffer;

/**
 * This class represents the Java type "byte".
 * There is only one instance of this type.
 */
public class CByteType extends CNumericType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new instance.
   */
  public CByteType() {
    super(TID_BYTE);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this type.
   */
  public String toString() {
    return "byte";
  }

  /**
   * Returns the VM signature of this type.
   */
  public String getSignature() {
    return "B";
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  public void appendSignature(SimpleStringBuffer buffer) {
    buffer.append('B');
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public int getSize() {
    return 1;
  }

  /**
   * Is this type ordinal ?
   */
  public boolean isOrdinal() {
    return true;
  }

  /**
   * Is this a floating point type ?
   */
  public boolean isFloatingPoint() {
    return false;
  }

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    if (dest == this) {
      // JLS 5.1.1 Identity Conversion
      return true;
    } else {
      // JLS 5.1.2 Widening Primitive Conversion
      switch (dest.getTypeID()) {
      case TID_SHORT:
      case TID_INT:
      case TID_LONG:
      case TID_FLOAT:
      case TID_DOUBLE:
	return true;
      default:
	return false;
      }
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
  public void genCastTo(CNumericType dest, GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    if (dest != this) {
      switch (dest.type) {
      case TID_CHAR:
	code.plantNoArgInstruction(opc_i2c);
	break;

      case TID_SHORT:
	code.plantNoArgInstruction(opc_i2s);
	break;

      case TID_INT:
	break;

      case TID_LONG:
	code.plantNoArgInstruction(opc_i2l);
	break;

      case TID_FLOAT:
	code.plantNoArgInstruction(opc_i2f);
	break;

      case TID_DOUBLE:
	code.plantNoArgInstruction(opc_i2d);
	break;

      default:
	throw new InconsistencyException();
      }
    }
  }
}
