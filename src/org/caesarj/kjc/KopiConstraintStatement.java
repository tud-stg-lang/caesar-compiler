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
 * $Id: KopiConstraintStatement.java,v 1.1 2003-07-05 18:29:41 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;


/**
 * constrains the body of a method to invoke invariant, pre & postcondition
 */
public class KopiConstraintStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   * @param	comment		the statement comment.
   */
  public KopiConstraintStatement(TokenReference where, JBlock stmt, JFormalParameter[] parameters) {
    super(where, null);
    this.stmt = stmt;
    this.parameters = parameters;
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
    TokenReference      ref = getTokenReference();
    JStatement[]        statements = new JStatement[]{null, 
                                                      stmt, 
                                                      new JEmptyStatement(ref, null), 
                                                      new JEmptyStatement(ref, null), 
                                                      new JEmptyStatement(ref, null)};
    CMethod             method = context.getMethodContext().getCMethod();
    CClassContext       classContext = context.getClassContext();
    CClass              clazz = classContext.getCClass();
    CReferenceType          storage = null;
    final CMethod       postmethod = method.getPostconditionMethod();
    CMethod             premethod = method.getPreconditionMethod();

    if (postmethod != null) {
      //      storage = postmethod.getOldValueStore();
      //      if (storage != null) {
        statements[4] = new JVariableDeclarationStatement(ref, 
                                                          new JVariableDefinition(ref, 
                                                                                  0, 
                                                                                  context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT),//storage 
                                                                                  IDENT_STORAGE,
                                                                                  new JNullLiteral(ref)) {

                                                              public void accept(KjcVisitor p) {
                                                                // correct type
                                                                if (postmethod.getOldValueStore() != null) {
                                                                  type = postmethod.getOldValueStore();
                                                                }
                                                                super.accept(p);
                                                              }

                                                              public void genLoad(GenerationContext context) {
                                                                // correct type
                                                                if (postmethod.getOldValueStore() != null) {
                                                                  type = postmethod.getOldValueStore();
                                                                }
                                                                super.genLoad(context);
                                                              }
                                                              
                                                              public void genStore(GenerationContext context) {
                                                                // correct type
                                                                if (postmethod.getOldValueStore() != null) {
                                                                  type = postmethod.getOldValueStore();
                                                                }
                                                                super.genStore(context);
                                                              }

                                                            }, 
                                                          null);
        JUnqualifiedInstanceCreation    storeCreation =
          new KopiOldValueStoreCreation(ref, postmethod);

        statements[3] = new JExpressionStatement(ref,
                                                 new JAssignmentExpression(ref, 
                                                                           new JNameExpression(ref, 
                                                                                               IDENT_STORAGE),
                                                                          storeCreation),
                                                 null){

            public void accept(KjcVisitor p) {
              if (postmethod.getOldValueStore() != null) {
                super.accept(p);
              }
            }

            // produced code only if there is a storage of old values
            public void genCode(GenerationContext context) {
              if (postmethod.getOldValueStore() != null) {
                super.genCode(context);
              }
            }
          };
        //      }
    }

    // precondition
    if (premethod != null) {
      boolean           hasThis = (!premethod.isStatic()) && (!premethod.isPrivate());
      JExpression[]     params = new JExpression[parameters.length+((hasThis)?1:0)];
      int               k = 0;

      // static and private method are not overridden
      if (hasThis) {
        params[k++] = new JNullLiteral(ref);
      }
      for (int i=0; i < parameters.length; i++,k++){
        params[k] = new JNameExpression(ref, parameters[i].getIdent());
      }

      statements[2] = new JExpressionStatement(ref,  
                                               new KopiMethodCallExpression(ref, 
                                                                            premethod,
                                                                            params), 
                                               null);
      check(context, premethod.isPrecondition(), KjcMessages.NOT_A_PRECONDITION);
    }
    // postcondition is done by return !!

    //invariant
    if (!method.isStatic() && !method.isPrivate()) {
      statements[0] = context.getEnvironment().getLanguageExtFactory().createInvariantCallStatement(ref);
    }

    //dummy for return value
    if (method.getReturnType().getTypeID() == TID_VOID) {
      statements[1] = new KopiImplicitReturnBlock(ref, stmt.getBody(), stmt.getComments());
    }

    JExpression[]       expr = new JExpression[]{new JLogicalComplementExpression(ref, new JNameExpression(ref, IDENT_ASSERT)) };

    // expr[0] assert ; stmts[0] invariant, stmts[1] body, stmts[2] pre, stmts[3] post storage, stmts[4] post store var;
    if (method.isStatic() || method.isPrivate()) {
      impl = constrainStaticMethod(context.getTypeFactory(), ref, statements, expr); 
    } else {
      impl = constrainMethodBody(context.getTypeFactory(), ref, statements, expr);
    }
    impl.analyse(context);
  }

