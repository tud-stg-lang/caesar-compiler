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
 * $Id: CodeGeneratorMethod.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.Vector;

import org.caesarj.classfile.Instruction;

/**
 * To generate classfile instructions of a method
 */
public class CodeGeneratorMethod extends CodeGenerator {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the code generator
     */
    public CodeGeneratorMethod() {
	this.insts = new Vector();
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Return the current index in the code generation
     * (To refind an instruction added)
     *
     * @return the current index in the code generation
     */
    public int currentIndex() {
	return insts.size();
    }

    /**
     * Add an new instruction
     *
     * @param inst the instruction to add.
     */
    public void addInstruction(Instruction inst) {
	insts.addElement(inst);
    }

    /**
     * Return an array of generated instructions
     *
     * @return the generated instructions.
     */
    public Instruction[] getInstructions() {
	Instruction[] instructions = new Instruction[insts.size()];
	insts.toArray(instructions);
	return instructions;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Vector insts;
}

