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
 * $Id: JSuperExpression.java,v 1.2 2004-02-08 20:27:58 ostermann Exp $
 */

package org.caesarj.compiler.ast;

import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * A 'super' expression
 */
public class JSuperExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public JSuperExpression(TokenReference where) {
    this(where, null);
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public JSuperExpression(TokenReference where, JExpression prefix) {
    super(where);
    this.prefix = prefix;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getType
   * Compute the type of this expression (called after parsing)
   * @return	the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return self.getAbstractType(); // !! FIXIFGEN
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

    /* JLS 15.12.1 
       Let T be the type declaration immediately enclosing this declaration. It is 
       a compile-time error if any of the following situations occur: 
       - T is the class Object. 
       - T is an interface. */
    CClass local      = context.getClassContext().getCClass();
    check(context, 
          local != context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).getCClass(), 
          KjcMessages.BAD_USE_SUPER);


    if (prefix == null) {
      self = context.getClassContext().getCClass().getSuperClass();
    } else {
      if ((context.getMethodContext() instanceof CConstructorContext) 
          && !((CConstructorContext)context.getMethodContext()).isSuperConstructorCalled()){
        prefix = prefix.analyse(context);
      } else {
        CExpressionContext        exprContext = new CExpressionContext(context, context.getEnvironment());

        exprContext.setIsTypeName(true);
        prefix = prefix.analyse(exprContext);
      }
      check(context, 
            prefix instanceof JTypeNameExpression, 
            KjcMessages.BAD_SUPER_TYPE);

      CClass clazz      = prefix.getType(factory).getCClass();

      /* JLS 12.11.2, JLS 15.12.1 
         If the form is ClassName.super . The class to be searched is the superclass 
         of the class C denoted by ClassName. It is a compile-time error if C is not
         a lexically enclosing class of the current class. It is a compile-time error 
         if C is the class Object. */
      check(context, 
            local.isDefinedInside(clazz), 
            KjcMessages.BAD_SUPER_TYPE_ENC, local, clazz);
      check(context, 
            clazz != context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).getCClass(), 
            KjcMessages.BAD_USE_SUPER);

      self = clazz.getSuperClass();
    }


    check(context, 
          !context.getMethodContext().getCMethod().isStatic(), 
          KjcMessages.BAD_SUPER_STATIC);

    return this;
  }

  /**
   * Return true iff the node is itself a Expression 
   * (not only a part like JTypeName)
   */
  public boolean isExpression() {
    return false;
  }

  // ----------------------------------------------------------------------
  // Accessors
  // ----------------------------------------------------------------------
  /**
   * Returns the prefix.
   * @author Walter Augusto Werner
   */
  public JExpression getPrefix()
  {
	return prefix;
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitSuperExpression(this);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    if (! discardValue) {
      setLineNumber(code);
      code.plantLoadThis();
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	CClass		self;
  private       JExpression     prefix;
}
