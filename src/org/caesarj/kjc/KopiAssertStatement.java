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
 * $Id: KopiAssertStatement.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * JSR 41: Extensions with Assert Statement: <code> assert bool-expr; </code> 
 * <code> assert bool-expr : expr; </code>.  This statement throw a runtime 
 * exception with an optional message if the boolean expr evaluates to false.
 */
public class KopiAssertStatement extends JStatement{
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param     cond            the condition
   * @param	expr		the expression to throw.
   * @param     standard        true if it is a java assert (JSR 14)
   * @param	comment		the statement comment.
   */
  public KopiAssertStatement(TokenReference where, 
                             JExpression cond, 
                             JExpression expr,
                             boolean standard,
                             JavaStyleComment[] comments) {
        super(where, comments);
        this.expr = expr;
        this.cond = cond;
        this.standard = standard;
  }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CBodyContext	assertContext = new CSimpleBodyContext(context, 
                                                               context.getEnvironment(), 
                                                               context);
    TypeFactory         factory = context.getTypeFactory();

    cond = cond.analyse(new CExpressionContext(assertContext, context.getEnvironment()));
    check(context, 
          cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN), 
          KjcMessages.ASSERT_COND_BAD_TYPE, 
          cond.getType(factory)); 

    TokenReference      tokenReference = getTokenReference();
    CMethod             method = context.getMethodContext().getCMethod();
    CReferenceType      throwIt;
    JExpression         classArg = null;
    boolean             nonReachable = cond.isConstant() && !cond.booleanValue();

    if (method.isPrecondition()) {
      throwIt = context.getTypeFactory().createType(KOPI_ERROR_PRECOND, false);
      classArg = new JClassExpression(tokenReference, new CClassOrInterfaceType(context.getClassContext().getCClass()), 0);
    } else if (method.isPostcondition()){
      throwIt = context.getTypeFactory().createType(KOPI_ERROR_POSTCOND, false);
    } else if (method.isInvariant()){
      throwIt = context.getTypeFactory().createType(KOPI_ERROR_INV, false);
    } else {
      // evaluate only if $assertDisabled is false
      cond = new JConditionalOrExpression(getTokenReference(), 
                                          new JNameExpression(getTokenReference(), IDENT_ASSERT), 
                                          new JParenthesedExpression(getTokenReference(), 
                                                                     new JCheckedExpression(getTokenReference(),cond)));
      if (standard) {
        // assert ...
        throwIt = context.getTypeFactory().createType(JAV_ERROR_ASSERT, false);
      } else {
        // @assert ...
        throwIt = context.getTypeFactory().createType(KOPI_ERROR_ASSERT, false);
      }
    }

    if (expr == null) {
      // default: return the name of the file and the line number
      expr = new JStringLiteral(tokenReference, tokenReference.getName() + ": " + tokenReference.getLine());
      impl = parseAssert(tokenReference, 
                         cond, 
                         throwIt, 
                         classArg != null ? new JExpression [] { classArg, expr } :  new JExpression [] { expr });
    } else {
      //      expr = expr.analyse(new CExpressionContext(assertContext, context.getEnvironment()));
      impl = parseAssert(tokenReference, 
                         cond, 
                         throwIt, 
                         classArg != null ? new JExpression [] { classArg, expr } :  new JExpression[] {expr});
    }
    impl.analyse(assertContext);
    context.merge(assertContext);

    if (nonReachable) {
      //      context.setReachable(false);
    }
  } 

  JStatement parseAssert(TokenReference ref, JExpression condition, CReferenceType throwThis, JExpression[] arg) {
    return new JIfStatement(ref,
                            new JLogicalComplementExpression(ref, condition), 
                            new JThrowStatement(ref,
                                                new JUnqualifiedInstanceCreation(ref, 
                                                                                 throwThis, 
                                                                                 arg), 
                                                null), 
                            null, 
                            null);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (impl == null) {
      p.visitAssertStatement(this, cond, expr);
    } else {
      impl.accept(p);
    }
  }
  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    impl.genCode(context);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  private JExpression   cond;
  private JExpression   expr;
  private JStatement    impl;
  private boolean       standard;
}
