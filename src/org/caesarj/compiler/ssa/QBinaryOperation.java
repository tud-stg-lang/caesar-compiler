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
 * $Id: QBinaryOperation.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassfileConstants2;
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
	if (opcode == ClassfileConstants2.opc_iinc) {
	    this.opcode = ClassfileConstants2.opc_iadd;
	}
	this.exception = false;
	switch(opcode) {
	case ClassfileConstants2.opc_dcmpg:
	case ClassfileConstants2.opc_dcmpl:
	case ClassfileConstants2.opc_lcmp:
	case ClassfileConstants2.opc_fcmpg:
	case ClassfileConstants2.opc_fcmpl:
	    this.type = ClassfileConstants2.TYP_INT;
	    break;

	case ClassfileConstants2.opc_fadd:
	case ClassfileConstants2.opc_fmul:
	case ClassfileConstants2.opc_fsub:
	    this.type = ClassfileConstants2.TYP_FLOAT;
	    break;

	case ClassfileConstants2.opc_iadd:
	case ClassfileConstants2.opc_imul:
	case ClassfileConstants2.opc_isub:
	case ClassfileConstants2.opc_ishl:
	case ClassfileConstants2.opc_ishr:
	case ClassfileConstants2.opc_iushr:
	case ClassfileConstants2.opc_iand:
	case ClassfileConstants2.opc_ior:
	case ClassfileConstants2.opc_ixor:
	    this.type = ClassfileConstants2.TYP_INT;
	    break;

	case ClassfileConstants2.opc_dadd:
	case ClassfileConstants2.opc_dmul:
	case ClassfileConstants2.opc_dsub:
	    this.type = ClassfileConstants2.TYP_DOUBLE;
	    break;


	case ClassfileConstants2.opc_ladd:
	case ClassfileConstants2.opc_land:
	case ClassfileConstants2.opc_lmul:
	case ClassfileConstants2.opc_lor:
	case ClassfileConstants2.opc_lsub:
	case ClassfileConstants2.opc_lxor:
	case ClassfileConstants2.opc_lshl:
	case ClassfileConstants2.opc_lshr:
	case ClassfileConstants2.opc_lushr:
	    this.type = ClassfileConstants2.TYP_LONG;
	    break;

	case ClassfileConstants2.opc_idiv:
	case ClassfileConstants2.opc_irem:
	    this.type = ClassfileConstants2.TYP_INT;
	    this.exception = true;
	    break;

	case ClassfileConstants2.opc_fdiv:
	case ClassfileConstants2.opc_frem:
	    this.type = ClassfileConstants2.TYP_FLOAT;
	    this.exception = true;
	    break;

	case ClassfileConstants2.opc_ddiv:
	case ClassfileConstants2.opc_drem:
	    this.type = ClassfileConstants2.TYP_DOUBLE;
	    this.exception = true;
	    break;

	case ClassfileConstants2.opc_ldiv:
	case ClassfileConstants2.opc_lrem:
	    this.type = ClassfileConstants2.TYP_LONG;
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
	return opcode == ClassfileConstants2.opc_iadd;
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
