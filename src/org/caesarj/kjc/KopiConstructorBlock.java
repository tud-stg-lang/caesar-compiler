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
 * $Id: KopiConstructorBlock.java,v 1.2 2003-10-29 12:29:08 kloppenburg Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

public class KopiConstructorBlock extends JConstructorBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public KopiConstructorBlock(TokenReference where, 
                              JConstructorCall constructorCall, 
                              JStatement[] body)
  {
    super(where, constructorCall, body);
  }
  
  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Analyses the constructor block (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    TokenReference      ref = getTokenReference();
    CMethod             preMethod = context.getMethodContext().getCMethod().getPreconditionMethod();
    JFormalParameter[]  parameters = context.getMethodContext().getFormalParameter();

    // no check of invariant at begin of constructor!
    if (preMethod != null) {
      // precondition of constructor is static

      JExpression[]     params = new JExpression[parameters.length]; 

      for (int i = 0; i < parameters.length; i++) {
        params[i] = new JNameExpression(ref, parameters[i].getIdent());
      }
      precondition = new JExpressionStatement(ref,
                                              new KopiMethodCallExpression(ref, preMethod, params), 
                                              null);
      precondition = constrainConstructor(context.getTypeFactory(), ref,
                                          new JStatement[]{ precondition },
                                          new JExpression[]{new JLogicalComplementExpression(ref, new JNameExpression(ref, IDENT_ASSERT)) });
      precondition.analyse(context);
    }
    super.analyse(context);
    if (context.isReachable()) {
      implicitReturn = new KopiReturnStatement(ref, null, null);
      implicitReturn.analyse(context);
    }
  }

  private JStatement constrainConstructor(TypeFactory tf, TokenReference ref, JStatement[] stmts, JExpression[] exprs) {
    CReferenceType              runtimeType = tf.createReferenceType(TypeFactory.RFT_KOPIRUNTIME);
    JMethodCallExpression       testAndSet = new JMethodCallExpression(ref,
                                                                       new JTypeNameExpression(ref, 
                                                                                               runtimeType),
                                                                       "testAndSetRunAssertion", 
                                                                       JExpression.EMPTY);
    JMethodCallExpression       clear = new JMethodCallExpression(ref,
                                                                  new JTypeNameExpression(ref, 
                                                                                          runtimeType),
                                                                  "clearRunAssertion", 
                                                                  JExpression.EMPTY);

    return
      new JIfStatement(ref,
                       exprs[0],
                       new JIfStatement(ref,
                                        new JLogicalComplementExpression(ref, testAndSet), 
                                        new JTryFinallyStatement(ref ,
                                                                 new JBlock(ref, 
                                                                            // precondition
                                                                            new JStatement[] {stmts[0]}, 
                                                                            null),
                                                                 new JBlock(ref, 
                                                                            new JStatement[] {new JExpressionStatement(ref, clear, null)}, 
                                                                            null), 
                                                                 null), 
                                        null, 
                                        null), 
                       null, 
                       null);
}


  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    code.setLineNumber(getTokenReference().getLine());
    if (precondition != null) {
      precondition.genCode(context);
    }
    super.genCode(context);
    if (implicitReturn != null) {
      implicitReturn.genCode(context);
    }
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (precondition != null) precondition.accept(p);
    super.accept(p);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

  private JStatement    precondition = null;
  private JStatement    implicitReturn = null;
}
