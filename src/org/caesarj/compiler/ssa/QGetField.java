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
 * $Id: QGetField.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.FieldRefConstant;
import org.caesarj.classfile.FieldRefInstruction;

/**
 * A class to represent an access to a field (static or not).
 */
public class QGetField extends QCallReturn {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the instruction
     *
     * @param field the field
     * @param ref the object reference
     *            null for a static field
     * @param type type of the field.
     * @param opcode the opcode source
     */
    public QGetField(FieldRefConstant field, QOperand ref,
		     byte type, int opcode) {
	this.field = field;
	this.ref = new QOperandBox(ref, this);
	this.type = type;
	this.opcode = opcode;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
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
	return new QOperandBox[] {ref };
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	if (ref == null || ref.getOperand() == null)
	    return field.getName();
	return ref + "." + field.getName();
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
	if (ref.getOperand() != null) {
	    ref.getOperand().generateInstructions(codeGen);
	}
	codeGen.addInstruction(new FieldRefInstruction(opcode, field));
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected FieldRefConstant field;
    protected QOperandBox ref;
    protected byte type;
    protected int opcode;
}
