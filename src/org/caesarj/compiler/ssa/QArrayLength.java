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
 * $Id: QArrayLength.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.classfile.NoArgInstruction;

/**
 * A class to represent an access to the length of an array
 */
public class QArrayLength extends QCallReturn {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the instruction
     *
     * @param array the reference to the array
     */
    public QArrayLength(QOperand array) {
	this.array = new QOperandBox(array, this);
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Return the type of the expression
     */
    public byte getType() {
	return ClassfileConstants2.TYP_INT;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {array};
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return array + ".length";
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
	array.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new NoArgInstruction(ClassfileConstants2.opc_arraylength));
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected QOperandBox array;
}
