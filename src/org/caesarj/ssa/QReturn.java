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
 * $Id: QReturn.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import org.caesarj.classfile.NoArgInstruction;
import org.caesarj.classfile.Constants;

/**
 * A class to represent a return instruction in 3-adress code.
 */
public class QReturn extends QInst {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a return instruction which return nothing.
     */
    public QReturn() {
	this(null, Constants.opc_return);
    }
    /**
     * Construct a return instruction
     *
     * @param op the operand of the instruction.
     * @param opcode the opcode of the source instruction.
     */
    public QReturn(QOperand op, int opcode) {
	if (op != null)
	    this.op = new QOperandBox(op, this);
	this.opcode = opcode;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the current instruction may throw an exception.
     *
     * We consider that this exception throw an exception not to
     * permit code moves over this instruction.
     */
    public boolean mayThrowException() {
	return true;
    }

    /**
     * Test if the instruction has side effects.
     */
    public boolean hasSideEffects() {
	return true;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	if (op == null)
	    return new QOperandBox[0];
	return new QOperandBox[] {op};
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "return " + op;
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
	if (op != null)
	    op.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new NoArgInstruction(opcode));
    }


    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    public QOperandBox op;
    public int opcode;
}
