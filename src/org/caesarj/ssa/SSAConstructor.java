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
 * $Id: SSAConstructor.java,v 1.2 2003-08-28 16:12:05 ostermann Exp $
 */

package org.caesarj.ssa;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.caesarj.classfile.Constants;
/**
 * Transform all the variables of a control flow graph of a method
 * in SSA form.
 *
 * Use the algorithm of "Pratical Improvements to the Construction and
 * Destruction of Static Single Assignment Form" From Briggs, Cooper,
 * Harvey, Simpson.
 *
 * Use phi catch and phi return used by Nystrom
 */
public class SSAConstructor {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the ssa constructor.
     *
     * @param cfg the control flow graph of a method in 3-adress code
     * @param start the first block of the method
     * @param end the end block of the control flow graph
     * @param nonLocals set of non local variables
     * @param exceptionHandlers exception handlers for the method
     * @param subroutines all subroutines
     */
    public SSAConstructor(Graph cfg, BasicBlock start, BasicBlock end,
			  BitSet nonLocals,
			  ExceptionHandler[] exceptionHandlers,
			  Collection subroutines) {
	this.cfg = cfg;
	this.start = start;
	this.end = end;
	cfg.setNodesIndex();
	this.nodes = cfg.getNodes();
	//compute the dominance frontier
	this.dominatorComputer = new DominatorComputer(nodes, start);
	this.varUsed = new BitSet(nonLocals.size());
	this.nonLocals = nonLocals;
	this.exceptionHandlers = exceptionHandlers;
	this.subroutines = subroutines;
	//compute a life analysis
	LivenessComputer liveness = new LivenessComputer(cfg, start, exceptionHandlers);
	ins = new BitSet[nodes.length];
	outs = new BitSet[nodes.length];
	for (int i = 0; i < nodes.length; ++i) {
	    ins[i] = new BitSet();
	    outs[i] = new BitSet();
	}
	liveness.computeNonSSALivenessAnalysis(ins, outs);
    }

    // -------------------------------------------------------------------
    // SSA TRANSFORMATION
    // -------------------------------------------------------------------
    /**
     * Compute the SSA form
     */
    public void constructSSAForm() {
	SSAVar.init();
	findUsedVar();
	//process each variables
	for (int i = 0; i < varUsed.size(); ++i) {
	    if (varUsed.get(i)) {
		//construct SSAConstructorInfo
		SSAConstructorInfo varInfo = new SSAConstructorInfo(this, i);
		findUses(varInfo);

		//place phi functions
		placePhiFunctions(varInfo);

		//renaming
		renameVariable(varInfo);

		//add phi in basic block
		verifyPhiTypes(varInfo);
		insertPhiFunctions(varInfo);
	    }
	}
    }

