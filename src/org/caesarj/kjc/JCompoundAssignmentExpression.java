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
 * $Id: JCompoundAssignmentExpression.java,v 1.1 2003-07-05 18:29:40 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.util.InconsistencyException;
import org.caesarj.compiler.CWarning;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * JLS 15.26.2 : Compound Assignment Operator.
 */
public class JCompoundAssignmentExpression extends JAssignmentExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	oper		the assignment operator
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JCompoundAssignmentExpression(TokenReference where,
				       int oper,
				       JExpression left,
				       JExpression right)
  {
    super(where, left, right);
    this.oper = oper;
  }
  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	oper		the assignment operator
   * @param	left		the left operand
   * @param	right		the right operand
   */
  public JCompoundAssignmentExpression(TokenReference where,
				       int oper,
				       JExpression left,
				       JExpression right,
                                       CType type)
  {
    super(where, left, right);
    this.oper = oper;
    this.type = type;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
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

    // discardValue = false: check if initialized
    if (left instanceof JParenthesedExpression) {
      context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.PARENTHESED_LVALUE));
    }
    left = left.analyse(new CExpressionContext(context, 
                                               context.getEnvironment(), 
                                               true, 
                                               false));

    check(context, left.isLValue(context), KjcMessages.ASSIGNMENT_NOTLVALUE);
    if ((context.getMethodContext() instanceof CInitializerContext)
        && (left instanceof JFieldAccessExpression)
        && (context.getClassContext().getCClass() == ((JFieldAccessExpression) left).getField().getOwner())) {
      CField    field = ((JFieldAccessExpression) left).getField();

       check(context,
             field.isAnalysed()  || context.isLeftSide() || field.isSynthetic(),
             KjcMessages.USE_BEFORE_DEF, field.getIdent());

    }

    // try to assign: check lhs is not final
    try {
      left.setInitialized(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    right = right.analyse(new CExpressionContext(context, context.getEnvironment(), false, false));
    if (right instanceof JTypeNameExpression) {
      check(context, false, KjcMessages.VAR_UNKNOWN, ((JTypeNameExpression)right).getQualifiedName());
    }

    // JLS 15.26.2 Compound Assignment Operators :
    // All compound assignment operators require both operands to be of
    // primitive type, except for +=, which allows the right-hand operand to be
    // of any type if the left-hand operand is of type String.

    boolean convertRight = true;

    type = left.getType(factory); // default

    try {
      switch (oper) {
      case OPE_STAR:
	type = JMultExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_SLASH:
	type = JDivideExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_PERCENT:
	type = JModuloExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_PLUS:
        TypeFactory     tf = context.getTypeFactory();
        CReferenceType      stringType = tf.createReferenceType(TypeFactory.RFT_STRING);
        CVoidType       voidType = tf.getVoidType();

	if (left.getType(factory).equals(stringType)) {
	  if (right.getType(factory).getTypeID() == TID_VOID) {
	    throw new UnpositionedError(KjcMessages.ADD_BADTYPE,
					stringType,
					voidType);
	  }
	  type = stringType;
	  convertRight = false;
	} else {
	  type = JBinaryArithmeticExpression.computeType(context,
                                                         "+",
							 left.getType(factory),
							 right.getType(factory));
	}
	break;
      case OPE_MINUS:
	type = JMinusExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      case OPE_SL:
      case OPE_SR:
      case OPE_BSR:
	type = JShiftExpression.computeType(context, left.getType(factory), right.getType(factory));
	convertRight = false;
	break;
      case OPE_BAND:
      case OPE_BXOR:
      case OPE_BOR:
	type = JBitwiseExpression.computeType(context, left.getType(factory), right.getType(factory));
	break;
      default:
	throw new InconsistencyException("unexpected operator " + oper);
      }
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    check(context,
	  type.isCastableTo(left.getType(factory)),
	  KjcMessages.ASSIGNMENT_BADTYPE, right.getType(factory), left.getType(factory));
    if (convertRight && !right.isAssignableTo(context, left.getType(factory))) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.NARROWING_COMPOUND_ASSIGNMENT,
					 right.getType(factory), left.getType(factory)));
    }

    //    type = left.getType(factory);

    if (convertRight) {
      right = right.convertType(context, type);
    }

    if (left.requiresAccessor()) {
      JExpression accessorExpr = left.getAccessor(new JExpression[]{right}, oper);
      accessorExpr.analyse(context);
      return accessorExpr;
    } else {
      return this;
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
    p.visitCompoundAssignmentExpression(this, oper, left, right);
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

    left.genStartAndLoadStoreCode(context, false);

    if (oper == OPE_PLUS && type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
      //      left.genCode(context, false);
      right.genCode(context, false);
      if (!right.getType(factory).isReference()) {
	code.plantMethodRefInstruction(opc_invokestatic,
				       "java/lang/String",
				       "valueOf",
				       "(" + right.getType(factory).getSignature() + ")Ljava/lang/String;");
      } else if (!right.getType(factory).equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
	code.plantMethodRefInstruction(opc_invokestatic,
				       "java/lang/String",
				       "valueOf",
				       "(Ljava/lang/Object;)Ljava/lang/String;");
      }

      code.plantMethodRefInstruction(opc_invokevirtual,
				     "java/lang/String",
				     "concat",
				     "(Ljava/lang/String;)Ljava/lang/String;");
    } else {
      //      left.genCode(context, false);
        if (type.isNumeric() && left.getType(factory).isNumeric()) {
          ((CNumericType)left.getType(factory)).genCastTo(((CNumericType)type), context);
        }
      right.genCode(context, false);


      int	opcode = -1;


      switch (oper) {
      case OPE_STAR:
	opcode = JMultExpression.getOpcode(getType(factory));
	break;
      case OPE_SLASH:
	opcode = JDivideExpression.getOpcode(getType(factory));
	break;
      case OPE_PERCENT:
	opcode = JModuloExpression.getOpcode(getType(factory));
	break;
      case OPE_PLUS:
	opcode = JAddExpression.getOpcode(getType(factory));
	break;
      case OPE_MINUS:
	opcode = JMinusExpression.getOpcode(getType(factory));
	break;
      case OPE_SL:
      case OPE_SR:
      case OPE_BSR:
        if (type.isNumeric() && right.getType(factory).isNumeric() && right.getType(factory).getTypeID() == TID_LONG) {
          ((CNumericType)right.getType(factory)).genCastTo(((CNumericType)type), context);
        }
	opcode = JShiftExpression.getOpcode(oper, getType(factory));
	break;
      case OPE_BAND:
      case OPE_BXOR:
      case OPE_BOR:
	opcode = JBitwiseExpression.getOpcode(oper, getType(factory));
	break;
      }
      code.plantNoArgInstruction(opcode);

      if (type.isNumeric() && left.getType(factory).isNumeric()) {
        ((CNumericType)type).genCastTo(((CNumericType)left.getType(factory)), context);
      }
    }

    left.genEndStoreCode(context, discardValue);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected  int		oper;
}
