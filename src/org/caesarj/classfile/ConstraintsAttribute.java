/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: ConstraintsAttribute.java,v 1.4 2004-04-14 11:49:13 klose Exp $
 */

package org.caesarj.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ConstraintsAttribute extends Attribute {

  public ConstraintsAttribute(MethodDescription pre, MethodDescription post) {
    if (pre != null) {
      precondition = new MethodRefConstant(pre.getName(), pre.getType());
    } else {
      precondition = null;
    }
    if (post != null) {
      postcondition = new MethodRefConstant(post.getName(), post.getType());
    } else {
      postcondition = null;
    }
  }

  public ConstraintsAttribute(DataInput in, ConstantPool cp) 
    throws IOException, ClassFileFormatException {

    if (in.readInt() != 4) {
      throw new ClassFileFormatException("bad attribute length");
    }
    int       idxPre = in.readUnsignedShort();
    int       idxPost = in.readUnsignedShort();

    precondition = (idxPre==0) ? null : (MethodRefConstant)cp.getEntryAt(idxPre);
    postcondition = (idxPost==0) ? null : (MethodRefConstant)cp.getEntryAt(idxPost);;
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ public int getTag() {
    return ClassfileConstants2.ATT_CONSTRAINTS;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ protected int getSize() {
    return 2 + 4 + 4;
  }


  /**
   * @return null if there is no Precondition
   */
  public MethodDescription getPrecondition() {
    if (precondition == null) {
      return null;
    } else {
      return new MethodDescription(precondition.getTypeName(),
                                   precondition.getType(),
                                   precondition.getClassName());
    }
  }

  /**
   * @return null if there is no Postcondition
   */
  public MethodDescription getPostcondition() {
    if (postcondition == null) {
      return null;
    } else {
      return new MethodDescription(postcondition.getTypeName(),
                                   postcondition.getType(),
                                   postcondition.getClassName());
    }
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
   protected void resolveConstants(ConstantPool cp)  throws ClassFileFormatException {
    cp.addItem(attr);
    if (precondition != null) cp.addItem(precondition);
    if (postcondition != null) cp.addItem(postcondition);
  }

  /**
   * Write this class into the the file (out) getting data position from
   * the constant pool
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  /*package*/ protected void write(ConstantPool cp, DataOutput out) throws IOException {
    out.writeShort(attr.getIndex());
    out.writeInt(4);
    out.writeShort((precondition == null) ? 0 : precondition.getIndex());
    out.writeShort((postcondition == null) ?  0 : postcondition.getIndex());
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public final static String    NAME = "Constraints";
  private MethodRefConstant     precondition;
  private MethodRefConstant     postcondition;
  private static AsciiConstant  attr = new AsciiConstant(NAME);
}
