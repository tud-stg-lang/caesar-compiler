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
 * $Id: QUnaryOperation.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassfileConstants2;
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
	case ClassfileConstants2.opc_d2i:
	case ClassfileConstants2.opc_l2i:
	case ClassfileConstants2.opc_f2i:
	case ClassfileConstants2.opc_i2b:
	case ClassfileConstants2.opc_i2c:
	case ClassfileConstants2.opc_i2s:
	case ClassfileConstants2.opc_ineg:
	    this.type = ClassfileConstants2.TYP_INT;
	    break;

	case ClassfileConstants2.opc_d2f:
	case ClassfileConstants2.opc_l2f:
	case ClassfileConstants2.opc_i2f:
	case ClassfileConstants2.opc_fneg:
	    this.type = ClassfileConstants2.TYP_FLOAT;
	    break;

	case ClassfileConstants2.opc_d2l:
	case ClassfileConstants2.opc_f2l:
	case ClassfileConstants2.opc_i2l:
	case ClassfileConstants2.opc_lneg:
	    this.type = ClassfileConstants2.TYP_LONG;
	    break;

	case ClassfileConstants2.opc_l2d:
	case ClassfileConstants2.opc_f2d:
	case ClassfileConstants2.opc_i2d:
	case ClassfileConstants2.opc_dneg:
	    this.type = ClassfileConstants2.TYP_DOUBLE;
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
