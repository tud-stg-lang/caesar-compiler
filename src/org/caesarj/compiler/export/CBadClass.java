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
 * $Id: CBadClass.java,v 1.1 2004-02-08 16:47:45 ostermann Exp $
 */

package org.caesarj.compiler.export;

import java.io.File;
import java.util.ArrayList;

import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;

/**
 * This class represents an undefined class (something that comes from a bad classfile)
 * This class is not usable to anything, so it will sooner or later produce a comprehensive error.
 */
public class CBadClass extends CClass {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class export from file
   */
  public CBadClass(String qualifiedName) {
    super(null, "undefined", 0, getIdent(qualifiedName), qualifiedName, null, false, true);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * descendsFrom
   * @param	from	an other CClass
   * @return	true if this class inherit from "from" or equals "from"
   */
  public boolean descendsFrom(CClass from) {
    return false;
  }

  /**
   * @param	ident		the name of the field
   * @return	the field
   */
  public final CField getField(String ident) {
    return null;
  }

  // ----------------------------------------------------------------------
  // LOOKUP
  // ----------------------------------------------------------------------

  /**
   * This can be used to see if a given class name is visible
   *    inside of this file.  This includes globally-qualified class names that
   *    include their package and simple names that are visible thanks to some
   *    previous import statement or visible because they are in this file.
   *    If one is found, that entry is returned, otherwise null is returned.
   * @param	caller		the class of the caller
   * @param	name		a TypeName (6.5.2)
   */
  public CClass lookupClass(CClass caller, String name) {
    return null;
  }

  /**
   * lookupMethod
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod lookupMethod(String name, CType[] params) {
    return null;
  }

  /**
   * lookupSuperMethod
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod[] lookupSuperMethod(String name, CType[] params) {
    return new CMethod[0];
  }

  /**
   * lookupField
   * search for a field
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CField lookupField(CClass caller, CClass primary, String name) {
    return null;
  }

  /**
   * @return	true if this member is accessible
   */
  public boolean isAccessible(CClass from) {
    return false;
  }

  /**
   * Returns a list of abstract methods
   */
  public CMethod[] getAbstractMethods(CTypeContext context, boolean test) {
    return new CMethod[0];
  }

  /**
   * collectInterfaceMethods
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   */
  public void collectInterfaceMethods(ArrayList v) {
  }

  /**
   * collectAbstractMethods
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   */
  public void collectAbstractMethods(ArrayList v) {
  }

  // ----------------------------------------------------------------------
  // GENERATE CLASSFILE INFO
  // ----------------------------------------------------------------------

  /**
   * Generate the code in a class file
   * @param	classes		a vector to add inner classes
   */
  public void genClassFile(File destination) {
    throw new InconsistencyException();
  }
}
