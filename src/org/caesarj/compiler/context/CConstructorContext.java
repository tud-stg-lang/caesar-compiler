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
 * $Id: CConstructorContext.java,v 1.3 2004-11-19 13:03:49 aracic Exp $
 */

package org.caesarj.compiler.context;

import java.util.Enumeration;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.types.CThrowableInfo;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a method context during check
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CConstructorContext extends CMethodContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * CConstructorContext
   * @param	parent		the parent context
   * @param	self		the corresponding method interface
   */
  public CConstructorContext(CClassContext parent, KjcEnvironment environment, JMethodDeclaration decl) {
    super(parent, environment, decl);

    // we create a local copy of field info
    this.fieldInfo = new CVariableInfo(null);

    isSuperConstructorCalled = true;
  }

  /**
   * Verify that all checked exceptions are defined in the throw list
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void close(TokenReference ref) throws PositionedError {
    if (getClassContext().getCClass().isAnonymous()) {
      getCMethod().setThrowables(getThrowables());
    }

    super.close(ref);
  }

  /**
   *
   */
  public void setSuperConstructorCalled(boolean b) {
    isSuperConstructorCalled = b;
  }

  /**
   *
   */
  public boolean isSuperConstructorCalled() {
    return isSuperConstructorCalled;
  }

  /** 
   * JLS 8.1.2: A statement or expression occurs in a static context if and 
   * only if the innermost method, constructor, instance initializer, static 
   * initializer, field initializer, or explicit constructor statement 
   * enclosing the statement or expression is a static method, a static 
   * initializer, the variable initializer of a static variable, or an 
   * explicit constructor invocation statement 
   *
   * @return true iff the context is static
   */
  public boolean isStaticContext() {
      return !isSuperConstructorCalled;
  }

  // ----------------------------------------------------------------------
  // FIELD STATE
  // ----------------------------------------------------------------------
 
  /**
   * Returns the field definition state.
   */
  public CVariableInfo getFieldInfo() {
    return fieldInfo;
  }

  /**
   * @param	var		the definition of a field
   * @return	all informations we have about this field
   */
  public int getFieldInfo(int index) {
    return fieldInfo.getInfo(index);
  }

  /**
   * @param	index		The field position in method array of local vars
   * @param	info		The information to add
   *
   * We make it a local copy of this information and at the end of this context
   * we will transfert it to the parent context according to controlFlow
   */
  public void setFieldInfo(int index, int info) {
    fieldInfo.setInfo(index, info);
  }

  /**
   * Marks all instance fields of this class initialized.
   */
  public void markAllFieldToInitialized() {
    for (int i = parent.getClassContext().getCClass().getFieldCount() - 1; i >= 0; i--) {
      setFieldInfo(i, CVariableInfo.INITIALIZED);
    }
  }

  /**
   * Adopts field state from instance initializer.
   */
  public void adoptInitializerInfo() {
    CClassContext	classContext = parent.getClassContext();
    CVariableInfo	initializerInfo = classContext.getInitializerInfo();
    int			fieldCount = classContext.getCClass().getFieldCount();

    if (initializerInfo != null) {
      for (int i = 0; i < fieldCount; i++) {
	int	info = initializerInfo.getInfo(i);

	if (info != 0) {
	  setFieldInfo(i, info);
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // DEBUG
  // ----------------------------------------------------------------------

  /**
   * Dumps this context to standard error stream.
   */
  public void dumpContext(String text) {
    System.err.println(text + " " + this + " parent: " + parent);
    System.err.print("    flds: ");
    if (fieldInfo == null) {
      System.err.print("---");
    } else {
      for (int i = 0; i < 8; i++) {
	System.err.print(" " + i + ":" + getFieldInfo(i));
      }
    }
    System.err.println("");
    System.err.print("    excp: ");
    for (Enumeration e = throwables.elements(); e.hasMoreElements(); ) {
      System.err.print(" " + ((CThrowableInfo)e.nextElement()).getThrowable());
    }
    System.err.println("");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean		isSuperConstructorCalled;

  private CVariableInfo		fieldInfo;
}
