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
 * $Id: KopiPreconditionStatement.java,v 1.2 2003-10-29 12:29:07 kloppenburg Exp $
 */

package org.caesarj.kjc;

import java.util.ArrayList;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

public class KopiPreconditionStatement extends JStatement {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   * @param	comment		the statement comment.
   */
  public KopiPreconditionStatement(TokenReference where, JFormalParameter[]  parameters, CType returnType, JBlock stmt) {
        super(where, null);
        this.stmt = stmt;
        this.parameters = parameters;
        this.returnType = returnType;
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
    check(context,
          !context.getMethodContext().getCMethod().isNative(),
          KjcMessages.NATIVE_WITH_COND, 
          context.getMethodContext().getCMethod().getIdent());
    conditionContext = context;
  }

  public void analyseConditions()  throws PositionedError {
    if (conditionContext == null) {
      return;
    }
    TokenReference      ref = getTokenReference();
    CMethod             method = conditionContext.getMethodContext().getCMethod();
    boolean             hasReturnType = (returnType.getTypeID() != TID_VOID);
    ArrayList           methods = new ArrayList();

    if (stmt != null) {
      methods.add(stmt);
    }

    CMethod[]      superMethods = ((CSourceMethod)method).getSuperMethods();

    if ((!method.isStatic()) && (!method.isPrivate()) && (superMethods.length > 0)) {
      for(int idx=0; idx < superMethods.length; idx++) {
        boolean         isInInterface = (superMethods[idx].getOwner().getIdent()).endsWith(LanguageExtensions.IDENT_CLASS_ASSERT);

        JExpression[]   params = new JExpression[parameters.length+1]; //((isInInterface)?1:0)];
        JExpression     prefix;
        int             i = 0;

        if (isInInterface) {
          params[i++] = new JThisExpression(ref);
          prefix = new JTypeNameExpression(ref, superMethods[idx].getOwner().getAbstractType());
        } else {
          params[i++] = new JNullLiteral(ref); //new JCastExpression(ref, new JNullLiteral(ref), m.getOwner().getAbstractType());
          prefix = new JThisExpression(ref);
        }
        for (int k=0; k < parameters.length; k++, i++) {
          params[i] = new JNameExpression(ref, parameters[k].getIdent());
        }

        JStatement methodcall = 
          new JExpressionStatement(ref,
                                   new KopiMethodCallExpression(ref, prefix, superMethods[idx], params), 
                                   null);
        methods.add(methodcall);
      }
    }

    JStatement[]        statements = (JStatement[])methods.toArray(new JStatement[methods.size()]);

    impl = createConditionCascade(conditionContext.getTypeFactory(), statements);
    impl.analyse(conditionContext);
  }

  private JStatement createConditionCascade(TypeFactory tf, JStatement[] preconditions) {
    TokenReference ref = getTokenReference();
    if (preconditions.length == 0) {
      return new JEmptyStatement(ref, null);
    } else if (preconditions.length == 1) {
      return preconditions[0];
    } else {
      JStatement condBlock = preconditions[preconditions.length-1];
      for (int i=preconditions.length-2; i >= 0; i--) {
        condBlock = createPrecondition(ref, tf, preconditions[i], condBlock ,(IDENT_EXCEPTION+i).intern(), (IDENT_EXCEPTION+"0"+i).intern());
      }
      return condBlock;
    }
  }

  public JStatement createPrecondition(TokenReference ref, TypeFactory tf, JStatement stmts0, JStatement stmts1,  String str0, String str1) {
        return new JBlock(ref,
            new JStatement[] {
                  new JTryCatchStatement(ref,
                  new JBlock(ref,
                  new JStatement[] {
                    stmts0
                  }, null),
                  new JCatchClause[] {
                        new JCatchClause(ref,
                        new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, tf.createType(KOPI_ERROR_PRECOND, false), str0, false),
                        new JBlock(ref,
                        new JStatement[] {
                              new JTryCatchStatement(ref,
                              new JBlock(ref,
                              new JStatement[] {
                                stmts1
                              }, null),
                              new JCatchClause[] {
                                    new JCatchClause(ref,
                                    new JFormalParameter(ref, JLocalVariable.DES_PARAMETER,tf.createType(KOPI_ERROR_PRECOND, false), str1, false),
                                    new JBlock(ref,
                                    new JStatement[] {
                                          new JExpressionStatement(ref,
                                               new JMethodCallExpression(ref,
                                                                         new JNameExpression(ref, str0), 
                                                                         "setSuperPreconditionError",
                                                                         new JExpression[] { new JNameExpression(ref, str1) }), 
                                                                   null),
                                          new JThrowStatement(ref,
                                          new JNameExpression(ref, str0), null)
                                    }, null))
                              }, null)
                        }, null))
                  }, null)
            }, null);
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
  private JBlock                stmt;
  private CType                 returnType;
  private JFormalParameter[]    parameters;
  private JStatement            impl = null;
  private CBodyContext          conditionContext;
}
