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
 * $Id: JAddExpression.java,v 1.1 2004-03-15 11:56:52 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JStringLiteral;
import org.caesarj.compiler.ast.visitor.KjcVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CNumericType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class implements '+ - * /' specific operations
 * Plus operand may be String, numbers
 */
public class JAddExpression extends JBinaryArithmeticExpression {

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
  public JAddExpression(TokenReference where,
			JExpression left,
			JExpression right)
  {
    super(where, left, right);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of this object.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JAddExpression[");
    buffer.append(left.toString());
    buffer.append(", ");
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
    TypeFactory         factory = context.getTypeFactory();

    left = left.analyse(context);
    right = right.analyse(context);
    check(context, left.getType(factory).getTypeID() != TID_VOID && right.getType(factory).getTypeID() != TID_VOID,
	  KjcMessages.ADD_BADTYPE, left.getType(factory), right.getType(factory));

    check(context, !(left instanceof JTypeNameExpression) && !(right instanceof JTypeNameExpression),
	  KjcMessages.ADD_BADTYPE, left.getType(factory), right.getType(factory));

    try {
      type = computeType(context, left.getType(factory), right.getType(factory));
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    CReferenceType  stringType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING);

    // programming trick: no conversion for strings here: will be done in code generation
    if (!type.equals(stringType)) {
      left = left.convertType(context, type);
      right = right.convertType(context, type);
   }

    if (left.isConstant() && right.isConstant()) {
      if (type.equals(stringType)) {
	// in this case we have to convert the operands
	left = left.convertType(context, type);
	right = right.convertType(context, type);
	return new JStringLiteral(getTokenReference(), left.stringValue() + right.stringValue());
      } else {
	return constantFolding(factory);
      }
    } else {
      return this;
    }
  }

  /**
   * compute the type of this expression according to operands
   * @param	leftType		the type of left operand
   * @param	rightType		the type of right operand
   * @return	the type computed for this binary operation
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public static CType computeType(CExpressionContext context, 
                                  CType	leftType, 
                                  CType rightType) throws UnpositionedError {
    CType stringType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING);

    if (leftType.equals(stringType)) {
      if (rightType.getTypeID() == TID_VOID) {
	throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
      }
      return stringType;
    } else if (rightType.equals(stringType)) {
      if (leftType.getTypeID() == TID_VOID) {
	throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
      }
      return stringType;
    } else {
      if (leftType.isNumeric() && rightType.isNumeric()) {
	return CNumericType.binaryPromote(context, leftType, rightType);
      }

      throw new UnpositionedError(KjcMessages.ADD_BADTYPE, leftType, rightType);
    }
  }

  // ----------------------------------------------------------------------
  // CONSTANT FOLDING
  // ----------------------------------------------------------------------

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public int compute(int left, int right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public long compute(long left, long right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public float compute(float left, float right) {
    return left + right;
  }

  /**
   * Computes the result of the operation at compile-time (JLS 15.28).
   * @param	left		the first operand
   * @param	right		the seconds operand
   * @return	the result of the operation
   */
  public double compute(double left, double right) {
    return left + right;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitBinaryExpression(this, "+", left, right);
  }

  /**
   * @param	type		the type of result
   * @return	the type of opcode for this operation
   */
  public static int getOpcode(CType type) {
    switch (type.getTypeID()) {
    case TID_FLOAT:
      return opc_fadd;
    case TID_LONG:
      return opc_ladd;
    case TID_DOUBLE:
      return opc_dadd;
    default:
      return opc_iadd;
    }
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    if (type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
      code.plantClassRefInstruction(opc_new, "java/lang/StringBuffer");
      code.plantNoArgInstruction(opc_dup);
      code.plantMethodRefInstruction(opc_invokespecial,
				     "java/lang/StringBuffer",
				     JAV_CONSTRUCTOR,
				     "()V");
      appendToStringBuffer(context, left);
      appendToStringBuffer(context, right);
      code.plantMethodRefInstruction(opc_invokevirtual,
				     "java/lang/StringBuffer",
				     "toString",
				     "()Ljava/lang/String;");
    } else {
      left.genCode(context, false);
      right.genCode(context, false);

      code.plantNoArgInstruction(getOpcode(getType(factory)));
    }

    if (discardValue) {
      code.plantPopInstruction(getType(factory));
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  private void appendToStringBuffer(GenerationContext context, JExpression expr) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    if ((expr instanceof JAddExpression) &&
        (expr.getType(factory).equals(factory.createReferenceType(TypeFactory.RFT_STRING)))) {
      ((JAddExpression)expr).appendToStringBuffer(context, ((JAddExpression)expr).left);
      ((JAddExpression)expr).appendToStringBuffer(context, ((JAddExpression)expr).right);
    } else {
      CType	type = expr.getType(factory);
      int	typeID = type.getTypeID();

      expr.genCode(context, false);
      if (!type.isReference()
          || type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
	// StringBuffer.append() is defined for most primitive types and
	// for type String. StringBuffer.append() is not defined for byte
	// and short ; using StringBuffer.append(int) instead is safe
	// since the value pushed on the stack is of type int and
	// should be interpreted as int for these types.
	if (typeID == TID_BYTE || typeID == TID_SHORT) {
	  type = factory.getPrimitiveType(TypeFactory.PRM_INT);
	}

	code.plantMethodRefInstruction(opc_invokevirtual,
				       "java/lang/StringBuffer",
				       "append",
				       "(" + type.getSignature() + ")Ljava/lang/StringBuffer;");
      } else {
	code.plantMethodRefInstruction(opc_invokevirtual,
				       "java/lang/StringBuffer",
				       "append",
				       "(Ljava/lang/Object;)Ljava/lang/StringBuffer;");
      }
    }
  }
}
