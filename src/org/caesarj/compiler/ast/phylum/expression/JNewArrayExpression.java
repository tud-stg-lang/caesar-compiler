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
 * $Id: JNewArrayExpression.java,v 1.2 2004-09-06 13:31:35 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.classfile.MultiarrayInstruction;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CArrayType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 15.10 Array Creation Expressions.
 *
 * An array instance creation expression is used to create new arrays.
 */
public class JNewArrayExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	type		the type of elements of this array
   * @param	dims		the dimensions of the array
   * @param	init		an initializer for the array
   */
  public JNewArrayExpression(TokenReference where,
			     CType type,
			     JExpression[] dims,
			     JArrayInitializer init)
  {
    super(where);

    this.type = new CArrayType(type, dims.length);
    this.dims = dims;
    this.init = init;
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
    boolean	hasBounds;

    hasBounds = analyseDimensions(context);

    try {
      type = (CArrayType)type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    if (init == null) {
      check(context, hasBounds, KjcMessages.MULTIARRAY_BOUND_MISSING);
    } else {
      check(context, !hasBounds, KjcMessages.ARRAY_BOUND_AND_INITIALIZER);
      init.setType(type);
      /*!!! init =*/ init.analyse(context);
    }

    return this;
  }

  /**
   * Analyses the dimension array.
   *
   * @return	true iff there are expressions for some sizes
   */
  private boolean analyseDimensions(CExpressionContext context)
    throws PositionedError
  {
    TypeFactory         factory = context.getTypeFactory();

    boolean	lastEmpty = false;	// last dimension empty ?
    boolean	hasBounds = false;	// expressions for some sizes ?

    for (int i = 0; i < dims.length; i++) {
      if (dims[i] == null) {
	lastEmpty = true;
      } else {
	check(context, !lastEmpty, KjcMessages.MULTIARRAY_BOUND_MISSING);
	dims[i] = dims[i].analyse(context);
	check(context,
	      dims[i].getType(factory).isAssignableTo(context, context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT)),
	      KjcMessages.ARRAY_BADTYPE, dims[i].getType(factory));
	hasBounds = true;
      }
    }
    return hasBounds;
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
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    if (init == null) {
      allocArray(context, type, dims);
      if (discardValue) {
	code.plantPopInstruction(type);
      }
    } else {
      init.genCode(context, discardValue);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public static void allocArray(GenerationContext context, CArrayType type, JExpression[] dims) {
    CodeSequence code = context.getCodeSequence();

    for (int i = 0; i < dims.length; i++) {
      if (dims[i] != null) {
	dims[i].genCode(context, false);
      }
    }

    if (type.getArrayBound() > 1) {
      int		filled = 0;

      for (int i = 0; i < dims.length && dims[i] != null; i++) {
	filled++;
      }
      code.plantInstruction(new MultiarrayInstruction(type.getSignature(), filled));
    } else {
      code.plantNewArrayInstruction(type.getElementType());
    }
  }

  public void recurse(IVisitor s) {
      for (int i = 0; i < dims.length; i++) {
        dims[i].accept(s);
      }
      init.accept(s);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CArrayType			type;
  private JExpression[]			dims;
  private JArrayInitializer		init;
}
