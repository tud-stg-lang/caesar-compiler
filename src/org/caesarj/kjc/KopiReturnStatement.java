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
 * $Id: KopiReturnStatement.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

public class KopiReturnStatement extends JStatement {
  public KopiReturnStatement(TokenReference where, JExpression expr, JavaStyleComment[] comments) {
    super(where, comments);
    this.expr = expr;
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
    CMethod             method = context.getMethodContext().getCMethod();
    CType               returnType = method.getReturnType();
    JExpression[]       exprS = new JExpression[] {new JLogicalComplementExpression(getTokenReference(), new JNameExpression(getTokenReference(), IDENT_ASSERT))};
    CClass              local = context.getClassContext().getCClass();
    LanguageExtensions  langExt = context.getEnvironment().getLanguageExtFactory();
    JFormalParameter[]  parameters = context.getMethodContext().getFormalParameter();

    JStatement[]        stmtS = new JStatement[] {
          new JEmptyStatement(getTokenReference(), null),
          new JEmptyStatement(getTokenReference(), null)
    };

    // Is there a postcondition to check
    final CMethod       post = method.getPostconditionMethod();

    if (post == null) {
      // no postcondition

      if (method.isConstructor()) {
        stmtS[1] = langExt.createInvariantStatement(getTokenReference(), local.getQualifiedName().replace('/','.'));

        check(context, expr == null || returnType.getTypeID() != TID_VOID, KjcMessages.RETURN_NONEMPTY_VOID);

        impl = new JBlock(getTokenReference(),
                             new JStatement[] {
                               constrainMethodPost(context.getTypeFactory(), getTokenReference(), stmtS, exprS),
                               new JReturnStatement(getTokenReference(), null, getComments()),
                             }, 
                          null);
      } else {
        impl = new JReturnStatement(getTokenReference(), expr, getComments());
      }
    } else {
      boolean           hasReturnType = (returnType.getTypeID() != TID_VOID);
      boolean           hasThis = !post.isStatic() && !method.isConstructor() && (!post.isPrivate());
      final boolean     hasStore = !method.isConstructor(); //true; //  post.getOldValueStore() != null;

      check(context, post.isPostcondition(), KjcMessages.NOT_A_POSTCONDITION);

      int               i = 0;
      JExpression[]     params = new JExpression[parameters.length + ((hasStore) ? 1 : 0) + ((hasReturnType) ? 1 : 0)+((hasThis)?1:0)];

      if (hasStore) {
        params[i++] = new JNameExpression(getTokenReference(), IDENT_STORAGE);
      }

      // static and private method are not overridden
      if (hasThis) {
        params[i++] = new JNullLiteral(getTokenReference());
      }
      if (hasReturnType) {
        params[i++] = new JNameExpression(getTokenReference(), IDENT_RETURN);
      }
      for (int j = 0; j < parameters.length; j++) {
        params[i++] = new JNameExpression(getTokenReference(), parameters[j].getIdent());
      }
      stmtS[0] = new JExpressionStatement(getTokenReference(),
                                          new KopiMethodCallExpression(getTokenReference(),
                                                                       post, 
                                                                       params){
                                              public void genCode(GenerationContext context, boolean discardValue) {
                                                CodeSequence code = context.getCodeSequence();
                                                
                                                //if there is no storage, remove first arguement
                                                if (hasStore && post.getOldValueStore() == null) {
                                                  JExpression[]         tmp = new JExpression[args.length-1];

                                                  System.arraycopy(args, 1, tmp, 0, tmp.length);
                                                  args = tmp;
                                                }
                                                super.genCode(context, discardValue);
                                              }

                                            }, 
                                          null);
      if (expr != null) {
        check(context, returnType.getTypeID() != TID_VOID, KjcMessages.RETURN_NONEMPTY_VOID);

        CExpressionContext      expressionContext = new CExpressionContext(context, 
                                                                           context.getEnvironment());
        TypeFactory             factory = context.getTypeFactory();

        expr = expr.analyse(expressionContext);
        check(context, expr.isAssignableTo(expressionContext, returnType), KjcMessages.RETURN_BADTYPE, expr.getType(factory), returnType);
        expr = expr.convertType(expressionContext, returnType);
        impl = new JBlock(getTokenReference(),
                             new JStatement[] {
                               // RETTYPE $return;
                               new JVariableDeclarationStatement(getTokenReference(),
                                                                 langExt.createReturnVariable(getTokenReference(), returnType), 
                                                                 null),
                               // $return = expr;
                               new JExpressionStatement(getTokenReference(),
                                                        new JAssignmentExpression(getTokenReference(),
                                                                                  new JNameExpression(getTokenReference(), IDENT_RETURN),
                                                                                  expr), 
                                                        null),
                               // postcondition
                               constrainMethodPost(context.getTypeFactory(), getTokenReference(), stmtS, exprS),
                               // return $return;
                               new JReturnStatement(getTokenReference(),
                                                    new JNameExpression(getTokenReference(), IDENT_RETURN), 
                                                    getComments()),
                             }, null);
      } else {
        // invariant (if it is a constructor)
        if (method.isConstructor()) {
          stmtS[1] = langExt.createInvariantStatement(getTokenReference(), local.getQualifiedName().replace('/','.'));
        }
        check(context, returnType.getTypeID() == TID_VOID, KjcMessages.RETURN_EMPTY_NONVOID);
        impl = new JBlock(getTokenReference(),
                             new JStatement[] {
                               constrainMethodPost(context.getTypeFactory(), getTokenReference(), stmtS, exprS),
                               new JReturnStatement(getTokenReference(), null, getComments()),
                             }, 
                             null);
      }
    }
    impl.analyse(context);
  }

//     if (%(boolean)[0]) {
//       if (!(org.caesarj.assertion.AssertionRuntime.getRunAssertion(Thread.currentThread()))) {
//         org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), true);
//         try {
//           %[0] //postcondition
//           %[1] //invariant (only in constructor!!)
//         } finally {
//           org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), false);
//         }
//       }
//     }

  private static JStatement constrainMethodPost(TypeFactory tf, TokenReference ref, JStatement[] stmts, JExpression[] exprs) {
    CReferenceType               runtimeType = tf.createReferenceType(TypeFactory.RFT_KOPIRUNTIME);
    JMethodCallExpression        testAndSet = new JMethodCallExpression(ref,
                                                                        new JTypeNameExpression(ref, 
                                                                                                runtimeType),
                                                                        "testAndSetRunAssertion", 
                                                                        JExpression.EMPTY);
    JMethodCallExpression        clear = new JMethodCallExpression(ref,
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
                                                                            // invariant (only in constructor!!)
                                                                            new JStatement[] {stmts[0], stmts[1]}, 
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
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    impl.accept(p);
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
  private JExpression           expr;
  private JStatement            impl = null;
}
