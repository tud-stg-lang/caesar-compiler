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
 * $Id: SSADestructor.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.ssa;

import java.util.BitSet;
import java.util.Iterator;

public class SSADestructor {

    public static void removeSSAForm(Graph cfg, Node start,
				     ExceptionHandler[] exceptionHandlers,
				     final ColorComputer colorer) {

	//remove phi catch functions and add affectations
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    ExceptionHandler exception = exceptionHandlers[i];
	    Iterator allPhis = exception.getHandlerBlock().getPhis();
	    while (allPhis.hasNext()) {
		QPhi phi = (QPhi) allPhis.next();
		if (phi instanceof QPhiCatch) {
		    QPhiCatch phiCatch = (QPhiCatch) phi;
		    SSAVar destVar = ((QSSAVar)phiCatch.getTarget()).getSSAVar();

		    //to know if we ever add an affectation for a given
		    // operand
		    BitSet ops = new BitSet();
		    QOperandBox[] operands = phiCatch.getUses();
		    for (int j = 0; j < operands.length; ++j) {
			QOperandBox op = operands[j];
			if (op.getOperand() instanceof QSSAVar) {
			    SSAVar sourceVar = ((QSSAVar) op.getOperand()).getSSAVar();
			    //if the affectation was not generated and
			    // destination and source has a different color
			    if (!ops.get(sourceVar.getUniqueIndex()) &&
				destVar.getColor() != sourceVar.getColor()) {
				ops.set(sourceVar.getUniqueIndex());
				QVar target = colorer.getVariable(destVar);
				QVar source = colorer.getVariable(sourceVar);
				if (sourceVar.getDefinition().getInstruction() instanceof QPhi) {
				    BasicBlock bb = sourceVar.getDefinition().getInstruction().getBasicBlock();
				    bb.getInstructionsArray().insertAfter(-1, new QAssignment(target, new QSimpleExpression(source)));
				} else {
				    sourceVar.getDefinition().getInstruction().insertAfter(new QAssignment(target, new QSimpleExpression(source)));
				}
			    }
			}
		    }
		}
	    }
	}


	//change SSA Variables to QVar
	//remove all phi join functions.
	cfg.visitGraph(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    BasicBlock bb = (BasicBlock) n;
		    Iterator it = bb.getInstructions();
		    while (it.hasNext()) {
			QInst inst = (QInst) it.next();
			if (inst.defVar() && inst.getDefined().getOperand() instanceof QSSAVar) {
			    SSAVar v = ((QSSAVar) inst.getDefined().getOperand()).getSSAVar();
			    inst.getDefined().setOperand(colorer.getVariable(v));
			}
			QOperandBox[] ops = inst.getUses();
			for (int i = 0; i < ops.length; ++i) {
			    if (ops[i].getOperand() instanceof QSSAVar) {
				SSAVar v = ((QSSAVar) ops[i].getOperand()).getSSAVar();
				ops[i].setOperand(colorer.getVariable(v));
			    }
			}
		    }
		    QInst lastInst = bb.removeLastInstruction();
		    //add affectation from phi function in next blocks
		    Iterator succs = bb.getSuccessors();
		    while (succs.hasNext()) {
			BasicBlock succ = (BasicBlock) succs.next();
			//iterates on phi join instructions
			Iterator allPhis = succ.getPhis();
			while (allPhis.hasNext()) {
			    QPhi phi = (QPhi) allPhis.next();
			    if (phi instanceof QPhiJoin) {
				QPhiJoin phiJoin = (QPhiJoin) phi;
				if (phiJoin.hasOperandForBlock(bb)) {
				    QOperand operandSource = phiJoin.getOperandForBlock(bb).getOperand();
				    QOperand operandTarget = phiJoin.getTarget();
				    if (operandTarget instanceof QSSAVar &&
					operandSource instanceof QSSAVar) {
					SSAVar varTarget = ((QSSAVar) operandTarget).getSSAVar();
					SSAVar varSource = ((QSSAVar) operandSource).getSSAVar();
					//do not had the affectation if the source equals the target
					if (varTarget.getColor() != varSource.getColor()) {
					    QVar target = colorer.getVariable(varTarget);
					    QVar source = colorer.getVariable(varSource);
					    bb.addInstruction(new QAssignment(target, new QSimpleExpression(source)));
					}
				    }
				}
			    }
			}
		    }
		    bb.addInstruction(lastInst);

		    return true;
		}
	    });

    }
}
