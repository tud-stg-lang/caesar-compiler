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
 * $Id: JConstructorBlock.java,v 1.1 2004-02-08 16:47:41 ostermann Exp $
 */

package org.caesarj.compiler.ast;

import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents the body of a constructor.
 */
public class JConstructorBlock extends JBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	constructorCall	an explicit constructor invocation
   * @param	body		the statements contained in the block
   */
  public JConstructorBlock(TokenReference where,
			   JConstructorCall constructorCall,
			   JStatement[] body)
  {
    super(where, body, null);
    this.constructorCall = constructorCall;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the constructor called by this constructor.
   */
  public CMethod getCalledConstructor() {
    return constructorCall == null ? null : constructorCall.getMethod();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the constructor body (semantically).
   *
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    sourceClass = (CSourceClass)context.getClassContext().getCClass();

    // JLS 8.8.5 :
    // If a constructor body does not begin with an explicit constructor
    // invocation and the constructor being declared is not part of the
    // primordial class Object, then the constructor body is implicitly
    // assumed by the compiler to begin with a superclass constructor
    // invocation "super();", an invocation of the constructor of its
    // direct superclass that takes no arguments.
    CType objectType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);

    if (constructorCall == null && !(sourceClass.getQualifiedName() == JAV_OBJECT)) {
      constructorCall = new JConstructorCall(getTokenReference(),
					     false,
					     JExpression.EMPTY);
    }

    if (sourceClass.isNested()) {
      paramsLength = context.getMethodContext().getCMethod().getParameters().length;
    }

    // Insert a call to the instance initializer, iff :
    // - there exists an instance initializer
    // - there is no explicit invocation of a constructor of this class
    if (! context.getClassContext().hasInitializer()
	|| constructorCall == null
	|| constructorCall.isThisInvoke()) {
      initializerCall = null;
    } else {
      // "Block$();"
      initializerCall =
	new JExpressionStatement(getTokenReference(),
				 new JMethodCallExpression(getTokenReference(),
							   null,
							   JAV_INIT,
							   JExpression.EMPTY),
				 null);
    }

    if (constructorCall != null) {
      constructorCall.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (constructorCall.isThisInvoke()) {
	((CConstructorContext)context.getMethodContext()).markAllFieldToInitialized();
      }
    }


    if (initializerCall != null) {
      initializerCall.analyse(context);
      ((CConstructorContext)context.getMethodContext()).adoptInitializerInfo();
    }

    super.analyse(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitConstructorBlockStatement(this, constructorCall, body, getComments());
  }

   /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    if (constructorCall != null) {
      constructorCall.genCode(context, true);
    }

    if (sourceClass.isNested()) {
      sourceClass.genInit(context, paramsLength);
    }

    if (initializerCall != null) {
      initializerCall.genCode(context);
    }

    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }

    //!!! graf 010529 : needed ?
    //    code.plantNoArgInstruction(opc_return);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JConstructorCall		constructorCall;
  private JStatement			initializerCall;
  private CSourceClass			sourceClass;
  private int				paramsLength;
}
