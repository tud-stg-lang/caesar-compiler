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
 * $Id: JConditionalExpression.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CSimpleBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CNumericType;
import org.caesarj.compiler.types.CPrimitiveType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 15.25 Conditional Operator ? :
 */
public class JConditionalExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	cond		the condition operand
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JConditionalExpression(TokenReference where,
				JExpression cond,
				JExpression left,
				JExpression right)
  {
    super(where);
    this.cond = cond;
    this.left = left;
    this.right = right;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * Returns a string representation of this literal.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JConditionalExpression[");
    buffer.append(cond.toString());
    buffer.append(", ");
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
    CType               primBoolean = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);

    cond = cond.analyse(context);

    // left side
    CBodyContext        leftBodyContext = new CSimpleBodyContext(context.getBodyContext(), 
                                                                 context.getEnvironment(), 
                                                                 context.getBodyContext());
    CExpressionContext  leftExprContext = new CExpressionContext(leftBodyContext, 
                                                                 context.getEnvironment(), 
                                                                 context.isLeftSide(), 
                                                                 context.discardValue());
    left = left.analyse(leftExprContext);

    // right side
//     CBodyContext        rightBodyContext = new CSimpleBodyContext(context.getBodyContext(), 
//                                                                   context.getEnvironment(), 
//                                                                   context.getBodyContext());
//     CExpressionContext  rightExprContext = new CExpressionContext(rightBodyContext, 
//                                                                   context.getEnvironment(), 
//                                                                   context.isLeftSide(), 
//                                                                   context.discardValue());
    right = right.analyse(context);
    context.getBodyContext().merge(leftBodyContext);
 
    check(context, cond.getType(factory) == primBoolean, KjcMessages.TRINARY_BADCOND);

    CType               leftType        = left.getType(factory);
    CType               rightType       = right.getType(factory);
    // !!!  Thomas : A VERIFIER -> on remplace tout par des CPrimitiveType ou on fait un cast ?
    //     CShortType          primShort       = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
    //     CByteType           primByte        = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
    //     CIntType            primInt         = factory.getPrimitiveType(TypeFactory.PRM_INT);
    //     CCharType           primChar        = factory.getPrimitiveType(TypeFactory.PRM_CHAR);
    CPrimitiveType      primShort       = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
    CPrimitiveType      primByte        = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
    CPrimitiveType      primInt         = factory.getPrimitiveType(TypeFactory.PRM_INT);
    CPrimitiveType      primChar        = factory.getPrimitiveType(TypeFactory.PRM_CHAR);

    // JLS 15.25 :
    // The type of a conditional expression is determined as follows:
    if (leftType.equals(rightType)) {
      // - If the second and third operands have the same type (which may
      //   be the null type), then that is the type of the conditional
      //   expression.
      type = leftType;
    } else if (leftType.isNumeric() && rightType.isNumeric()) {
      // - Otherwise, if the second and third operands have numeric type,
      //   then there are several cases:
      //   * If one of the operands is of type byte and the other is of
      //     type short, then the type of the conditional expression is short.
      //   * If one of the operands is of type T where T is byte, short,
      //     or char, and the other operand is a constant expression of type
      //     int whose value is representable in type T, then the type of
      //     the conditional expression is T.
      //   * Otherwise, binary numeric promotion is applied to the operand
      //     types, and the type of the conditional expression is the promoted
      //     type of the second and third operands. Note that binary numeric
      //     promotion performs value set conversion.
      if ((leftType == primByte && rightType == primShort)
	  || (rightType == primByte && leftType == primShort)) {
	type = primShort;
      } else if ((leftType == primByte
		  || leftType == primShort
		  || leftType == primChar)
		 && rightType == primInt
		 && right.isConstant()
		 && right.isAssignableTo(context, leftType)) {
	type = leftType;
      } else if ((rightType == primByte
		  || rightType == primShort
		  || rightType == primChar)
		 && leftType == primInt
		 && left.isConstant()
		 && left.isAssignableTo(context, rightType)) {
	type = rightType;
      } else {
	type = CNumericType.binaryPromote(context, leftType, rightType);
	check(context,
	      type != null,
	      KjcMessages.TRINARY_INCOMP, leftType, rightType);
      }
      left = left.convertType(context, type);
      right = right.convertType(context, type);
    } else if (leftType.isReference() && rightType.isReference()) {
      // - If one of the second and third operands is of the null type and the
      //   type of the other is a reference type, then the type of the
      //   conditional expression is that reference type.
      // - If the second and third operands are of different reference types,
      //   then it must be possible to convert one of the types to the other type
      //   (call this latter type T) by assignment conversion ; the type of the
      //   conditional expression is T.
      //   It is a compile-time error if neither type is assignment
      //   compatible with the other type.
      CType     nullType = factory.getNullType();

      if (leftType == nullType) {
	type = rightType;
      } else if (rightType == nullType) {
	type = leftType;
      } else if (leftType.isAssignableTo(context,rightType)) {
	type = rightType;
      } else if (rightType.isAssignableTo(context,leftType)) {
	type = leftType;
      } else {
	check(context, false, KjcMessages.TRINARY_INCOMP, leftType, rightType);
      }
    } else {
      check(context, false, KjcMessages.TRINARY_INCOMP, leftType, rightType);
    }

    // JLS 15.28: Constant Expression ?
    if (cond.isConstant() && left.isConstant() && right.isConstant()) {
      return cond.booleanValue() ? left : right;
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
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		rightLabel = new CodeLabel();
    CodeLabel		nextLabel = new CodeLabel();

    cond.genBranch(false, context, rightLabel);		//		COND IFEQ right
    left.genCode(context, discardValue);			//		LEFT CODE
    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    code.plantLabel(rightLabel);			//	right:
    right.genCode(context, discardValue);			//		RIGHT CODE
    code.plantLabel(nextLabel);				//	next:	...
  }
  
  public void recurse(IVisitor s) {
    cond.accept(s);
    left.accept(s);
    right.accept(s);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CType		type;
  private JExpression   cond;
  private JExpression   left;
  private JExpression   right;
}
