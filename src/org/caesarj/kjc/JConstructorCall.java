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
 * $Id: JConstructorCall.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * This class represents a explicit call to a super or self constructor
 */
public class JConstructorCall extends JExpression {

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	functorIsThis	true if functor is "this" (else "super")
   * @param	arguments	the argument of the call
   */
  public JConstructorCall(TokenReference where,
			  boolean functorIsThis,
			  JExpression[] arguments) {
    this(where, functorIsThis, null, arguments);
  }
  public JConstructorCall(TokenReference where,
			  boolean functorIsThis,
                          JExpression expr,
			  JExpression[] arguments)
  {
    super(where);

    this.expr = expr;
    this.functorIsThis = functorIsThis;
    this.arguments = arguments;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the called method.
   */
  public CMethod getMethod() {
    return method;
  }

  /**
   * Returns true if it's this() else it's super().
   */
  //Walter start
  //final boolean isThisInvoke() {
  public final boolean isThisInvoke() {
  //Walter end
    return functorIsThis;
  }

  /**
   * !!!
   */
  public CType getType(TypeFactory factory) {
    return null;
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
    ((CConstructorContext)context.getMethodContext()).setSuperConstructorCalled(false);
    // !!! check in constructor !!!
    TypeFactory         factory = context.getTypeFactory();

    if (expr != null) {
      // analyse super prefix
//       CBlockContext	block = new CBlockContext(context.getMethodContext(), 
//                                                   context.getEnvironment(), 
//                                                   context.getMethodContext().getCMethod().getParameters().length);
      CExpressionContext exprContext = new CExpressionContext(context.getBlockContext(), context.getEnvironment()) {
          public CTypeVariable lookupTypeVariable(String ident)
            throws UnpositionedError
          {
            return null;
          }
      };
      expr = expr.analyse(exprContext);

      // FIX following for generic:
      CClass    superClass = context.getClassContext().getCClass().getSuperClass();

      check(context, 
            superClass != null && superClass.isNested() && superClass.hasOuterThis() && expr.getType(factory).getCClass().descendsFrom(superClass.getOwner()), 
            KjcMessages.BAD_SUPER_PREFIX);
      check(context, !functorIsThis, KjcMessages.BAD_SUPER_PREFIX);
      if (expr instanceof JTypeNameExpression) {
        expr = null;
      }
    } else {
      // can expr be null ?
      CClass    clazz = context.getClassContext().getCClass();
      CClass    superClass = clazz.getSuperClass();

      // FIX !!!! 04.06.2002 qualified prefix? 
      check(context, 
            ! isThisInvoke()
            || !superClass.hasOuterThis() 
            || (clazz.hasOuterThis() && inCorrectOuter(clazz.getOwner(), superClass.getOwner())),
            KjcMessages.MISSING_SUPER_PREFIX);
    }

    CType[]	argsType = new CType[arguments.length];

    for (int i = 0; i < argsType.length; i++) {
      arguments[i] = arguments[i].analyse(context);

      argsType[i] = arguments[i].getType(factory);
      verify(argsType[i] != null);
    }
    inClass = context.getClassContext().getCClass();

    if (functorIsThis) {
      clazz = context.getClassContext().getCClass();
    } else {
      clazz = context.getClassContext().getCClass().getSuperClass();
    }

    verify(clazz != null);
    try {
      method = clazz.lookupMethod(context, context.getClassContext().getCClass(), null, JAV_CONSTRUCTOR, argsType, context.getClassContext().getCClass().getSuperType().getArguments());
      exprContext = context;
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    check(context, method != null, KjcMessages.CONSTRUCTOR_NOTFOUND, clazz.getIdent());

    if (method.getOwner() != clazz) {
      // May be an inner class
      if (clazz.isNested()) {
	CType[]		argsType2 = new CType[argsType.length + 1];

	System.arraycopy(argsType, 0, argsType2, 0, argsType.length);
	argsType2[argsType.length] = clazz.getOwnerType();
	try {
	  method = clazz.lookupMethod(context, context.getClassContext().getCClass(), null, JAV_CONSTRUCTOR, argsType2, context.getClassContext().getCClass().getTypeVariables());
          exprContext = context;
	} catch (UnpositionedError e) {
	  throw e.addPosition(getTokenReference());
	}
      }
      if (method.getOwner() != clazz) {
	// do not want a super constructor !
	throw new CMethodNotFoundError(getTokenReference(), null, clazz.getAbstractType().toString(), argsType);
      }
    }

    CReferenceType[]	exceptions = method.getThrowables();
    for (int i = 0; i < exceptions.length; i++) {
      if (exceptions[i].isCheckedException(context)) {
	context.getBodyContext().addThrowable(new CThrowableInfo(exceptions[i], this));
      }
    }

    check(context, !context.getMethodContext().getCMethod().isStatic(), KjcMessages.BAD_THIS_STATIC);

    argsType = method.getParameters();
    for (int i = 0; i < arguments.length; i++) {
      arguments[i] = arguments[i].convertType(context, argsType[i]);
    }
    ((CConstructorContext)context.getMethodContext()).setSuperConstructorCalled(true);

    return this;
  }

  public void setSyntheticOuter(CClass outerThis) throws PositionedError {
    if (exprContext == null) {
      return;
    }

    CConstructorContext         ctrContext = (CConstructorContext)exprContext.getMethodContext();

    ctrContext.setSuperConstructorCalled(false);
    synOuter = new JOwnerExpression(getTokenReference(), outerThis).analyse(exprContext);
    ctrContext.setSuperConstructorCalled(true);
    
    exprContext = null;
  }

   private boolean inCorrectOuter(CClass local, CClass outer) {
    while (local != null) {
      if (local.descendsFrom(outer)) {
        return true;
      }
      local = local.getOwner();
    }
    return false;
  }

 // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    exprContext = null;
    p.visitConstructorCall(this, functorIsThis, arguments);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    exprContext = null;
    setLineNumber(code);
    code.plantLoadThis();

    CClass      owner = method.getOwner();

    //    if (owner.isNested() && owner.hasOuterThis()) {
      //      clazz.genSyntheticParamsFromExplicitSuper(inClass.isQualifiedAndAnonymous(), code);
    //}
    if (owner.isNested() && !owner.isStatic() && owner.hasOuterThis() && expr == null) {
      if (synOuter != null) {
        synOuter.genCode(context, false);
      } else {
        JGeneratedLocalVariable var = new JGeneratedLocalVariable(null, 
                                                                  0, 
                                                                  owner.getOwnerType(), 
                                                                  JAV_OUTER_THIS, 
                                                                  null);
      /* If it is not qualified, the first*/
        var.setPosition(inClass.isQualifiedAndAnonymous() ? 2 : 1);
        new JLocalVariableExpression(TokenReference.NO_REF, var).genCode(context, false);
      }
    }
    if (expr != null) {
      expr.genCode(context, false);
      // null check
      CodeLabel         ok = new CodeLabel();

      code.plantNoArgInstruction(opc_dup);
      code.plantJumpInstruction(opc_ifnonnull, ok);
      code.plantNoArgInstruction(opc_aconst_null);
      code.plantNoArgInstruction(opc_athrow);
      code.plantLabel(ok);
    }

    for (int i = 0; i < arguments.length; i++) {
      arguments[i].genCode(context, false);
    }

    method.getOwner().genOuterSyntheticParams(context, method.getParameters().length);
    method.genCode(context, true);

    // The return type is void : there is no result value.
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean		functorIsThis;
  //Walter start
  //private JExpression[]	arguments;
  protected JExpression[]	arguments;
  //Walter end
  private JExpression	expr;
  private CExpressionContext    exprContext;
  private CClass		clazz;
  private CClass		inClass;
  private CMethod		method;
  private JExpression   synOuter = null;
}
