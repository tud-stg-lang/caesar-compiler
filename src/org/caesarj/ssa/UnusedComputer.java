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
 * $Id: UnusedComputer.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * To remove unuse variable and instruction
 * in a control flow graph in SSA form
 */
public class UnusedComputer {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Constructor
     *
     * @param nodes the basic blocks with instruction in SSA form
     */
    public UnusedComputer(Node[] nodes) {
	this.nodes = nodes;
    }

    // -------------------------------------------------------------------
    // OPTIMIZATIONS
    // -------------------------------------------------------------------
    /**
     * Remove unused variables in the control flow graph
     */
    public void removeUnusedVariables() {
	LinkedList workList = new LinkedList();
	/*
	 * search unused variable as target of any instruction
	 * if the instruction is removed add to the work list
	 * all instruction that might be unusefull
	 */
	for (int i = 0; i < nodes.length; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];

	    Iterator phis = bb.getPhis();
	    while (phis.hasNext()) {
		QInst inst = (QInst) phis.next();
		tryRemoveInstruction(inst, workList);
	    }

	    QInstArray insts = bb.getInstructionsArray();
	    for (int j = insts.size() - 1; j >= 0; --j) {
		QInst inst = insts.getInstructionAt(j);
		tryRemoveInstruction(inst, workList);
	    }
	}
	/*
	 * Search unused variable in the work list
	 */
	while (!workList.isEmpty()) {
	    QInst inst = (QInst) workList.removeFirst();
	    if (inst.isAttached()) {
		tryRemoveInstruction(inst, workList);
	    }
	}

    }

    /**
     * Remove all unusefull phi function.
     * A phi function is unusefull if all use of the variable
     * defined by the function are used in phi catch function.
     * If the phi is removed all operands of the phi are added
     * to previous phi catchs.
     */
    public void removeUnusefullPhis() {
	LinkedList workList = new LinkedList();
	//search phis in all basic blocks
	for (int i = 0; i < nodes.length; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];
	    Iterator phis = bb.getPhis();
	    while (phis.hasNext()) {
		QPhi inst = (QPhi) phis.next();
		tryRemovePhi(inst, workList);
	    }
	}
	//with the work list
	while (!workList.isEmpty()) {
	    QPhi phi = (QPhi) workList.removeFirst();
	    if (phi.isAttached()) {
		tryRemovePhi(phi, workList);
	    }
	}
    }

    // -------------------------------------------------------------------
    // PROTECTED METHODS
    // -------------------------------------------------------------------
    /**
     * Test if an instruction can be removed.
     * An instruction can be removed if it defines an unused variable
     * and has no side effects.
     * Remove this instruction and add to the work list all instructions
     * that define a variable used in this instruction.
     *
     * @param inst the instruction
     * @param workList the work list
     */
    protected void tryRemoveInstruction(QInst inst, LinkedList workList) {
	if (inst.defVar() && inst.getDefined().getOperand() instanceof QSSAVar) {
	    SSAVar var = ((QSSAVar)inst.getDefined().getOperand()).getSSAVar();
	    if (!var.isUsed()) {
		if (!inst.hasSideEffects()) {
		    if (ControlFlowGraph.DEBUG) {
			System.out.println("Unused : " + inst);
		    }
		    //add the definition point of the operands in the work list
		    QOperandBox[] operands = inst.getUses();
		    for (int ops = 0; ops <operands.length; ++ops) {
			QOperandBox op = operands[ops];
			if (op.getOperand() instanceof QSSAVar) {
			    workList.add(((QSSAVar)op.getOperand()).getSSAVar().getDefinition().getInstruction());
			}
		    }
		    //remove the instruction.
		    inst.removeSSAInstruction();
		}
	    }
	}
    }

    /**
     * Try to remove a unusefull phi function.
     * Add all phi functions that define a variable, used in the removed phi,
     * in the work list
     *
     * @param phi the phi function
     * @param workList the work list
     */
    protected void tryRemovePhi(QPhi phi, LinkedList workList) {
	if (phi.getDefined().getOperand() instanceof QSSAVar) {
	    SSAVar var = ((QSSAVar)phi.getDefined().getOperand()).getSSAVar();
	    /*
	     * Test if all uses of the phi target are in phi catch
	     */
	    Iterator uses = var.getUses();
	    // keep all uses before modification in the set
	    // not to have concurent modifications
	    Set currentUses = new HashSet();
	    while (uses.hasNext()) {
		QOperandBox use = (QOperandBox)uses.next();
		if (! (use.getInstruction() instanceof QPhiCatch)) {
		    return;
		}
		currentUses.add(use);
	    }

	    /*
	     * Add all phi operands to the phi catchs which use
	     * the phi target
	     */
	    QOperandBox[] operands = phi.getUses();
	    for (int ops = 0; ops <operands.length; ++ops) {
		QOperandBox op = operands[ops];
		SSAVar ssaOp = ((QSSAVar)op.getOperand()).getSSAVar();
		uses = currentUses.iterator();
		//add this operand to all phi catch
		while (uses.hasNext()) {
		    QPhiCatch phiCatch = (QPhiCatch)((QOperandBox)uses.next()).getInstruction();
		    if (!phiCatch.hasSSAVarAsOperand(ssaOp)) {
			QSSAVar.newSSAVarUse(phiCatch.addNewOperand(), ssaOp, ssaOp.getType());
		    }
		}
		//add the definition point of the operand in the work list
		// in fact this is only used if phi is a QPhiJoin
		if (ssaOp.getDefinition().getInstruction() instanceof QPhi) {
		    workList.add(ssaOp.getDefinition().getInstruction());
		}
	    }

	    /*
	     * Remove the phi target from the phi catchs which use it.
	     */
	    uses = currentUses.iterator();
	    while (uses.hasNext()) {
		QOperandBox use = ((QOperandBox)uses.next());
		QPhiCatch phiCatch = (QPhiCatch)use.getInstruction();
		phiCatch.removeOperand(use);
	    }
	    if (ControlFlowGraph.DEBUG) {
		System.out.println("Unused phi : " + " "+ phi);
	    }
	    /*
	     * Remove the phi function
	     */
	    phi.removeSSAInstruction();
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Node[] nodes;
}
