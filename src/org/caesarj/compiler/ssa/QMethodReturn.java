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
 * $Id: QMethodReturn.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.classfile.InterfaceConstant;
import org.caesarj.classfile.InvokeinterfaceInstruction;
import org.caesarj.classfile.MethodRefConstant;
import org.caesarj.classfile.MethodRefInstruction;
import org.caesarj.classfile.ReferenceConstant;

/**
 * A class to represent method call with a result (non void).
 */
public class QMethodReturn extends  QCallReturn {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the instruction
     *
     * @param method the method
     * @param ops object and parameter of the method
     * @param typeReturn the return type of the method
     * @param opcode source code operation
     */
    public QMethodReturn(ReferenceConstant method, QOperand[] ops,
			 byte typeReturn, int opcode) {
	this.method = method;
	operands = new QOperandBox[ops.length];
	for (int i = 0; i < operands.length; ++i) {
	    operands[i] = new QOperandBox(ops[i], this);
	}
	this.type = typeReturn;
	this.interfaceNbArgs = -1;
	this.opcode = opcode;
    }

    /**
     * Construct the instruction for an invoke interface
     *
     * @param method the method
     * @param ops object and parameter of the method
     * @param typeReturn the return type of the method
     * @param opcode source code operation
     * @param nbargs number of args for the interface
     */
    public QMethodReturn(ReferenceConstant method, QOperand[] ops,
			 byte typeReturn, int opcode, int nbargs) {
	this.method = method;
	operands = new QOperandBox[ops.length];
	for (int i = 0; i < operands.length; ++i) {
	    operands[i] = new QOperandBox(ops[i], this);
	}
	this.type = typeReturn;
	this.opcode = opcode;
	this.interfaceNbArgs = nbargs;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the operand has side effects.
     */
    public boolean hasSideEffects() {
	return true;
    }

    /**
     * Return the type of the expression
     */
    public byte getType() {
	return type;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return operands;
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	String tmp = "";
	if (opcode != ClassfileConstants2.opc_invokestatic) {
	    tmp += operands[0] + ".";
	}
	tmp += method.getName() + "(";
	for (int i = 1; i < operands.length; ++i) {
	    tmp += operands[i];
	    if (i != operands.length - 1) {
		tmp += ", ";
	    }
	}
	tmp += ")";
	return tmp;
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
	for (int i = 0; i < operands.length; ++i) {
	    operands[i].getOperand().generateInstructions(codeGen);
	}
	if (opcode == ClassfileConstants2.opc_invokeinterface) {
	    codeGen.addInstruction(new InvokeinterfaceInstruction((InterfaceConstant) method, interfaceNbArgs));
	} else {
	    codeGen.addInstruction(new MethodRefInstruction(opcode, (MethodRefConstant) method));
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected ReferenceConstant method;
    protected QOperandBox[] operands;
    protected byte type;
    protected int opcode;
    protected int interfaceNbArgs;

}
