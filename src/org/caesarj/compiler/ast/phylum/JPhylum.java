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
 * $Id: JPhylum.java,v 1.2 2004-03-17 11:50:07 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.visitor.KjcVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

/**
 * This class represents the root class for all elements of the parsing tree
 */
public abstract class JPhylum extends Utils implements Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct an element of the parsing tree
   * @param where the token reference of this node
   */
  public JPhylum(TokenReference where) {
	this.where = where;
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds a compiler error.
   * Redefine this method to change error handling behaviour.
   * @param	context		the context in which the error occurred
   * @param	description	the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(CContext context, MessageDescription description, Object[] params)
    throws PositionedError
  {
    throw new CLineError(getTokenReference(), description, params);
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	params		the array of parameters
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object[] params)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, params);
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	param1		the first parameter
   * @param	param2		the second parameter
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object param1, Object param2)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, new Object[] { param1, param2 });
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   * @param	param		the parameter
   */
  public final void check(CContext context, boolean cond, MessageDescription description, Object param)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, new Object[] { param });
    }
  }

  /**
   * Verifies that the condition is true; otherwise adds an error.
   * @param	context		the context in which the check occurred
   * @param	cond		the condition to verify
   * @param	description	the message ident to be displayed
   */
  public final void check(CContext context, boolean cond, MessageDescription description)
    throws PositionedError
  {
    if (!cond) {
      fail(context, description, null);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public abstract void accept(KjcVisitor p);

  /**
   * Sets the line number of this phylum in the code sequence.
   *
   * @param	code		the bytecode sequence
   */
  public void setLineNumber(CodeSequence code) {
    code.setLineNumber(getTokenReference().getLine());
  }
  
 // ----------------------------------------------------------------------
 // ACCESSORS
 // ----------------------------------------------------------------------

 /**
  * Returns the token reference of this node in the source text.
  * @return the entire token reference
  */
 public TokenReference getTokenReference() {
   return where;
 }

 // ----------------------------------------------------------------------
 // DATA MEMBERS
 // ----------------------------------------------------------------------

 private final TokenReference	where;		// position in the source text
}
