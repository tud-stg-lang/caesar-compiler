/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: JDoubleLiteral.java,v 1.3 2005-01-24 16:53:01 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression.literal;

import org.caesarj.classfile.PushLiteralInstruction;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 3.10.2 Floating-Point Literals. This class represents double literals.
 */
public class JDoubleLiteral extends JLiteral {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a literal expression from a textual representation.
   * @param	where		the line of this node in the source code
   * @param	image		the textual representation of this literal
   */
  public JDoubleLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    try {
      this.value = Double.valueOf(image).doubleValue();
    } catch (NumberFormatException e) {
      throw new PositionedError(where, KjcMessages.INVALID_DOUBLE_LITERAL, image);
    }
    if (Double.isInfinite(this.value)) {
      throw new PositionedError(where, KjcMessages.DOUBLE_LITERAL_OVERFLOW, image);
    }
    // cannot be negative since - is an operator :
    if (this.value == 0 && !isZeroLiteral(image)) {
      throw new PositionedError(where, KjcMessages.DOUBLE_LITERAL_UNDERFLOW, image);
    }
  }

  /*
   * Is this a zero literal, i.e. 0.0, 0e22, 0d ... ?
   */
  private boolean isZeroLiteral(String image) {
    char[]	chars = image.toCharArray();

    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
	return false;
      case 'd':
      case 'D':
      case 'e':
      case 'E':
	return true;
      }
    }

    return true;
  }

  /**
   * Constructs a literal expression from a constant value.
   * @param	where		the line of this node in the source code
   * @param	value		the constant value
   */
  public JDoubleLiteral(TokenReference where, double value) {
    super(where);
    this.value = value;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the type of this expression.
   */
  public CType getType(TypeFactory factory) {
    return factory.getPrimitiveType(TypeFactory.PRM_DOUBLE);
  }

  /**
   * Returns the constant value of the expression.
   */
  public double doubleValue() {
    return value;
  }

  /**
   * Returns true iff the value of this literal is the
   * default value for this type (JLS 4.5.5).
   */
  public boolean isDefault() {
    return Double.doubleToLongBits(value) == ZERO_BITS;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * convertType
   * changes the type of this expression to an other
   * @param  dest the destination type
   */
  public JExpression convertType(CExpressionContext context, CType dest) {
    switch (dest.getTypeID()) {
    case TID_BYTE:
      return new JByteLiteral(getTokenReference(), (byte)value);
    case TID_SHORT:
      return new JShortLiteral(getTokenReference(), (short)value);
    case TID_CHAR:
      return new JCharLiteral(getTokenReference(), (char)value);
    case TID_INT:
      return new JIntLiteral(getTokenReference(), (int)value);
    case TID_LONG:
      return new JLongLiteral(getTokenReference(), (long)value);
    case TID_FLOAT:
      return new JFloatLiteral(getTokenReference(), (float)value);
    case TID_DOUBLE:
      return this;
    case TID_CLASS:
      if (dest != context.getTypeFactory().createReferenceType(TypeFactory.RFT_STRING)) {
	throw new InconsistencyException("cannot convert from double to " + dest);
      }
      return new JStringLiteral(getTokenReference(), ""+value);
    default:
      throw new InconsistencyException("cannot convert from double to " + dest);
    }
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

  private static final long	ZERO_BITS = Double.doubleToLongBits(0d);
  private final double		value;
}
