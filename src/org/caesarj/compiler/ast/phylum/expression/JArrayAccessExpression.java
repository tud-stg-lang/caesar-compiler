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
 * $Id: JArrayAccessExpression.java,v 1.1 2004-03-15 11:56:51 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.KjcVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CArrayType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * 15.12 Array Access Expressions
 * This class implements an access through an array
 * constant values may be folded at compile time
 */
public class JArrayAccessExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	accessor	a natural integer
   */
  public JArrayAccessExpression(TokenReference where,
				JExpression prefix,
				JExpression accessor)
  {
    super(where);

    this.prefix = prefix;
    this.accessor = accessor;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return	the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * @return	true if this expression is a variable already valued
   */
  public boolean isInitialized(CExpressionContext context) {
    // nothing to do in array access 15.12 Array Access Expressions
    return true;
  }

  /**
   * Declares this variable to be initialized.
   *
   * @exception	UnpositionedError an error if this object can't actually
   *		be assignated this may happen with final variables.
   */
  public void setInitialized(CExpressionContext context) {
    // nothing to do in array access 15.12 Array Access Expressions
  }

  /**
   *
   */
  public boolean isLValue(CExpressionContext context) {
    // nothing to do in array access 15.12 Array Access Expressions
    return true;
  }

  public String getIdent() {
    return type + "[]";
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
    TypeFactory factory = context.getTypeFactory();

    // evaluate the accessor in rhs mode, result will be used
    accessor = accessor.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context,
	  accessor.getType(factory).isAssignableTo(context, context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT)),
	  KjcMessages.ARRAY_EXPRESSION_INT, accessor.getType(factory));

    // evaluate the prefix in rhs mode, result will be used
    prefix = prefix.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context, prefix.getType(factory).isArrayType(), KjcMessages.ARRAY_PREFIX, prefix.getType(factory));

    type = ((CArrayType)prefix.getType(factory)).getElementType();

    // no constant folding is applied to array access expressions

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitArrayAccessExpression(this, prefix, accessor);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    prefix.genCode(context, false);
    accessor.genCode(context, false);
 
    code.plantNoArgInstruction(type.getArrayLoadOpcode());
    if (discardValue) {
      code.plantPopInstruction(type);
    }
  }

  /**
   * Generates JVM bytecode to store a value into the storage location
   * denoted by this expression.
   *
   * Storing is done in 3 steps :
   * - prefix code for the storage location (may be empty),
   * - code to determine the value to store,
   * - suffix code for the storage location.
   *
   * @param	code		the code list
   */
  public void genStartStoreCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    prefix.genCode(context, false);
    accessor.genCode(context, false);
  }

  /**
   * Generates JVM bytecode to for compound assignment, pre- and 
   * postfix expressions.
   *
   * @param	code		the code list
   */
  public void genStartAndLoadStoreCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    genStartStoreCode(context);

    code.plantNoArgInstruction(opc_dup2);
    code.plantNoArgInstruction(type.getArrayLoadOpcode());
    if (discardValue) {
      code.plantPopInstruction(type);
    }
  }
  /**
   * Generates JVM bytecode to store a value into the storage location
   * denoted by this expression.
   *
   * Storing is done in 3 steps :
   * - prefix code for the storage location (may be empty),
   * - code to determine the value to store,
   * - suffix code for the storage location.
   *
   * @param	code		the code list
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genEndStoreCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    if (!discardValue) {
      if (getType(factory).getSize() == 2) {
	code.plantNoArgInstruction(opc_dup2_x2);
      } else if (type.getSize() == 1) {
	code.plantNoArgInstruction(opc_dup_x2);
      }
    }
    code.plantNoArgInstruction(type.getArrayStoreOpcode());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		prefix;
  private JExpression		accessor;
  private CType			type;
}
