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
 * $Id: JRelationalExpression.java,v 1.2 2004-02-08 20:27:58 ostermann Exp $
 */

package org.caesarj.compiler.ast;

import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CNumericType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class implements '+ - * /' specific operations
 * Plus operand may be String, numbers
 */
public class JRelationalExpression extends JBinaryExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	oper		the operator
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JRelationalExpression(TokenReference where,
			       int oper,
			       JExpression left,
			       JExpression right)
  {
    super(where, left, right);
    this.oper = oper;
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

    check(context,
	  left.getType(factory).isNumeric() && right.getType(factory).isNumeric(),
	  KjcMessages.RELATIONAL_TYPE, left.getType(factory), right.getType(factory));

    type = context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_BOOLEAN);

    CType	promoted = CNumericType.binaryPromote(context, left.getType(factory), right.getType(factory));
    left = left.convertType(context, promoted);
    right = right.convertType(context, promoted);

    if (left.isConstant() && right.isConstant()) {
      return constantFolding(factory);
    }

    return this;
  }

  // ----------------------------------------------------------------------
  // CONSTANT FOLDING
  // ----------------------------------------------------------------------

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the left value
   * @param	right		the right value
   * @return	a literal resulting of an operation over two literals
   */
  public JExpression constantFolding(TypeFactory factory) {
    boolean	result;

    switch (left.getType(factory).getTypeID()) {
    case TID_INT:
      result = compute(left.intValue(), right.intValue());
      break;
    case TID_LONG:
      result = compute(left.longValue(), right.longValue());
      break;
    case TID_FLOAT:
      result = compute(left.floatValue(), right.floatValue());
      break;
    case TID_DOUBLE:
      result = compute(left.doubleValue(), right.doubleValue());
      break;
    default:
      throw new InconsistencyException("unexpected type " + left.getType(factory));
    }

    return new JBooleanLiteral(getTokenReference(), result);
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public boolean compute(int left, int right) {
    switch (oper) {
    case OPE_LT:
      return left < right;
    case OPE_LE:
      return left <= right;
    case OPE_GT:
      return left > right;
    case OPE_GE:
      return left >= right;
    default:
      throw new InconsistencyException();
    }
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public boolean compute(long left, long right) {
    switch (oper) {
    case OPE_LT:
      return left < right;
    case OPE_LE:
      return left <= right;
    case OPE_GT:
      return left > right;
    case OPE_GE:
      return left >= right;
    default:
      throw new InconsistencyException();
    }
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public boolean compute(float left, float right) {
    switch (oper) {
    case OPE_LT:
      return left < right;
    case OPE_LE:
      return left <= right;
    case OPE_GT:
      return left > right;
    case OPE_GE:
      return left >= right;
    default:
      throw new InconsistencyException();
    }
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public boolean compute(double left, double right) {
    switch (oper) {
    case OPE_LT:
      return left < right;
    case OPE_LE:
      return left <= right;
    case OPE_GT:
      return left > right;
    case OPE_GE:
      return left >= right;
    default:
      throw new InconsistencyException();
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitRelationalExpression(this, oper, left, right);
  }

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

    if (left.getType(factory).getTypeID() == TID_INT && left.isConstant()	&& ((JLiteral)left).isDefault()) {
      int	opcode;

      right.genCode(context, false);
      switch (oper) {
      case OPE_LT:
	opcode = cond ? opc_ifgt : opc_ifle;
	break;
      case OPE_LE:
	opcode = cond ? opc_ifge : opc_iflt;
	break;
      case OPE_GT:
	opcode = cond ? opc_iflt : opc_ifge;
	break;
      case OPE_GE:
	opcode = cond ? opc_ifle : opc_ifgt;
	break;
      default:
	throw new InconsistencyException("bad operator " + oper);
      }
      code.plantJumpInstruction(opcode, label);
    } else if (left.getType(factory).getTypeID() == TID_INT && right.isConstant() && ((JLiteral)right).isDefault()) {
      int	opcode;

      left.genCode(context, false);
      switch (oper) {
      case OPE_LT:
	opcode = cond ? opc_iflt : opc_ifge;
	break;
      case OPE_LE:
	opcode = cond ? opc_ifle : opc_ifgt;
	break;
      case OPE_GT:
	opcode = cond ? opc_ifgt : opc_ifle;
	break;
      case OPE_GE:
	opcode = cond ? opc_ifge : opc_iflt;
	break;
      default:
	throw new InconsistencyException("bad operator " + oper);
      }
      code.plantJumpInstruction(opcode, label);
    } else {
      left.genCode(context, false);
      right.genCode(context, false);

      if (left.getType(factory).getTypeID() == TID_INT) {
	int		opcode;

	switch (oper) {
	case OPE_LT:
	  opcode = cond ? opc_if_icmplt : opc_if_icmpge;
	  break;
	case OPE_LE:
	  opcode = cond ? opc_if_icmple : opc_if_icmpgt;
	  break;
	case OPE_GT:
	  opcode = cond ? opc_if_icmpgt : opc_if_icmple;
	  break;
	case OPE_GE:
	  opcode = cond ? opc_if_icmpge : opc_if_icmplt;
	  break;
	default:
	  throw new InconsistencyException("bad operator " + oper);
	}
	code.plantJumpInstruction(opcode, label);
      } else {
	int		opcode;

	if (left.getType(factory).getTypeID() == TID_LONG) {
	  opcode = opc_lcmp;
	} else if (left.getType(factory).getTypeID() == TID_FLOAT) {
	switch (oper) {
	case OPE_LT:
	case OPE_LE:
	  opcode = opc_fcmpg;
	  break;
	case OPE_GT:
	case OPE_GE:
	  opcode = opc_fcmpl;
	  break;
	default:
	  throw new InconsistencyException("bad operator " + oper);
	}
	} else if (left.getType(factory).getTypeID() == TID_DOUBLE) {
	switch (oper) {
	case OPE_LT:
	case OPE_LE:
	  opcode = opc_dcmpg;
	  break;
	case OPE_GT:
	case OPE_GE:
	  opcode = opc_dcmpl;
	  break;
	default:
	  throw new InconsistencyException("bad operator " + oper);
	}
	} else {
	  throw new InconsistencyException("bad type " + left.getType(factory));
	}
	code.plantNoArgInstruction(opcode);

	switch (oper) {
	case OPE_LT:
	  opcode = cond ? opc_iflt : opc_ifge;
	  break;
	case OPE_LE:
	  opcode = cond ? opc_ifle : opc_ifgt;
	  break;
	case OPE_GT:
	  opcode = cond ? opc_ifgt : opc_ifle;
	  break;
	case OPE_GE:
	  opcode = cond ? opc_ifge : opc_iflt;
	  break;
	default:
	  throw new InconsistencyException("bad operator " + oper);
	}
	code.plantJumpInstruction(opcode, label);
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected final int		oper;
}
