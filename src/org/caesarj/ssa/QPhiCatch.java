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
 * $Id: QPhiCatch.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A PhiCatch is a special phi function used in catch block.
 *
 * The operands of the phi function are all variables defined
 * in the protected blocks.
 */
public class QPhiCatch extends QPhi {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param var variable used in phi in non SSA form
     */
    public QPhiCatch(QVar var) {
	super(var);
	operands = new ArrayList();
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------

    /**
     * Add a new null operand at the phi catch
     * The operand must be then initialized.
     */
    public QOperandBox addNewOperand() {
	QOperandBox newOp = new QOperandBox(null, this);
	operands.add(newOp);
	return newOp;
    }

    /**
     * Test if one of the operand is a use of a given ssa variable
     */
    public boolean hasSSAVarAsOperand(SSAVar v) {
	Iterator ops = operands.iterator();
	while (ops.hasNext()) {
	    QOperandBox op = (QOperandBox) ops.next();
	    if (op.getOperand() instanceof QSSAVar &&
		((QSSAVar)op.getOperand()).getSSAVar() == v) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Get the operands of the instruction
     */
    public QOperandBox[] getUses() {
	QOperandBox[] ops = new QOperandBox[operands.size()];
	operands.toArray(ops);
	return ops;
    }

    /**
     * Remove a given operand from the list of operands
     */
    public void removeOperand(QOperandBox op) {
	operands.remove(op);
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	String tmp = variableDefined + " = phiCatch(";
	Iterator it = operands.iterator();
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
    protected ArrayList operands; //all operands in SSA form
}
