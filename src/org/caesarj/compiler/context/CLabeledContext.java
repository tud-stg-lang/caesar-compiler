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
 * $Id: CLabeledContext.java,v 1.1 2004-02-08 16:47:37 ostermann Exp $
 */

package org.caesarj.compiler.context;

import org.caesarj.compiler.CWarning;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.KjcMessages;
import org.caesarj.compiler.ast.JLabeledStatement;
import org.caesarj.compiler.ast.JStatement;
import org.caesarj.util.TokenReference;

/**
 * This class provides the contextual information for the semantic
 * analysis of a labeled statement.
 */
public class CLabeledContext extends CBodyContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs the context to analyse a labeled statement semantically.
   * @param	parent		the parent context
   * @param	stmt		the labeled statement
   */
  public CLabeledContext(CBodyContext parent, KjcEnvironment environment, JLabeledStatement stmt) {
    super(parent, environment);

    this.stmt = stmt;
  }

  /**
   * Verify everything is okay at the end of this context
   */
  public void close(TokenReference ref) {
    if (!isUsed) {
      reportTrouble(new CWarning(stmt.getTokenReference(),
				 KjcMessages.UNUSED_LABEL,
				 stmt.getLabel()));
    }

    if (breakContextSummary != null) {
      if (isReachable()) {
	merge(breakContextSummary);
      } else {
	adopt(breakContextSummary);
      }
    }

    super.close(ref);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the statement with the specified label.
   */
  public JStatement getLabeledStatement(String label) {
    if (label.equals(stmt.getLabel())) {
      isUsed = true;
      return stmt.getTargetStatement();
    } else {
      return ((CBodyContext)parent).getLabeledStatement(label);
    }
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

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final JLabeledStatement	stmt;
  private boolean			isUsed;
  private CBodyContext			breakContextSummary;
}
