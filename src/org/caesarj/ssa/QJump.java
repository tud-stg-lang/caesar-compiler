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
 * $Id: QJump.java,v 1.2 2003-10-29 12:29:11 kloppenburg Exp $
 */

package org.caesarj.ssa;

import org.caesarj.classfile.Constants;
import org.caesarj.classfile.JumpInstruction;

/**
 * A class to represent an inconditional jump
 */
public class QJump extends QAbstractJumpInst {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a jump instruction
     *
     * @param dest the destination of the jump
     */
    public QJump(Edge dest) {
	this.dest = dest;
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
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	return new QOperandBox[0];
    }

    /**
     * Return the destination of the jump
     */
    public Node getDestination() {
	return  dest.getTarget();
    }

    /**
     * Return the edge to the destination of the jump
     */
    public Edge getEdge() {
	return dest;
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	return "goto";
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
	BasicBlock next = codeGen.getCurrentBasicBlock().getNext();
	//if it's a jump to the next block in the generation order
	if (next != null &&
	    dest.getTarget() == next) {
	    return;//don't generate the jump (not necessary).
	} else {
	    simplifyJump(dest);
	}
	codeGen.addInstruction(new JumpInstruction(Constants.opc_goto,
						   new EdgeLabel(dest)));
    }

    /**
     * Simplify all consecutive jumps
     */
    public void simplifyAllJumps() {
	simplifyJump(dest);
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Edge dest;

}
