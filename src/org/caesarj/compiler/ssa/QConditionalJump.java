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
 * $Id: QConditionalJump.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.Constants;
import org.caesarj.classfile.JumpInstruction;

/**
 * A class to represent a conditional jump
 */
public class QConditionalJump extends QAbstractJumpInst {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a conditional jump instruction
     *
     * @param destTrue the destination if the condition is true
     * @param destFalse the destination if the condition is false
     * @param var operand of the condition
     * @param opcode the code operation of the comparison
     */
    protected QConditionalJump(Edge destTrue, Edge destFalse,
			       QOperand op1, QOperand op2, int opcode) {
	this.destTrue = destTrue;
	this.destFalse = destFalse;
	this.operand1 = new QOperandBox(op1, this);
	this.operand2 = new QOperandBox(op2, this);
	this.opcode = opcode;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the current instruction may throw an exception.
     */
    public  boolean mayThrowException() {
	return false;
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this quadruple instruction
     *
     * @param codeGen the code generator
     */
    public  void generateInstructions(CodeGenerator codeGen) {
	simplifyJump(destTrue);

	if (!tryGenerateUnaryConditionalJump(codeGen)) {
	    operand1.getOperand().generateInstructions(codeGen);
	    operand2.getOperand().generateInstructions(codeGen);
	    codeGen.addInstruction(new JumpInstruction(opcode,
						       new EdgeLabel(destTrue)));
	}

	BasicBlock next = codeGen.getCurrentBasicBlock().getNext();
	//if it's a jump to the next block in the generation order
	if (next != null &&
	    destFalse.getTarget() == next) {
	    return;//don't generate the jump (not necessary).
	} else {
	    simplifyJump(destFalse);
	}
	codeGen.addInstruction(new JumpInstruction(Constants.opc_goto,
						   new EdgeLabel(destFalse)));
    }

    /**
     * Simplify all consecutive jumps
     */
    public void simplifyAllJumps() {
	simplifyJump(destTrue);
	simplifyJump(destFalse);
    }

    /**
     * Try to generate an unary conditional jump
     *
     * @param codeGen the code generator
     * @return true iff an unary conditional jump has been generated
     */
    public boolean tryGenerateUnaryConditionalJump(CodeGenerator codeGen) {
	if (operand2.getOperand() instanceof QConstant) {
	    QConstant constant = (QConstant) operand2.getOperand();
	    if (constant.isNull()) {
		switch (opcode) {
		case Constants.opc_if_acmpeq:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifnull,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_acmpne:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifnonnull,
							       new EdgeLabel(destTrue)));
		    return true;
		}
	    } else if (constant.getValue() instanceof Integer &&
		       ((Integer) constant.getValue()).intValue() == 0) {
		switch (opcode) {
		case Constants.opc_if_icmpeq:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifeq,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_icmpne:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifne,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_icmplt:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_iflt,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_icmpge:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifge,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_icmpgt:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifgt,
							       new EdgeLabel(destTrue)));
		    return true;
		case Constants.opc_if_icmple:
		    operand1.getOperand().generateInstructions(codeGen);
		    codeGen.addInstruction(new JumpInstruction(Constants.opc_ifle,
							       new EdgeLabel(destTrue)));
		    return true;
		}
	    }
	}
	return false;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {operand1, operand2};
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "if " + operand1 + " xxx " + operand2;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox operand1;
    protected QOperandBox operand2;
    protected int opcode;
    protected Edge destTrue;
    protected Edge destFalse;
}
