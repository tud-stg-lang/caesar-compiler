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
 * $Id: QANewArray.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassConstant;
import org.caesarj.classfile.ClassRefInstruction;
import org.caesarj.classfile.ClassfileConstants2;
/**
 * A class to represent instruction new
 */
public class QANewArray extends QCallReturn {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the instruction
     *
     * @param className class name of the object in the array
     * @param size the array size
     */
    public QANewArray(ClassConstant className, QOperand size) {
	this.className = className;
	this.size = new QOperandBox(size, this);
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the instruction has side effects.
     */
    public boolean hasSideEffects() {
	return true;
    }

    /**
     * Return the type of the expression
     */
    public byte getType() {
	return ClassfileConstants2.TYP_REFERENCE;
    }
    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {size };
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "new " + className.getName() + " [" + size + "]";
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
	size.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new ClassRefInstruction(ClassfileConstants2.opc_anewarray,
						       className));
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected ClassConstant className;
    protected QOperandBox size;
}
