/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: DeprecatedAttribute.java,v 1.5 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * VMS 4.7.10 : Deprecated Attribute.
 *
 * This attribute marks superseded classes, fields and methods.
 */
public class DeprecatedAttribute extends Attribute {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Create a deprecated attribute.
   */
  public DeprecatedAttribute() {
    this.data = null;
  }

  /**
   * Constructs a deprecated attribute from a class file stream.
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  public DeprecatedAttribute(DataInput in, ConstantPool cp)
    throws IOException
  {
    this.data = new byte[in.readInt()];
    in.readFully(this.data);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the attribute's tag
   */
  /*package*/ public int getTag() {
    return ClassfileConstants2.ATT_DEPRECATED;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ protected int getSize() {
    return 2 + 4 + (data == null ? 0 : data.length);
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ protected void resolveConstants(ConstantPool cp)  throws ClassFileFormatException {
    cp.addItem(attr);
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
    if (data != null) {
      out.writeInt(data.length);
      out.write(data);
    } else {
      out.writeInt(0);
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static AsciiConstant		attr = new AsciiConstant("Deprecated");
  private byte[]			data;
}
