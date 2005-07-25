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
 * $Id: JUnaryPromote.java,v 1.7 2005-07-25 12:43:52 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.family.Path;
import org.caesarj.compiler.types.CNumericType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;

/**
 * This class convert arithmetics expression from types to types
 */
// andreas start
// public final class JUnaryPromote extends JExpression {
public class JUnaryPromote extends JExpression {
// andreas end

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

 /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public JUnaryPromote(CTypeContext context, JExpression expr, CType type) {
    super(expr.getTokenReference());

    this.expr = expr;
    this.type = type;

    if (!expr.isAssignableTo(context, type)) {
      needCheck = true;
    }
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

  public JExpression getExpression() {
      return expr;
  }
  
	public Path getFamily() {
	    return expr.getFamily();
	}
	
    public Path getThisAsFamily() {
        return expr.getThisAsFamily();
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

    if (type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))
	&& expr.getType(factory).isReference()
	&& expr.getType(factory) != factory.getNullType()) {
      return new JMethodCallExpression(getTokenReference(),
				       new JCheckedExpression(getTokenReference(), expr),
				       "toString",
				       JExpression.EMPTY).analyse(context);
    } else {
        
        family = expr.getFamily();
        thisAsFamily = expr.getThisAsFamily();
        
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
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    expr.genCode(context, false);

    if (type.isNumeric()) {
      ((CNumericType)expr.getType(factory)).genCastTo((CNumericType)type, context);
    } else if (needCheck) {
      code.plantClassRefInstruction(opc_checkcast, ((CReferenceType)type).getQualifiedName());
    }

    if (discardValue) {
      code.plantPopInstruction(type);
    }
  }

  public void recurse(IVisitor s) {
    expr.accept(s);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
  private CType			type;
  private boolean		needCheck;
}
