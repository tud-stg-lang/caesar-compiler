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
 * $Id: JPrefixExpression.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.classfile.IincInstruction;
import org.caesarj.classfile.PushLiteralInstruction;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CVariableInfo;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents prefix increment and decrement expressions.
 * 15.13.2 Prefix Increment Operator ++
 * 15.13.3 Prefix Decrement Operator --
 */
public class JPrefixExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	oper		the operator
   * @param	expr		the operand
   */
  public JPrefixExpression(TokenReference where, int oper, JExpression expr) {
    super(where);
    this.oper = oper;
    this.expr = expr;
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

    expr = expr.analyse(new CExpressionContext(context, 
                                               context.getEnvironment(), 
                                               true, 
                                               false)); //13.08: Value is never discarded
    check(context, expr.getType(factory).isNumeric(), KjcMessages.PREFIX_BADTYPE, expr.getType(factory));
    check(context, expr.isLValue(context), KjcMessages.PREFIX_NOTLVALUE);
    check(context, expr.isInitialized(context), KjcMessages.PREFIX_NOTINITIALIZED);
    if (expr instanceof JFieldAccessExpression) {
      CField    field = ((JFieldAccessExpression)expr).getField();

      check(context,
            context.getClassContext().getCClass() != field.getOwner()
            || field.isAnalysed() 
            || context.isLeftSide() 
            || field.isSynthetic() 
            || ((field instanceof CSourceField) && CVariableInfo.isInitialized(context.getBodyContext().getFieldInfo(((CSourceField)field).getPosition()))),
            KjcMessages.USE_BEFORE_DEF, field.getIdent());
    }
    type = expr.getType(factory);

    return this;
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
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    int			val = oper == OPE_PREINC ? 1 : -1;

    if ((expr.getType(factory).getTypeID() == TID_INT) &&
	(expr instanceof JLocalVariableExpression)) {
      code.plantInstruction(new IincInstruction(((JLocalVariableExpression)expr).getPosition(),
						val));
      expr.genCode(context, discardValue); 
    } else {
      expr.genStartAndLoadStoreCode(context, false);

      //      expr.genCode(context, false); //13.08: Value is never discarded
      switch (expr.getType(factory).getTypeID()) {
	case TID_FLOAT:
	  code.plantInstruction(new PushLiteralInstruction((float)val));
	  code.plantNoArgInstruction(opc_fadd);
	  break;
	case TID_LONG:
	  code.plantInstruction(new PushLiteralInstruction((long)val));
	  code.plantNoArgInstruction(opc_ladd);
	  break;
	case TID_DOUBLE:
	  code.plantInstruction(new PushLiteralInstruction((double)val));
	  code.plantNoArgInstruction(opc_dadd);
	  break;

	case TID_BYTE:
	case TID_CHAR:
	case TID_SHORT:
	case TID_INT:
	  code.plantInstruction(new PushLiteralInstruction(val));
	  code.plantNoArgInstruction(opc_iadd);
	  switch (expr.getType(factory).getTypeID()) {
	  case TID_BYTE:
	    code.plantNoArgInstruction(opc_i2b);
	    break;
	  case TID_CHAR:
	    code.plantNoArgInstruction(opc_i2c);
	    break;
	  case TID_SHORT:
	    code.plantNoArgInstruction(opc_i2s);
	  } 
      }

      expr.genEndStoreCode(context, discardValue);
    }
  }

  public void recurse(IVisitor s) {
      expr.accept(s);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected int			oper;
  protected JExpression		expr;
  protected CType		type;
}
