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
 * $Id: QUnaryOperation.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.Constants;
import org.caesarj.classfile.NoArgInstruction;

/**
 * A unary operation
 */
public class QUnaryOperation extends QExpression {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a unary operation
     *
     * @param op the operation operand
     * @param opcode the source opcode of the instruction
     */
    public QUnaryOperation(QOperand op, int opcode) {
	this.op = new QOperandBox(op, this);
	this.opcode = opcode;
	switch(opcode) {
	case Constants.opc_d2i:
	case Constants.opc_l2i:
	case Constants.opc_f2i:
	case Constants.opc_i2b:
	case Constants.opc_i2c:
	case Constants.opc_i2s:
	case Constants.opc_ineg:
	    this.type = Constants.TYP_INT;
	    break;

	case Constants.opc_d2f:
	case Constants.opc_l2f:
	case Constants.opc_i2f:
	case Constants.opc_fneg:
	    this.type = Constants.TYP_FLOAT;
	    break;

	case Constants.opc_d2l:
	case Constants.opc_f2l:
	case Constants.opc_i2l:
	case Constants.opc_lneg:
	    this.type = Constants.TYP_LONG;
	    break;

	case Constants.opc_l2d:
	case Constants.opc_f2d:
	case Constants.opc_i2d:
	case Constants.opc_dneg:
	    this.type = Constants.TYP_DOUBLE;
	    break;
	}

    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {op };
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "xxx " + op;
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
     * Return the type of the expression
     */
    public byte getType() {
	return type;
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
	op.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new NoArgInstruction(opcode));
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox op;
    protected int opcode;
    protected byte type;
}
