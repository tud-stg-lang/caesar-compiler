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
 * $Id: CTypeContext.java,v 1.1 2004-02-08 16:47:37 ostermann Exp $
 */

package org.caesarj.compiler.context;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

/**
 * Context type checking. Also used while loading binary classes.
 */
public interface CTypeContext {

  /**
   * @return the TypeFactory
   */
  TypeFactory getTypeFactory();

  /**
   * @return the Object used to read class files.
   */
  ClassReader getClassReader();


  /**
   * @return the user of the CClass, which wants to access it.
   */
  CClassContext getClassContext();

  /**
   * @param caller the user of the CClass, which wants to access it.
   * @param name the name of the class
   */
  CClass lookupClass(CClass caller, String name) throws UnpositionedError ;

  /**
   * Searches the class, interface and Method to locate declarations of TV's that are
   * accessible.
   * 
   * @param	ident		the simple name of the field
   * @return	the TV definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  CTypeVariable lookupTypeVariable(String ident) throws UnpositionedError;

  /**
   * Reports a semantic error detected during analysis.
   *
   * @param	trouble		the error to report
   */
  void reportTrouble(PositionedError trouble);
}
