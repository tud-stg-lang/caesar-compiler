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
 * $Id: JSwitchGroup.java,v 1.2 2004-09-06 13:31:34 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import java.util.Vector;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CSwitchGroupContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JSwitchGroup extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	labels		a group of labels
   * @param	stmts		a group of statements
   */
  public JSwitchGroup(TokenReference where,
		      JSwitchLabel[] labels,
		      JStatement[] stmts)
  {
    super(where);

    this.labels = labels;
    this.stmts = stmts;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a list of statements
   */
  public JStatement[] getStatements() {
    return stmts;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CSwitchGroupContext context) throws PositionedError {
    for (int i = 0; i < labels.length; i++) {
      labels[i].analyse(context);
    }

    context.setReachable(true);
    for (int i = 0; i < stmts.length; i++) {
      try {
	if (!context.isReachable()) {
	  throw new CLineError(stmts[i].getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
	}
	stmts[i].analyse(context);
      } catch (CLineError e) {
	context.reportTrouble(e);
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void recurse(IVisitor s) {
    for (int i = 0; i < labels.length; i++) {
        labels[i].accept(s);
    }
    for (int i = 0; i < stmts.length; i++) {
        stmts[i].accept(s);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	matches			a vector of values to match
   * @param	targets			a vector of target labels
   */
  public void collectLabels(CodeLabel deflab, Vector matches, Vector targets, TypeFactory factory) {
    pos = null;

    // check if one of the labels is "default:"
    for (int i = 0; pos == null && i < this.labels.length; i++) {
      if (this.labels[i].isDefault()) {
	pos = deflab;
      }
    }

    if (pos == null) {
      // no default: define a new label
      pos = new CodeLabel();

      for (int i = 0; i < this.labels.length; i++) {
	matches.addElement(this.labels[i].getLabel(factory));
	targets.addElement(pos);
      }
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.plantLabel(pos);
    for (int i = 0; i < stmts.length; i++) {
      stmts[i].genCode(context);
    }
  }

  public JSwitchLabel[] getLabels() {return labels;}
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JSwitchLabel[]	labels;
  private JStatement[]		stmts;
  private CodeLabel		pos;
}
