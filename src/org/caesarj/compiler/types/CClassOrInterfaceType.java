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
 * $Id: CClassOrInterfaceType.java,v 1.4 2004-10-17 20:59:36 aracic Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents class type in the type structure
 */
public class CClassOrInterfaceType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------


  /**
   * Construct a class type
   * @param	clazz		the class that will represent this type
   */
  public CClassOrInterfaceType(CClass clazz) {
    super(clazz);
    this.checked = false;
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
    checked = true;
    return this;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean c) {
    checked = c;
  }
 // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------



  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CType dest) {
    // test for array first because array types are classes

    if (getCClass().isInterface()) {
      if (! dest.isClassType()) {
	return false;
      } else if (dest.getCClass().isInterface()) {
	// if T is an interface type and if T and S contain methods
	// with the same signature but different return types,
	// then a compile-time error occurs.
	//!!! graf 000512: FIXME: implement this test
	return true;
      } else if (! dest.getCClass().isFinal()) {
	return true;
      } else {
	return dest.getCClass().descendsFrom(getCClass());
      }
    } else {
      // this is a class type
      if (dest.isArrayType()) {
	return equals(CStdType.Object);
      } else if (! dest.isClassType()) {
	return false;
      } else if (dest.getCClass().isInterface()) {
	if (! getCClass().isFinal()) {
	  return true;
	} else {
	  return getCClass().descendsFrom(dest.getCClass());
	}
      } else {
	return getCClass().descendsFrom(dest.getCClass())
	  || dest.getCClass().descendsFrom(getCClass());
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private boolean checked;
}