//     %[4]     // postcondition  storage
//     if (%(boolean)[0]) {
//       if (!(org.caesarj.assertion.AssertionRuntime.getRunAssertion(Thread.currentThread()))) {
//         org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), true);
//         try {
//           %[0]     // invariant
//           %[2]     // precondition
//           %[3]     // postcondition  storage
//         } finally {
//           org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), false);
//         }
//       }
//     }
//     try {
//       %[1]       // method body
//     } finally {
//       if (%(boolean)[0]) {
//         if (!(org.caesarj.assertion.AssertionRuntime.getRunAssertion(Thread.currentThread()))) {
//           org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), true);
//           try {
//             %[0]   // invariant
//           } finally {
//             org.caesarj.assertion.AssertionRuntime.setRunAssertion(Thread.currentThread(), false);
//           }
//         }
//       }
//     }
 private static JBlock constrainMethodBody(TypeFactory tf, TokenReference ref, JStatement[] stmts, JExpression[] exprs) {
   CReferenceType               runtimeType = tf.createReferenceType(TypeFactory.RFT_KOPIRUNTIME);
   JMethodCallExpression        testAndSet1 = new JMethodCallExpression(ref,
                                                                        new JTypeNameExpression(ref, 
                                                                                                runtimeType),
                                                                        "testAndSetRunAssertion", 
                                                                        JExpression.EMPTY);
   JMethodCallExpression        testAndSet2 = new JMethodCallExpression(ref,
                                                                        new JTypeNameExpression(ref, 
                                                                                                runtimeType),
                                                                        "testAndSetRunAssertion", 
                                                                        JExpression.EMPTY);
   JMethodCallExpression        clear1 = new JMethodCallExpression(ref,
                                                                        new JTypeNameExpression(ref, 
                                                                                                runtimeType),
                                                                        "clearRunAssertion", 
                                                                        JExpression.EMPTY);
   JMethodCallExpression        clear2 = new JMethodCallExpression(ref,
                                                                        new JTypeNameExpression(ref, 
                                                                                                runtimeType),
                                                                        "clearRunAssertion", 
                                                                        JExpression.EMPTY);
   

   return
     new JBlock(ref, 
                new JStatement[]{
                  stmts[4],
                  new JIfStatement(ref,
                                   exprs[0],
                                   new JIfStatement(ref, 
                                                    new JLogicalComplementExpression(ref, testAndSet1),
                                                    new JTryFinallyStatement(ref ,
                                                                             new JBlock(ref, 
                                                                                        new JStatement[] {stmts[0], stmts[2], stmts[3]}, 
                                                                                        null), 
                                                                             new JBlock(ref, 
                                                                                        new JStatement[] {
                                                                               // postcondition  storage
                                                                               new JExpressionStatement(ref, 
                                                                                                        clear1, 
                                                                                                        null)}, 
                                                                                        null), 
                                                                             null), 
                                                    null, 
                                                    null), 
                                   null, 
                                   null), 
                    
                    new JTryFinallyStatement(ref ,
                                             new JBlock(ref, new JStatement[] {stmts[1]}, null),
                                             new JBlock(ref, new JStatement[] {
                                               new JIfStatement(ref, 
                                                                exprs[0],
                                                                new JIfStatement(ref,
                                                                                 new JLogicalComplementExpression(ref, testAndSet2),
                                                                                 new JTryFinallyStatement(ref ,
                                                                                           // invariant
                                                                                     new JBlock(ref, new JStatement[] {stmts[0]}, null),
                                                                                     new JBlock(ref, 
                                                                                                new JStatement[] {
                                                                                                    new JExpressionStatement(ref, clear2, null)}, null), 
                                                                                                          null), 
                                                                                 null, null), 
                                                                null,null)}, 
                                                        null), null)}, 
                null);
}
  // not generated...

  private static JBlock constrainStaticMethod(TypeFactory tf, TokenReference ref, JStatement[] stmts, JExpression[] exprs) {
    CReferenceType              runtimeType = tf.createReferenceType(TypeFactory.RFT_KOPIRUNTIME);
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
      new JBlock(ref, 
                 new JStatement[] {
                    stmts[4], 
                      new JIfStatement(ref,
                                       exprs[0],
                                       new JIfStatement(ref,
                                                        new JLogicalComplementExpression(ref,
                                                                                         testAndSet),
                                                        new JTryFinallyStatement(ref ,
                                                                                 new JBlock(ref, 
                                                                                            // postcondition  storage
                                                                                            new JStatement[] {stmts[2], stmts[3]}, 
                                                                                            null),
                                                                                 new JBlock(ref, 
                                                                                            new JStatement[] {new JExpressionStatement(ref, clear, null)}, 
                                                                                            null), 
                                                                                 null), 
                                                        null, null), 
                                       null, null), 
                      stmts[1]}, 
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
      stmt.accept(p);
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
  private JBlock stmt;
  private JFormalParameter[] parameters;

  private JStatement impl = null;
}
