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
 * $Id: JSwitchLabel.java,v 1.2 2004-09-06 13:31:34 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CSwitchGroupContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.MessageDescription;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JSwitchLabel extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	expr		the expression (null if default label)
   */
  public JSwitchLabel(TokenReference where, JExpression expr) {
    super(where);

    this.expr = expr;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return	true if this label is a "default:" label
   */
  public boolean isDefault() {
    return expr == null;
  }

  /**
   * @return	the value of this label
   */
  public Integer getLabel(TypeFactory factory) {
    return new Integer(getLabelValue(factory));
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CSwitchGroupContext context)
    throws PositionedError
  {
    TypeFactory         factory = context.getTypeFactory();

    if (expr != null) {
      expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
      check(context, expr.isConstant(), KjcMessages.SWITCH_LABEL_EXPR_NOTCONST);
      check(context,
	    expr.isAssignableTo(context, context.getType()),
	    KjcMessages.SWITCH_LABEL_OVERFLOW, expr.getType(factory));

      try {
	context.addLabel(new Integer(getLabelValue(factory)));
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    } else {
      try {
	context.addDefault();
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    }
  }

  /**
   * Adds a compiler error.
   * @param	context		the context in which the error occurred
   * @param	key		the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(CContext context, MessageDescription key, Object[] params)
    throws PositionedError
  {
    throw new CLineError(getTokenReference(), key, params);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  public void recurse(IVisitor s) {
    expr.accept(s);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  private int getLabelValue(TypeFactory factory) {
    CType	type = expr.getType(factory);

    if (type == factory.getPrimitiveType(TypeFactory.PRM_BYTE)) {
      return expr.byteValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_CHAR)) {
      return expr.charValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_SHORT)) {
      return expr.shortValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
      return expr.intValue();
    } else {
      throw new InconsistencyException("unexpected type " + type);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
}
