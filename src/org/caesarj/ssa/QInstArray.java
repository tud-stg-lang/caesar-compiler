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
 * $Id: QInstArray.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Vector;
import java.util.Iterator;

/**
 * To represent an array of 3-adress instructions for a basic block
 */
public class QInstArray {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a new array of 3-adress instructions
     */
    public QInstArray(BasicBlock bb) {
	this.bb = bb;
	this.insts = new Vector();
    }

    // -------------------------------------------------------------------
    // ACCESSORS
    // -------------------------------------------------------------------
    /**
     * Add a 3-adress instruction int the array
     *
     * @param newInst the instruction to add.
     */
    public void addInstruction(QInst newInst) {
	newInst.setArray(new QInstArrayAccessor(this, insts.size()));
	insts.addElement(newInst);
    }

    /**
     * Get the basic block
     */
    public BasicBlock getBasicBlock() {
	return bb;
    }

    /**
     * Get an instruction iterator
     */
    public Iterator iterator() {
	return insts.iterator();
    }

    /**
     * Get the number of instructions
     */
    public int size() {
	return insts.size();
    }

    /**
     * Replace an instruction in the array
     *
     * @param index the index of instruction to replace in the array
     * @param newInstruction the new instruction
     */
    public void replaceInstruction(int index, QInst newInstruction) {
	((QInst) insts.elementAt(index)).setArray(null);
	newInstruction.setArray(new QInstArrayAccessor(this, index));
	insts.setElementAt(newInstruction, index);
    }

    /**
     * Get the last instruction
     */
    public QInst getLastInstruction() {
	return (QInst) insts.elementAt(insts.size() - 1);
    }

    /**
     * Get the instruction at a given index
     *
     * @param index the instruction index
     */
    public QInst getInstructionAt(int index) {
	return (QInst) insts.elementAt(index);
    }

    /**
     * Remove the last instruction
     *
     * @return the instruction removed
     */
    public QInst removeLastInstruction() {
	if (insts.size() == 0) return null;
	QInst last = (QInst) insts.elementAt(insts.size() - 1);
	last.setArray(null);
	insts.setSize(insts.size() - 1);
	return last;
    }

    /**
     * Remove a SSA instruction
     *
     * @param index the instruction index
     */
    public void removeSSAInstruction(int index) {
	QInst inst = (QInst) insts.elementAt(index);
	//remove all operand uses.
	QOperandBox[] operands = inst.getUses();
	for (int i = 0; i < operands.length; ++i) {
	    if (operands[i].getOperand() instanceof QSSAVar) {
		SSAVar var = ((QSSAVar)operands[i].getOperand()).getSSAVar();
		var.removeUse(operands[i]);
	    }
	}
	if (inst instanceof QPhi) {
	    replaceInstruction(index, new QPhiNop());
	} else {
	    replaceInstruction(index, new QNop());
	}
    }

    /**
     * Add an instruction after the curent instruction
     *
     * @param instruction the instruction to add
     */
    public void insertAfter(int index, QInst instruction) {
	if (index == insts.size() - 1) {
	    addInstruction(instruction);
	} else {
	    QInst next = getInstructionAt(index + 1);
	    if (next instanceof QNop) {
		replaceInstruction(index + 1, instruction);
	    } else {
		//call the vector insertion
		//update all next instructions
		insts.insertElementAt(instruction, index + 1);
		for (++index; index < insts.size(); ++index) {
		    getInstructionAt(index).setArray(new QInstArrayAccessor(this, index));
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected BasicBlock bb;
    protected Vector insts;
}

