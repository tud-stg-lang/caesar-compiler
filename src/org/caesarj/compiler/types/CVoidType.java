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
 * $Id: CVoidType.java,v 1.2 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents java and kopi numericals types
 * Such as byte, short, int, long, float, double
 */

/* andreas start
 * 
 * package access doesn't allow
 * using the class in familyj.*
final class CVoidType extends CType {
 */
final public class CVoidType extends CType {
// andreas end

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public CVoidType() {
    super(TID_VOID);
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
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    return false;
  }

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CType dest) {
    return false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Transforms this type to a string
   */
  public String toString() {
    return "void";
  }

  /**
   * Returns the VM signature of this type.
   */
  public String getSignature() {
    return "V";
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  public void appendSignature(SimpleStringBuffer buffer) {
    buffer.append('V');
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public int getSize() {
    return 0;
  }
}
