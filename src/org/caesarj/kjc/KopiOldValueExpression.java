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
 * $Id: KopiOldValueExpression.java,v 1.2 2003-10-29 12:29:08 kloppenburg Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.CWarning;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.util.InconsistencyException;

/**
 * Refer to a value at the begin of the method call in the postcondition
 */
public class KopiOldValueExpression extends JExpression { 

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   *
   * @param	where	the line of this node in the source code
   * @param	expr	the expression (evaluated at the method entry)
   */
  public KopiOldValueExpression(TokenReference where, JExpression expr) {
    super(where);
    this.expr = expr;
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
    CMethodContext       methodContext = context.getMethodContext();
    CBlockContext        block = new CBlockContext(methodContext, context.getEnvironment(), 0);
    CExpressionContext   self = new CExpressionContext(block,  context.getEnvironment());
    TypeFactory          factory = context.getTypeFactory();

    check(context, methodContext.getCMethod().isPostcondition(), KjcMessages.WRONG_OLD_VALUE);
    check(context, !context.getClassContext().getCClass().isAssertionClass(), KjcMessages.OLD_VALUE_IN_INTER);
    check(context, !context.isInOld(), KjcMessages.NESTED_OLD);

    self.setInOld(true);
    expr = expr.analyse(self);
 
    if (expr.isConstant()) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.CONSTANT_OLD_VALUE));
      return expr; // nothing more to do
    } else {
      // store value in field of 'old value store' class
      String                    fieldName = IDENT_FIELD+methodContext.getNextStoreFieldIndex();
      JFieldDeclaration         fieldDecl = 
        new JFieldDeclaration(getTokenReference(),
                              new JVariableDefinition(getTokenReference(),
                                                      Constants.ACC_FINAL,
                                                      expr.getType(factory),
                                                      fieldName,
                                                      expr),
                              //                                                      new JCheckedExpression(getTokenReference(),
                              //                                              expr)),
                              null,
                              null);
      // in KopiPostconditionStatement an inner class with these fields is created
      methodContext.addStoreField(fieldDecl);

      KopiStoreFieldAccessExpression    access = 
        new KopiStoreFieldAccessExpression(getTokenReference(), 
                                   new JNameExpression(getTokenReference(), IDENT_STORAGE), 
                                   fieldDecl,
                                   expr.getType(factory));

      access.analyse(context); 
      return access;
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression.
   *
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return expr.getType(factory);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    //  !!!   to do
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    throw new InconsistencyException("gencode of old value");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
  
} 
