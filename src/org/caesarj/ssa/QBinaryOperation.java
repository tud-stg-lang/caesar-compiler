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
 * $Id: QBinaryOperation.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import org.caesarj.classfile.Constants;
import org.caesarj.classfile.NoArgInstruction;

/**
 * A binary operation
 */
public class QBinaryOperation extends QExpression {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a binary operation
     *
     * @param op1 the first operand
     * @param op2 the second operand
     * @param opcode the source opcode of the instruction
     */
    public QBinaryOperation(QOperand op1, QOperand op2, int opcode) {
	this.op1 = new QOperandBox(op1, this);
	this.op2 = new QOperandBox(op2, this);
	this.opcode = opcode;
	if (opcode == Constants.opc_iinc) {
	    this.opcode = Constants.opc_iadd;
	}
	this.exception = false;
	switch(opcode) {
	case Constants.opc_dcmpg:
	case Constants.opc_dcmpl:
	case Constants.opc_lcmp:
	case Constants.opc_fcmpg:
	case Constants.opc_fcmpl:
	    this.type = Constants.TYP_INT;
	    break;

	case Constants.opc_fadd:
	case Constants.opc_fmul:
	case Constants.opc_fsub:
	    this.type = Constants.TYP_FLOAT;
	    break;

	case Constants.opc_iadd:
	case Constants.opc_imul:
	case Constants.opc_isub:
	case Constants.opc_ishl:
	case Constants.opc_ishr:
	case Constants.opc_iushr:
	case Constants.opc_iand:
	case Constants.opc_ior:
	case Constants.opc_ixor:
	    this.type = Constants.TYP_INT;
	    break;

	case Constants.opc_dadd:
	case Constants.opc_dmul:
	case Constants.opc_dsub:
	    this.type = Constants.TYP_DOUBLE;
	    break;


	case Constants.opc_ladd:
	case Constants.opc_land:
	case Constants.opc_lmul:
	case Constants.opc_lor:
	case Constants.opc_lsub:
	case Constants.opc_lxor:
	case Constants.opc_lshl:
	case Constants.opc_lshr:
	case Constants.opc_lushr:
	    this.type = Constants.TYP_LONG;
	    break;

	case Constants.opc_idiv:
	case Constants.opc_irem:
	    this.type = Constants.TYP_INT;
	    this.exception = true;
	    break;

	case Constants.opc_fdiv:
	case Constants.opc_frem:
	    this.type = Constants.TYP_FLOAT;
	    this.exception = true;
	    break;

	case Constants.opc_ddiv:
	case Constants.opc_drem:
	    this.type = Constants.TYP_DOUBLE;
	    this.exception = true;
	    break;

	case Constants.opc_ldiv:
	case Constants.opc_lrem:
	    this.type = Constants.TYP_LONG;
	    this.exception = true;
	    break;
	default:
	    this.exception = false;
	}

    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {op1, op2 };
    }

    /**
     * Test the instruction is an integer addition
     */
    public boolean isIadd() {
	return opcode == Constants.opc_iadd;
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return  op1  + " xxx " + op2;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the operand may throw an exception
     */
    public boolean mayThrowException() {
	return exception;
    }

    /**
     * Return the type of the expression.
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
	// TO SEE IINC
	op1.getOperand().generateInstructions(codeGen);
	op2.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new NoArgInstruction(opcode));
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox op1;
    protected QOperandBox op2;
    protected int opcode;
    protected boolean exception;
    protected byte type;
}
