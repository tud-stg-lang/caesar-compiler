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
 * $Id: CSwitchBodyContext.java,v 1.3 2004-03-15 11:56:53 aracic Exp $
 */

package org.caesarj.compiler.context;

import java.util.Hashtable;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.statement.JSwitchStatement;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a local context during checkBody
 * It follows the control flow and maintain informations about
 * variable (initialised, used, allocated), exceptions (thrown, catched)
 * It also verify that context is still reachable
 *
 * There is a set of utilities method to access fields, methods and class
 * with the name by clamping the parsing tree
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CSwitchBodyContext extends CBlockContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context, it must be different
   *				than null except if called by the top level
   * @param	switchType	the size of switch (byte, short, char...)
   */
  public CSwitchBodyContext(CBodyContext parent, KjcEnvironment environment,
			    JSwitchStatement stmt)
  {
    super(parent, environment);
    this.stmt = stmt;
    clearFlowState();
    setInLoop(parent.isInLoop());
  }

  /**
   * close
   */
  public void close() {
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (SWITCHES)
  // ----------------------------------------------------------------------

  /**
   * add a default label to this switch
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void addDefault() throws UnpositionedError {
    if (defaultExist) {
      throw new UnpositionedError(KjcMessages.SWITCH_DEFAULT_DOUBLE);
    }
    defaultExist = true;
  }

  /**
   * add a label to this switch and check it is a new one
   * @param	value		the literal value of this label
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void addLabel(Integer value) throws UnpositionedError {
    if (labels.put(value, "*") != null) {
      throw new UnpositionedError(KjcMessages.SWITCH_LABEL_EXIST, value);
    }
  }

  /**
   * Returns the type of the switch expression.
   */
  public CType getType() {
    return stmt.getType(getTypeFactory());
  }

  /**
   * @return	true is a default label has already happen
   */
  public boolean defaultExists() {
    return defaultExist;
  }

  /**
   * Returns the innermost statement which can be target of a break
   * statement without label.
   */
  public JStatement getNearestBreakableStatement() {
    return stmt;
  }

  /**
   *
   */
  protected void addBreak(JStatement target,
			  CBodyContext context)
  {
    if (stmt == target) {
      if (breakContextSummary == null) {
	breakContextSummary = context.cloneContext();
      } else {
	breakContextSummary.merge(context);
      }
      breakContextSummary.setReachable(true);
    } else {
      ((CBodyContext)getParentContext()).addBreak(target, context);
    }
  }
  /**
   * Checks whether this statement is target of a break statement.
   *
   * @return	true iff this statement is target of a break statement.
   */
  public boolean isBreakTarget() {
    return breakContextSummary != null;
  }

  /**
   * Returns the context state after break statements.
   */
  public CBodyContext getBreakContextSummary() {
    return breakContextSummary;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final JSwitchStatement	stmt;
  private CBodyContext			breakContextSummary;
  private final Hashtable		labels = new Hashtable();
  private boolean			defaultExist;
}
