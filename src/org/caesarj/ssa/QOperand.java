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
 * $Id: QOperand.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

/**
 * An abstract class to represent operand of quadruple instructions
 *
 * QVar, QVarSSA and QConstant are operand in the 3-adress code.
 * The class QExpression also inherite this class. But this kind
 * of operand is used before code Generation to find some stack
 * variables.
 */
public abstract class QOperand {

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the operand may throw an exception
     */
    public abstract boolean mayThrowException();

    /**
     * Test if the operand has side effects.
     */
    public boolean hasSideEffects() {
	return false;
    }

    /**
     * Return the type of the operand.
     */
    public abstract byte getType();

    /**
     * Test if the operand is constant
     */
    public boolean isConstant() {
	return false;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * A representation of the instruction
     */
    public abstract String toString();


    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this operand
     *
     * @param codeGen the code generator
     */
    public abstract void generateInstructions(CodeGenerator codeGen);

    /**
     * Generate the classfile instructions for save in
     * this operand the value actually son the stack.
     */
    public abstract void generateStore(CodeGenerator codeGen);


}
