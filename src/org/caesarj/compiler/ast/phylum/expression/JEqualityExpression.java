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
 * $Id: JEqualityExpression.java,v 1.2 2004-09-06 13:31:35 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JBooleanLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JLiteral;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CNumericType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.CWarning;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 15.21: Equality Operators ('==' and '!=')
 */
public class JEqualityExpression extends JBinaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	equal		is the operator '==' ?
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JEqualityExpression(TokenReference where,
			     boolean equal,
			     JExpression left,
			     JExpression right)
  {
    super(where, left, right);
    this.equal = equal;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this literal.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JEqualityExpression[");
    buffer.append(left.toString());
    buffer.append(equal ? " == " : " != ");
    buffer.append(right.toString());
    buffer.append("]");
    return buffer.toString();
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
    left = left.analyse(context);
    right = right.analyse(context);

    TypeFactory factory = context.getTypeFactory();
    CType	leftType = left.getType(factory);
    CType	rightType = right.getType(factory);
    CType       booleanType = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);
    CType       stringType = factory.createReferenceType(TypeFactory.RFT_STRING);

    if (leftType.isNumeric()) {
      // JLS 15.21.1: Numerical Equality Operators
      check(context, rightType.isNumeric(), KjcMessages.EQUALITY_TYPE, leftType, rightType);

      CType	promoted = CNumericType.binaryPromote(context, leftType, rightType);

      left = left.convertType(context, promoted);
      right = right.convertType(context, promoted);
    } else if (leftType == booleanType) {
      // JLS 15.21.2: Boolean Equality Operators
      check(context, rightType == booleanType, KjcMessages.EQUALITY_TYPE, leftType, rightType);
      if (left instanceof JBooleanLiteral || right instanceof JBooleanLiteral) {
	context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.COMPARING_BOOLEAN_CONSTANT));
      }
    } else {
      // JLS 15.21.3: Reference Equality Operators
      check(context,
	    leftType.isReference() && rightType.isReference(),
	    KjcMessages.EQUALITY_TYPE, leftType, rightType);
      check(context,
	    rightType.isCastableTo(leftType) || leftType.isCastableTo(rightType),
	    KjcMessages.EQUALITY_TYPE, leftType, rightType);
      if (left.getType(factory).equals(stringType) && right.getType(factory).equals(stringType) &&
	  (left.isConstant() || right.isConstant())) {
	context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.STRING_COMPARISON));
      }
    }

    type = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);

    if (left.isConstant() && right.isConstant()) {
      return constantFolding(factory);
    } else {
      return this;
    }
  }

  /**
   * @return	a literal resulting of an operation over two literals
   */
  public JExpression constantFolding(TypeFactory factory) {
    boolean	result;

    switch (left.getType(factory).getTypeID()) {
    case TID_INT:
      result = left.intValue() == right.intValue();
      break;
    case TID_LONG:
      result = left.longValue() == right.longValue();
      break;
    case TID_FLOAT:
      result = left.floatValue() == right.floatValue();
      break;
    case TID_DOUBLE:
      result = left.doubleValue() == right.doubleValue();
      break;
    case TID_BOOLEAN:
      result = left.booleanValue() == right.booleanValue();
      break;
    case TID_CLASS:
      if (left.getType(factory) != factory.createReferenceType(TypeFactory.RFT_STRING)) {
	throw new InconsistencyException("unexpected type " + left.getType(factory));
      }
      result = left.stringValue().equals(right.stringValue());
      break;
    default:
      throw new InconsistencyException("unexpected type " + left.getType(factory));
    }

    return new JBooleanLiteral(getTokenReference(), equal ? result : !result);
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
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    if (left.getType(factory).getTypeID() == TID_NULL) {
      // use specific instruction to compare to null
      right.genCode(context, false);
      code.plantJumpInstruction(cond == equal ? opc_ifnull : opc_ifnonnull, label);
    } else if (right.getType(factory).getTypeID() == TID_NULL) {
      // use specific instruction to compare to null
      left.genCode(context, false);
      code.plantJumpInstruction(cond == equal ? opc_ifnull : opc_ifnonnull, label);
    } else if (left.isConstant()
	       && (left.getType(factory).getTypeID() == TID_INT || left.getType(factory).getTypeID() == TID_BOOLEAN)
	       && ((JLiteral)left).isDefault()) {
      // use specific instruction to compare to 0
      right.genCode(context, false);
      code.plantJumpInstruction(cond == equal ? opc_ifeq : opc_ifne, label);
    } else if (right.isConstant()
	       && (right.getType(factory).getTypeID() == TID_INT || right.getType(factory).getTypeID() == TID_BOOLEAN)
	       && ((JLiteral)right).isDefault()) {
      // use specific instruction to compare to 0
      left.genCode(context, false);
      code.plantJumpInstruction(cond == equal ? opc_ifeq : opc_ifne, label);
    } else {
      left.genCode(context, false);
      right.genCode(context, false);

      switch (left.getType(factory).getTypeID()) {
      case TID_ARRAY:
      case TID_NULL:
      case TID_CLASS:
	code.plantJumpInstruction(cond  == equal ? opc_if_acmpeq : opc_if_acmpne, label);
	break;
      case TID_FLOAT:
	code.plantNoArgInstruction(opc_fcmpl);
	code.plantJumpInstruction(cond == equal ? opc_ifeq : opc_ifne, label);
	break;
      case TID_LONG:
	code.plantNoArgInstruction(opc_lcmp);
	code.plantJumpInstruction(cond == equal ? opc_ifeq : opc_ifne, label);
	break;
      case TID_DOUBLE:
	code.plantNoArgInstruction(opc_dcmpl);
	code.plantJumpInstruction(cond == equal ? opc_ifeq : opc_ifne, label);
	break;
      default:
	code.plantJumpInstruction(cond == equal ? opc_if_icmpeq : opc_if_icmpne, label);
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected final boolean		equal;
}
