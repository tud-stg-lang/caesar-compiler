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
 * $Id: QPutField.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.FieldRefConstant;
import org.caesarj.classfile.FieldRefInstruction;
/**
 * A class to represent a put in a field (static or not).
 */
public class QPutField extends QCallVoid {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a put static field
     *
     * @param field the field
     * @param value the value to store in.
     * @param opcode the opcode source
     */
    public QPutField(FieldRefConstant field, QOperand value,
		     int opcode) {
	this(field,null, value, opcode);
    }

    /**
     * Construct the instruction
     *
     * @param field the field
     * @param ref the object reference
     *            null for a static field
     * @param value the value to store in.
     * @param opcode the opcode source
     */
    public QPutField(FieldRefConstant field, QOperand ref,
		     QOperand value,
		     int opcode) {
	this.field = field;
	this.ref = new QOperandBox(ref, this);
	this.operand = new QOperandBox(value, this);
	this.opcode = opcode;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {ref, operand};
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this quadruple instruction
     *
     * @param codeGen the code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	if (ref.getOperand() != null) {
	    ref.getOperand().generateInstructions(codeGen);
	}
	operand.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new FieldRefInstruction(opcode, field));
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return ref + "." + field.getName() +  " = " + operand;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox ref;
    protected FieldRefConstant field;
    protected QOperandBox operand;
    protected int opcode;
}
