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
 * $Id: KopiStoreFieldAccessExpression.java,v 1.2 2003-10-29 12:29:08 kloppenburg Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

/**
 * A Store Field Access Expression.
 *
 * A field access expression may access a field of an object or array.
 */
public class KopiStoreFieldAccessExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   *
   * @param	where		the line of this node in the source code
   * @param	prefix		the prefix denoting the object to search
   * @param	decl		field declaration which must be analysed 
   *                            somewhere else.
   * @param     type            the type decl will have after analysation
   */
  public KopiStoreFieldAccessExpression(TokenReference where,
                                   JExpression prefix,
                                   JFieldDeclaration decl,
                                   CType type)
  {
    super(where);
    this.prefix = prefix;
    this.decl = decl;
    this.type = type;
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
    prefix = prefix.analyse(context);
    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    // !! to do
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    JExpression         expr = new JFieldAccessExpression(getTokenReference(), prefix, decl.getField());
    // with this constructor it must not be analysed

    expr.genCode(context, discardValue);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		prefix;
  private JFieldDeclaration     decl;
  private CType			type;
}
 
