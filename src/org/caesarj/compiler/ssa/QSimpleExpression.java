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
 * $Id: QSimpleExpression.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.util.InconsistencyException;

/**
 * A simple expression : a variable or a constant.
 */
public class QSimpleExpression extends QExpression {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a simple expression.
     *
     * Precondition : expr is not instance of QExpression.
     *
     * @param expr the expression
     */
    public QSimpleExpression(QOperand expr) {
	if (expr instanceof QExpression)
	    throw new InconsistencyException("Not a simple expression " +
					     expr);
	this.expr = new QOperandBox(expr, this);

    }
    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the operand may throw an exception
     */
    public boolean mayThrowException() {
	return false;
    }

    /**
     * Return the type of the operand.
     */
    public byte getType() {
	return expr.getOperand().getType();
    }

    /**
     * Test if the expression is simple (a variable or a constant).
     */
    public boolean isSimple() {
	return true;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {expr };
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return expr.toString();
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this operand
     *
     * @param codeGen the code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	expr.getOperand().generateInstructions(codeGen);
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox expr;
}
