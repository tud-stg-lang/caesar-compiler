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
 * $Id: QVar.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.ssa;

import org.caesarj.util.InconsistencyException;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.Constants;

/**
 * A class to represent variable for quadruple instructions.
 *
 */
public class QVar extends QOperand {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Create a new variable
     *
     * @param register index of the variable
     * @param type type of the variable
     */
    public QVar(int register, byte type) {
	this.register = register;
	this.type = type;
    }

    // -------------------------------------------------------------------
    // ACCESSORS
    // -------------------------------------------------------------------
    /**
     * Test if an object is the same variable
     */
    public boolean equals(Object o) {
	return (o instanceof QVar) && ((QVar) o).register == register;
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
	int opcode;

	switch (getType()) {
	case Constants.TYP_REFERENCE:
	    opcode = Constants.opc_aload;
	    break;
	case Constants.TYP_DOUBLE:
	    opcode = Constants.opc_dload;
	    break;
	case Constants.TYP_FLOAT:
	    opcode = Constants.opc_fload;
	    break;
	case Constants.TYP_INT:
	    opcode = Constants.opc_iload;
	    break;
	case Constants.TYP_LONG:
	    opcode = Constants.opc_lload;
	    break;
	case Constants.TYP_ADDRESS:
	    opcode = Constants.opc_aload;
	    break;
	default:
	    throw new InconsistencyException("unknows type : " + getType());
	}
	codeGen.addInstruction(new LocalVarInstruction(opcode, register));
    }

    /**
     * Generate the classfile instructions for save in
     * this operand the value actually son the stack.
     */
    public void generateStore(CodeGenerator codeGen) {
	int opcode;

	switch (getType()) {
	case Constants.TYP_REFERENCE:
	    opcode = Constants.opc_astore;
	    break;
	case Constants.TYP_DOUBLE:
	    opcode = Constants.opc_dstore;
	    break;
	case Constants.TYP_FLOAT:
	    opcode = Constants.opc_fstore;
	    break;
	case Constants.TYP_INT:
	    opcode = Constants.opc_istore;
	    break;
	case Constants.TYP_LONG:
	    opcode = Constants.opc_lstore;
	    break;
	case Constants.TYP_ADDRESS:
	    opcode = Constants.opc_astore;
	    break;
	default:
	    throw new InconsistencyException("unknows type : " + getType());
	}

	codeGen.addInstruction(new LocalVarInstruction(opcode, register));
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the register of the variable
     */
    public int getRegister() {
	return register;
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "v" + register;
    }
    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected byte type;
    protected int register;
}
