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
 * $Id: JConditionalAndExpression.java,v 1.2 2004-09-06 13:31:35 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JBooleanLiteral;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class implements the conditional and operation
 */
public class JConditionalAndExpression extends JBinaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JConditionalAndExpression(TokenReference where,
				   JExpression left,
				   JExpression right)
  {
    super(where, left, right);
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
    TypeFactory         factory = context.getTypeFactory();

    left = left.analyse(context);
    right = right.analyse(context);

    CType	leftType = left.getType(factory);
    CType	rightType = right.getType(factory);
    CType       booleanType = context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_BOOLEAN);

    check(context,
	  leftType == booleanType && rightType == booleanType,
	  KjcMessages.AND_BADTYPE, leftType, rightType);

    type = booleanType;

    // JLS 15.28: Constant Expression ?
    if (left.isConstant() && right.isConstant()) {
      return new JBooleanLiteral(getTokenReference(), left.booleanValue() && right.booleanValue());
    } else {
      return this;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    genBooleanResultCode(context, discardValue);
  }

  /**
   * Optimize a bi-conditional expression
   */
  protected void genBranch(JExpression left,
			   JExpression right,
			   boolean cond,
			   GenerationContext context,
			   CodeLabel label)
  {
    CodeSequence code = context.getCodeSequence();

    if (cond) {
      CodeLabel		skip = new CodeLabel();
      left.genBranch(false, context, skip);
      right.genBranch(true, context, label);
      code.plantLabel(skip);
    } else {
      left.genBranch(false, context, label);
      right.genBranch(false, context, label);
    }
  }
}
