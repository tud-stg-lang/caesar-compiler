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
 * $Id: QPhiJoin.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
/**
 * The usual phi function.
 *
 * We keep the origin of each operand with the edge in the control
 * flow graph.
 */
public class QPhiJoin extends QPhi {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param var variable used in phi in non SSA form
     * @param bb the basic block
     */
    public QPhiJoin(QVar var, BasicBlock bb) {
	super(var);
	operands = new HashMap();
	Iterator it = bb.getPredecessors();
	//operands are created when request are done by getOperand
	// so no unuse operand are created.
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	Collection collectionOperands = operands.values();
	QOperandBox[] ops = new QOperandBox[collectionOperands.size()];
	collectionOperands.toArray(ops);
	return ops;
    }

    /**
     * Remove a given operand from the list of operands
     */
    public void removeOperand(QOperandBox op) {
	operands.values().remove(op);
    }

    /**
     * Get the operand associated with a given basic block
     *
     * Precondition : bb is a predecessor of the block in
     *   which the phi function is.
     *
     * @param bb the basic block
     */
    public QOperandBox getOperandForBlock(BasicBlock bb) {
	QOperandBox op = (QOperandBox) operands.get(bb);
	if (op == null) {
	    //create the operand associated with this predecessor
	    op = new QOperandBox(null, this);
	    operands.put(bb, op);
	}
	return op;
    }

    /**
     * Test if an operand is associated with a given basic block
     *
     * @param bb the basic block
     */
    public boolean hasOperandForBlock(BasicBlock bb) {
	return operands.get(bb) != null;
    }


    /**
     * A representation of the instruction
     */
    public String toString() {
	String tmp = variableDefined + " = phi(";
	Iterator it = operands.values().iterator();
	while (it.hasNext()) {
	    QOperandBox op = (QOperandBox) it.next();
	    tmp += op;
	    if (it.hasNext()) {
		tmp += ", ";
	    }
	}
	tmp += ")";
	return tmp;
    }
    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Map operands; //mapping between incoming basic block and phi operand
}
