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
 * $Id: QPhi.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.util.InconsistencyException;

/**
 * An abstract phi function
 *
 * Used in SSA Form at the merge point of control flow graph.
 */
public abstract class QPhi extends QInst {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param varDef variable defined
     */
    public QPhi(QOperand varDef) {
	variableDefined = new QOperandBox(varDef, this, true);
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the current instruction may throw an exception.
     */
    public boolean mayThrowException() {
	return false;
    }

    /**
     * Test if the instruction define a local variable.
     */
    public boolean defVar() {
	return true;
    }

    /**
     * Get the operand defined if the instruction define one
     */
    public QOperandBox getDefined() {
	return variableDefined;
    }

    /**
     * Get the target of the phi
     */
    public QOperand getTarget() {
	return variableDefined.getOperand();
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
     * Remove current instruction from the basic block
     */
    public void remove() {
	arrayAccess.replaceCurrentInstruction(new QPhiNop());
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
	throw new InconsistencyException("Can't generate a phi function");
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected QOperandBox variableDefined;
}
