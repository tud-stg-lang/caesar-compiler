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
 * $Id: QInst.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

/**
 * A abstract class to represent an instruction in 3-adress code.
 */
public abstract class QInst {

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the current instruction may throw an exception.
     */
    public abstract boolean mayThrowException();

    /**
     * Test if the instruction has side effects.
     */
    public boolean hasSideEffects() {
	return false;
    }

    /**
     * Test if the instruction define a local variable.
     */
    public boolean defVar() {
	return false;
    }

    /**
     * Get the operand defined if the instruction define one
     */
    public QOperandBox getDefined() {
	return null;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public abstract QOperandBox[] getUses();

    /**
     * A representation of the instruction
     */
    public abstract String toString();

    /**
     * Set in which array the instruction is
     *
     * @param arrayAccess the array in which the instruction is
     */
    /*package*/ void setArray(QInstArrayAccessor arrayAccess) {
	this.arrayAccess = arrayAccess;
    }

    /**
     * Get the basic block in which the instruction is
     *
     * @return the basic block using this instruction
     */
    public BasicBlock getBasicBlock() {
	return arrayAccess.getBasicBlock();
    }

    /**
     * Replace current instruction in the basic block
     *
     * @param newInstruction the instruction
     */
    public void replaceBy(QInst newInstruction) {
	arrayAccess.replaceCurrentInstruction(newInstruction);
    }

    /**
     * Add an instruction after the current in the basic block
     *
     * @param instruction the instruction to add
     */
    public void insertAfter(QInst instruction) {
	arrayAccess.insertAfter(instruction);
    }


    /**
     * Remove current instruction from the basic block
     */
    public void remove() {
	arrayAccess.replaceCurrentInstruction(new QNop());
    }

    /**
     * Remove current instruction from the basic block
     */
    public void removeSSAInstruction() {
	arrayAccess.removeSSAInstruction();
    }

    /**
     * Test if this instruction is attached to a basic block
     */
    public boolean isAttached() {
	return arrayAccess != null;
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this quadruple instruction
     *
     * @param codeGen the code generator
     */
    public abstract void generateInstructions(CodeGenerator codeGen);

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected QInstArrayAccessor arrayAccess;
}
