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
 * $Id: QPhiReturn.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

/**
 * PhiReturn
 */
public class QPhiReturn extends QPhi {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param var variable used in phi in non SSA form
     * @param subroutine the subroutine associated with the Phi Return
     */
    public QPhiReturn(QVar var, SubRoutine subroutine) {
	super(var);
	this.operand = new QOperandBox(var, this);
	this.subroutine = subroutine;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------

    /**
     * Get the operand of the phi return
     */
    public QOperandBox getOperand() {
	return operand;
    }


    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] { operand };
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return variableDefined + " = phiReturn(" +
	    operand + ")";
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this quadruple instruction
     *
     * @param codeGen the code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	//TO SEE
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected QOperandBox operand;
    protected SubRoutine subroutine;
}
