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
 * $Id: CBinaryType.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.export.CClass;


/**
 * This class represents class type load from a binary class file 
 * The class of this type is only loaded if necessary. This type need no check
 */
public class CBinaryType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class or interface type from binary class
   */
  public CBinaryType(String qualifiedName, ClassReader classReader, TypeFactory typeFactory) {
    super();
    this.qualifiedName = qualifiedName;
    this.classReader = classReader;
    this.typeFactory = typeFactory;
    this.arguments = new CReferenceType[][]{CReferenceType.EMPTY};
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
    if (!isChecked()) {
      setClass(classReader.loadClass(typeFactory, qualifiedName));
      qualifiedName = null;
      classReader = null;
      typeFactory = null;
    }

    return super.getCClass();
  }

  /**
   *
   */
  public String getQualifiedName() {
    return qualifiedName == null ? super.getQualifiedName() : qualifiedName;
  }

  private String qualifiedName; 
  private ClassReader classReader; 
  private TypeFactory typeFactory;
}
