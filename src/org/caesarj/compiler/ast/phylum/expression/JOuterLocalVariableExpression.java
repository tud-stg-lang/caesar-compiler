/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
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
 * $Id: JOuterLocalVariableExpression.java,v 1.2 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JLiteral;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Root class for all expressions
 */
public class JOuterLocalVariableExpression extends JLocalVariableExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JOuterLocalVariableExpression(TokenReference where,
				       JLocalVariable var,
				       CClass outer) {
    super(where, var);

    this.outer = outer;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    // First we have to find the right context
    CContext	body = context.getBodyContext();
    while (body.getClassContext().getCClass() != outer) {
      body = body.getClassContext().getParentContext();
    }

    CContext		parent = body.getClassContext().getParentContext();
    CExpressionContext  ctxt = parent instanceof CExpressionContext ? (CExpressionContext)parent : new CExpressionContext((CBodyContext)parent, context.getEnvironment());
    JExpression		expr = super.analyse(ctxt);

    if (! (expr instanceof JLiteral)) {
      check(context,
	    expr == this && getVariable().isFinal(),
	    KjcMessages.BAD_LOCAL_NOT_FINAL,
	    getVariable().getIdent());

      CSourceClass      local = (CSourceClass) context.getClassContext().getCClass();

      expr = local.getOuterLocalAccess(getTokenReference(),
                                       getVariable(),
                                       context.getMethodContext() instanceof CConstructorContext ?
                                       ((CConstructorContext)context.getMethodContext()).getCMethod() :
                                       null,
                                       (CSourceClass) outer);
      expr = expr.analyse(context);
    }

    return expr;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public CClass		outer;
}
