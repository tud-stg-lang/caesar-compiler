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
 * $Id: QCheckCast.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassConstant;
import org.caesarj.classfile.ClassRefInstruction;
import org.caesarj.classfile.ClassfileConstants2;
/**
 * A class to represent a check cast.
 */
public class QCheckCast extends QCallReturn {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the instruction
     *
     * @param className the name of the class to cast in.
     * @param op the reference to an object.
     */
    public QCheckCast(ClassConstant className, QOperand op) {
	this.className = className;
	this.operand = new QOperandBox(op, this);
    }



    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Return the type of the expression
     */
    public byte getType() {
	return ClassfileConstants2.TYP_REFERENCE;
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
	operand.getOperand().generateInstructions(codeGen);
	codeGen.addInstruction(new ClassRefInstruction(ClassfileConstants2.opc_checkcast,
						       className));
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[] {operand};
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "(" + className.getName() + ") " + operand;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected ClassConstant className;
    protected QOperandBox operand;
}