    /**
     * Find the variables used in the method
     */
    protected void findUsedVar() {
	final BitSet localVarUsed = varUsed;
	cfg.visitGraph(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    Iterator insts = ((BasicBlock) n).getInstructions();
		    while (insts.hasNext()) {
			QInst current = (QInst) insts.next();
			if (current.defVar()) {
			    if (current.getDefined().getOperand() instanceof QVar) {
				varUsed.set(((QVar)current.getDefined().getOperand()).getRegister());
			    }
			}
			QOperandBox[] ops = current.getUses();
			for (int i = 0; i < ops.length; ++i) {
			    if (ops[i].getOperand() instanceof QVar) {
				varUsed.set(((QVar)ops[i].getOperand()).getRegister());
			    }

			}
		    }
		    return true;
		}
	    });
    }

    /**
     * Find all occurence of a given variable
     *
     * @param varInfo info on the variable
     */
    protected void findUses(final SSAConstructorInfo varInfo) {
	final int varIndex = varInfo.getVariableRegister();
	cfg.visitGraph(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    BasicBlock bb = (BasicBlock) n;
		    Iterator insts = bb.getInstructions();
		    while (insts.hasNext()) {
			QInst current = (QInst) insts.next();
			QOperandBox[] ops = current.getUses();
			for (int i = 0; i < ops.length; ++i) {
			    QOperandBox use = ops[i];
			    if (use.getOperand() instanceof QVar &&
				((QVar)use.getOperand()).getRegister() == varIndex) {
				varInfo.addUse(bb, use);
			    }
			}
			//definition must be put after use.
			if (current.defVar()) {
			    QOperandBox def = current.getDefined();
			    if (def.getOperand() instanceof QVar &&
				((QVar)def.getOperand()).getRegister() == varIndex) {
				varInfo.addDefinitionBlock(bb);
				varInfo.addUse(bb, def);
			    }
			}
		    }
		    return true;
		}
	    });
    }

    /**
     * Place phi functions
     */
    protected void placePhiFunctions(SSAConstructorInfo varInfo) {
	if (!nonLocals.get(varInfo.getVariableRegister()))
	    return;

	//add a phi catch in each catch block
	// if the variable is alive at the beginning of the basic block
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    BasicBlock bb = exceptionHandlers[i].getHandlerBlock();
	    if (ins[bb.getIndex()].get(varInfo.getVariableRegister())) {
		varInfo.addPhiCatch(bb);
		varInfo.addDefinitionBlock(bb);
	    }
	}

	//add a phi return all return points of each subroutines
	Iterator it = subroutines.iterator();

	while (it.hasNext()) {
	    SubRoutine sub = (SubRoutine) it.next();

	    Iterator calls = sub.getCalls();
	    while (calls.hasNext()) {
		Edge[] call = (Edge[]) calls.next();
		BasicBlock bb = (BasicBlock) call[1].getTarget();
		if (ins[bb.getIndex()].get(varInfo.getVariableRegister())) {
		    varInfo.addPhiReturn(bb, sub);
		    varInfo.addDefinitionBlock(bb);
		}
	    }
	}

	//add phi join add the iterated frontier of all blocks which
	// define the variable
	//Add only the phi function if the variable is alive at the beginning
	// of the basic block.
	Iterator df = dominatorComputer.getDFPlus(varInfo.getDefinitionBlocks()).iterator();
	while (df.hasNext()) {
	    BasicBlock dom = (BasicBlock) nodes[((Integer) df.next()).intValue()];
	    if (dom != end && ins[dom.getIndex()].get(varInfo.getVariableRegister())) {
		varInfo.addPhiJoin(dom);
	    }
	}
    }


    /**
     * Put variables in the ssa form
     *
     * @param varInfo the concerned variable
     */
    protected void renameVariable(SSAConstructorInfo varInfo) {
	search(varInfo, null, dominatorComputer.getTreeRoot());

	//Algorithm From Nystrom.

	// Eliminate PhiReturns by replacing their uses with the defs live
	// at the end of the returning sub or live on the same path on entry
	// to the sub (if the variable did not occur in the subroutine).

	// Examine each QPhiReturn in the CFG.
	boolean changed = true;

	while (changed) {
	    changed = false;

	    Iterator subs = subroutines.iterator();

	    while (subs.hasNext()) {
		SubRoutine sub = (SubRoutine) subs.next();
		Iterator paths = sub.getCalls();

		QPhiJoin entry = (QPhiJoin) varInfo.getPhiAtBlock(sub.getStart());

		if (entry == null) {
		    // If there was no PhiJoinStmt for the variable in the
		    // subroutine, who cares?  We don't.
		    continue;
		}

		while (paths.hasNext()) {
		    Edge[] path = (Edge[]) paths.next();

		    QPhiReturn ret = (QPhiReturn)
			varInfo.getPhiAtBlock((BasicBlock)path[1].getTarget());

		    if (ret != null) {
			SSAVar ssaVar = ((QSSAVar) ret.getOperand().getOperand()).getSSAVar();

			if (ssaVar != ((QSSAVar)entry.getTarget()).getSSAVar()) {
			    // If the operand of the QPhiReturn is different
			    // from the new SSA variable defined by the
			    // QPhiJoin at the beginning of the subroutine,
			    // then the variable was defined in the subroutine,
			    // so the operand to the QPhiReturn is the correct
			    // SSA variable.
			    continue;
			}

			// Replace all uses of the target of the PhiReturnStmt
			// with the SSA variable corresponding to the block in
			// which the jsr occured.
			if (entry.hasOperandForBlock((BasicBlock)path[0].getSource())) {
			    SSAVar ssaVarOriginBlock = ((QSSAVar) entry.getOperandForBlock((BasicBlock)path[0].getSource()).getOperand()).getSSAVar();

			    SSAVar ssaVarPhiReturnTarget = ((QSSAVar) ret.getTarget()).getSSAVar();

			    Iterator uses = ssaVarPhiReturnTarget.getUses();

			    while (uses.hasNext()) {
				QOperandBox use = (QOperandBox) uses.next();
				QSSAVar.newSSAVarUse(use, ssaVarOriginBlock, ssaVarOriginBlock.getType());

			    }
			    ssaVar.removeUse(ret.getOperand());
			    // The QPhiReturn is no longer needed
			    varInfo.removePhiAtBlock((BasicBlock)path[1].getTarget());
			    changed = true;
			} else if (!((QSSAVar) ret.getTarget()).getSSAVar().getUses().hasNext()) {
			    // We can remove the phi return because the
			    // target is never used.
			    ssaVar.removeUse(ret.getOperand());
			    varInfo.removePhiAtBlock((BasicBlock)path[1].getTarget());
			}
		    }
		}
	    }
	}

	Iterator subs = subroutines.iterator();

	// Examine any remaining QPhiReturn.  Replace all uses of the
	// target of the QPhiReturn with its operand.
	while (subs.hasNext()) {
	    SubRoutine sub = (SubRoutine) subs.next();

	    Iterator paths = sub.getCalls();

	    while (paths.hasNext()) {
		Edge[] path = (Edge[]) paths.next();

		QPhiReturn ret = (QPhiReturn)
		    varInfo.getPhiAtBlock((BasicBlock)path[1].getTarget());

		if (ret != null) {
		    if (!(ret.getOperand().getOperand() instanceof QSSAVar)) {
			continue;
		    }
		    SSAVar ssaVar = ((QSSAVar) ret.getOperand().getOperand()).getSSAVar();
		    SSAVar ssaVarPhiReturnTarget = ((QSSAVar) ret.getTarget()).getSSAVar();
		    Iterator uses = ssaVarPhiReturnTarget.getUses();

		    while (uses.hasNext()) {
			QOperandBox use = (QOperandBox) uses.next();
			QSSAVar.newSSAVarUse(use, ssaVar, ssaVar.getType());
		    }

		    ssaVar.removeUse(ret.getOperand());
		    varInfo.removePhiAtBlock((BasicBlock)path[1].getTarget());
		}
	    }
	}

    }

    /**
     * Search
     * algorithm from [BCHS98]
     *
     * @param varInfo the variable
     * @param top the most recent SSA variable
     * @param block the block concerned.
     */
    protected void search(SSAConstructorInfo varInfo, SSAVar top,
			  DominatorTreeNode block) {
	LinkedList catchBlocks = new LinkedList();
	BasicBlock bb = (BasicBlock) block.getNode();
	if (top != null) {
	    findCatchBlocks(bb, catchBlocks);
	    addCatchPhiOperands(varInfo, top, catchBlocks);
	}

	// Search for a phi in the block
	QPhi phi = varInfo.getPhiAtBlock(bb);

	if (phi != null) {
	    QOperandBox varDef = phi.getDefined();
	    QVar sourceVar = (QVar) varDef.getOperand();
	    int count = 0;
	    if (top == null) {
		findCatchBlocks(bb, catchBlocks);
	    } else {
		count = top.getCount() + 1;
	    }
	    if (phi.getUses().length > 0) {
		top = new SSAVar(sourceVar.getRegister(),
				 phi.getUses()[0].getOperand().getType(),
				 count);
	    } else {
		top = new SSAVar(sourceVar.getRegister(), sourceVar.getType(),
				 count);
	    }

	    QSSAVar.newSSAVarDefinition(varDef, top);
	}

	// Examine each occurrence of the variable in the block of
	// interest.  When we encounter a definition of the variable, make
	// that definition to the most recent SSA variable (top).  For
	// each use, make this most recent SSA variable be its defining
	// expression.
	Iterator it = varInfo.getUsesAtBlock(bb);
	while (it.hasNext()) {
	    QOperandBox varUse = (QOperandBox) it.next();
	    if (varUse.isDefinition()) {//definition
		QVar sourceVar = (QVar) varUse.getOperand();
		int count = 0;
		if (top == null) {
		    findCatchBlocks(bb, catchBlocks);
		} else {
		    count = top.getCount() + 1;
		}

		top = new SSAVar(sourceVar.getRegister(), sourceVar.getType(),
				 count);

		QSSAVar.newSSAVarDefinition(varUse, top);

		//add new var in phi catch of catch blocks.
		addCatchPhiOperands(varInfo, top, catchBlocks);
	    } else {//use
		if (top == null)
		    System.out.println("Error2 : " + varInfo.getVariableRegister());

		QSSAVar.newSSAVarUse(varUse, top, varUse.getOperand().getType());
	    }
	}
	catchBlocks = null;

	// For each successor in the control flow graph, if there
	// a phi function, update the operand associated with current block.
	if (top != null) {
	    Iterator successors = bb.getSuccessors();
	    while (successors.hasNext()) {
		BasicBlock succ = (BasicBlock) successors.next();

		QPhi phiSucc = varInfo.getPhiAtBlock(succ);

		if (phiSucc instanceof QPhiJoin) {
		    QSSAVar.newSSAVarUse(((QPhiJoin) phiSucc).getOperandForBlock(bb), top, top.getType());
		} else if (phiSucc instanceof QPhiReturn) {
		    QSSAVar.newSSAVarUse(((QPhiReturn) phiSucc).getOperand(), top, top.getType());
		}

		LinkedList succCatchBlocks = new LinkedList();
		findCatchBlocks(bb, succCatchBlocks);
		addCatchPhiOperands(varInfo, top, succCatchBlocks);
	    }
	}

	//Visit successors in the dominator tree
	Iterator dominatorSuccessors = block.getSuccessors();
	while (dominatorSuccessors.hasNext()) {
	    DominatorTreeNode succ = (DominatorTreeNode) dominatorSuccessors.next();
	    search(varInfo, top, succ);
	}

    }

    /**
     * Find the catch blocks for a given basic block
     *
     * @param bb the basic block
     * @param catchBlocks list to add catch blocks
     */
    protected void findCatchBlocks(BasicBlock bb, LinkedList catchBlocks) {
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    if (exceptionHandlers[i].contains(bb)) {
		catchBlocks.add(exceptionHandlers[i].getHandlerBlock());
	    }
	}
    }

    /**
     * Add phi operands to phi catch in catch blocks
     *
     * @param varInfo the variable
     * @param var phi operand
     * @param type the variable type
     * @param catchBlocks list of catch blocks
     */
    protected void addCatchPhiOperands(SSAConstructorInfo varInfo,
				       SSAVar var,
				       LinkedList catchBlocks) {
	Iterator it = catchBlocks.iterator();
	while (it.hasNext()) {
	    BasicBlock bb = (BasicBlock) it.next();
	    QPhiCatch phiCatch = (QPhiCatch) varInfo.getPhiAtBlock(bb);
	    if (phiCatch != null &&
		(!(phiCatch.getTarget() instanceof QSSAVar) ||
		 ((QSSAVar)phiCatch.getTarget()).getUniqueIndex() != var.getUniqueIndex())) {
		if (!phiCatch.hasSSAVarAsOperand(var)) {
		    QSSAVar.newSSAVarUse(phiCatch.addNewOperand(), var, var.getType());
		}
	    }
	}
    }

    /**
     * Insert phi functions in the basic blocks
     *
     * @param varInfo the variable
     */
    protected void insertPhiFunctions(SSAConstructorInfo varInfo) {
	for (int i = 0; i < nodes.length; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];
	    QPhi phi = varInfo.getPhiAtBlock(bb);
	    if (phi != null) {
		//don't add phi catch with no operand
		if (phi instanceof QPhiCatch &&
		    phi.getUses().length == 0) {
		    //remove uses of the target of the phi
		    // this one can only occur in a phi function
		    SSAVar v = ((QSSAVar) phi.getTarget()).getSSAVar();
		    Iterator uses = v.getUses();
		    while (uses.hasNext()) {
			QOperandBox use = (QOperandBox) uses.next();
			QInst inst = use.getInstruction();
			if (inst instanceof QPhiCatch) {
			    ((QPhiCatch) inst).removeOperand(use);
			} else if (inst instanceof QPhiJoin) {
			    ((QPhiJoin) inst).removeOperand(use);
			}
		    }
		} else {
		    bb.addPhi(phi);
		}
	    }
	}
    }

    /**
     * Verify the type of the target of any phi function
     *
     * @param varInfo the variable
     */
    protected void verifyPhiTypes(SSAConstructorInfo varInfo) {
	boolean changed = true;
	while (changed) {
	    changed = false;
	bb:
	    for (int i = 0; i < nodes.length; ++i) {
		BasicBlock bb = (BasicBlock) nodes[i];
		QPhi phi = varInfo.getPhiAtBlock(bb);
		if (phi != null) {
		    SSAVar var = ((QSSAVar) phi.getTarget()).getSSAVar();
		    if (var.getType() == Constants.TYP_REFERENCE) {
			QOperandBox[] ops = phi.getUses();
			for (int j = 0; j < ops.length; ++j) {
			    byte type = ops[j].getOperand().getType();
			    if (type != Constants.TYP_REFERENCE) {
				var.setType(type);
				changed = true;
				continue bb;
			    }
			}
		    }
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // ACCESSORS
    // -------------------------------------------------------------------
    /**
     * Get the array of basic blocks
     */
     /*package*/ Node[] getBasicBlockArray() {
	 return nodes;
     }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Graph cfg;
    protected BasicBlock start;
    protected BasicBlock end;
    protected DominatorComputer dominatorComputer;
    protected Node[] nodes;     //all nodes of the cfg
    protected BitSet varUsed;   //var used in the method
    protected BitSet nonLocals; //non local variables
    protected Collection subroutines;
    protected ExceptionHandler[] exceptionHandlers;
    protected BitSet[] ins;
    protected BitSet[] outs;
}
