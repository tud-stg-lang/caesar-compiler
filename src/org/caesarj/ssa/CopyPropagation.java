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
 * $Id: CopyPropagation.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Iterator;

/**
 * To perform copy propagations
 * in a control flow graph in SSA form
 */
public class CopyPropagation {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor
     *
     * @param nodes the basic blocks with instruction in SSA form
     */
    public CopyPropagation(Node[] nodes) {
	this.nodes = nodes;
    }

    // -------------------------------------------------------------------
    // OPTIMIZATIONS
    // -------------------------------------------------------------------
    /**
     * Perform copy propagation on all copy instructions
     */
    public void propagate() {
	for (int i = 0; i < nodes.length; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];

	    Iterator phis = bb.getPhis();
	    while (phis.hasNext()) {
		QInst inst = (QInst) phis.next();
		QOperandBox[] uses = inst.getUses();
		if (uses.length == 1) {
		    if (inst.getDefined().getOperand() instanceof QSSAVar &&
			uses[0].getOperand() instanceof QSSAVar) {
			replaceUses(((QSSAVar)inst.getDefined().getOperand()).getSSAVar(),
				    ((QSSAVar)uses[0].getOperand()).getSSAVar());
			if (ControlFlowGraph.DEBUG) {
			    System.out.println("Copy Propagation : " + inst);
			}
			inst.removeSSAInstruction();
		    }
		}
	    }

	    Iterator insts = bb.getInstructions();
	    while (insts.hasNext()) {
		QInst inst = (QInst) insts.next();
		if (inst instanceof QAssignment) {
		    QOperand target = inst.getDefined().getOperand();
		    QExpression expr = ((QAssignment) inst).getExpression();
		    if (expr instanceof QSimpleExpression) {
			QOperand source = expr.getUses()[0].getOperand();
			if (target instanceof QSSAVar &&
			    source instanceof QSSAVar) {
			    replaceUses(((QSSAVar)target).getSSAVar(),
					((QSSAVar)source).getSSAVar());
			    if (ControlFlowGraph.DEBUG) {
				System.out.println("Copy Propagation : " + inst);
			    }
			    inst.removeSSAInstruction();
			}
		    }

		}
	    }
	}
    }
    // -------------------------------------------------------------------
    // PROTECTED METHODS
    // -------------------------------------------------------------------
    /**
     *
     */
    protected void replaceUses(SSAVar from, SSAVar to) {
	Iterator uses = from.getUses();

	while (uses.hasNext()) {
	    QOperandBox use = (QOperandBox) uses.next();
	    QSSAVar.newSSAVarUse(use, to, to.getType());
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Node[] nodes;
}
