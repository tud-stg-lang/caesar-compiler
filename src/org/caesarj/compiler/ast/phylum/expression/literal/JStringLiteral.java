/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
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
 * $Id: JStringLiteral.java,v 1.3 2005-01-24 16:53:02 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression.literal;

import org.caesarj.classfile.PushLiteralInstruction;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * A simple character constant
 */
public class JStringLiteral extends JLiteral {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	image		the string representation of this literal
   */
  public JStringLiteral(TokenReference where, String image) {
    this(where, image, false);
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	image		the string representation of this literal
   * @param	quoted		there is quote around image
   */
  public JStringLiteral(TokenReference where, String image, boolean quoted) {
    super(where);

    if (image == null) {
      throw new InconsistencyException();
    }

    if (quoted) {
      StringBuffer s = new StringBuffer();
      for (int i = 0; i < image.length(); i++) {
	char c = image.charAt(i);
	if (c == '\\') {
	  if (i + 1 < image.length() - 1) {
	    i++;
	    c = image.charAt(i);
	    switch (c) {
	    case 'n' : c = '\n'; break;
	    case 'r' : c = '\r'; break;
	    case 't' : c = '\t'; break;
	    case 'b' : c = '\b'; break;
	    case 'f' : c = '\f'; break;
	    case '"' : c = '\"'; break;
	    case '\'' : c = '\''; break;
	    case '\\' : c = '\\'; break;
	    }
	  }
	}
	s.append(c);
      }
      value = s.toString();
      value = value.substring(1, value.length() - 1);
    } else {
      value = image;
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.createReferenceType(TypeFactory.RFT_STRING);
  }

  /**
   * Returns the constant value of the expression.
   */
  public String stringValue() {
    return value;
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public boolean isDefault() {
    return false;
  }

  /**
   * Returns a string representation of this literal.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append("JStringLiteral[");
    buffer.append(value);
    buffer.append("]");
    return buffer.toString();
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
  public JExpression analyse(CExpressionContext context) {
    return this;
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

    if (! discardValue) {
      setLineNumber(code);
      code.plantInstruction(new PushLiteralInstruction(value));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	String		value;
}
