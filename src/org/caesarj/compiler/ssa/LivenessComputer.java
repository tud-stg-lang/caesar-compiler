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
 * $Id: LivenessComputer.java,v 1.1 2004-02-08 16:47:49 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Compute the interference graph for a control flow graph in SSA form
 */
public class LivenessComputer {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the liveness computer
     *
     * @param cfg the control flow graph
     */
    public LivenessComputer(Graph cfg, BasicBlock start,
			    ExceptionHandler[] exceptionHandlers) {
	this.graph = cfg;
	this.start = start;
	this.nodes = graph.getNodes();
	this.exceptionHandlers = exceptionHandlers;
	this.topologicalOrder = new int[nodes.length];
	topologicalSort(nodes, start);
    }

    // -------------------------------------------------------------------
    // INTERFERENCE GRAPH COMPUTER
    // -------------------------------------------------------------------
    /**
     * Compute the interference graph
     *
     * Can be improved by removing allocation of new BitSet by clone().
     */
    public InterferenceGraph computeInterferenceGraph() {
	int nodesNumber = nodes.length;
	BitSet[] ins = new BitSet[nodesNumber];
	BitSet[] outs = new BitSet[nodesNumber];
	for (int i = 0; i < nodesNumber; ++i) {
	    ins[i] = new BitSet();
	    outs[i] = new BitSet();
	}
	computeSSALivenessAnalysis(ins, outs);

	InterferenceGraph interference = new InterferenceGraph(SSAVar.getSSAVariableNumber());

	BitSet tmp = new BitSet();

	//iterates on all basic blocks
	for (int block = 0; block < nodesNumber; ++block) {
	    BasicBlock bb = (BasicBlock) nodes[block];
	    BitSet in;
	    BitSet out = outs[block];
	    Vector phis = new Vector();
	    addPhiCatch(bb, phis);

	    // Compute in and out sets for each instruction.
	    // And use this information to compute the interference graph.
	    /*
	     * If the instruction b is after instruction a :
	     *  in[a] = use[a] union (out[a] - def[a])
	     *  out[a] = in[b]
	     */

	    //to begin
	    in = out;

	    /**
	     * We simulate the addition of copy that will be inserted
	     * when the phi functions will be removed
	     */
	    //look at phi join in the successors
	    Iterator succs = bb.getSuccessors();
	    while (succs.hasNext()) {
		BasicBlock succ = (BasicBlock) succs.next();
		//iterates on phi join instructions
		QInstArray allPhis = succ.getPhisArray();
		for (int i = allPhis.size() - 1; i >= 0; --i) {

		    QPhi phi = (QPhi) allPhis.getInstructionAt(i);
		    if (phi instanceof QPhiJoin) {
			QPhiJoin phiJoin = (QPhiJoin) phi;
			if (phiJoin.hasOperandForBlock(bb)) {
			    /**
			     * We concider that there is an affectation
			     *  'phi catch target' = 'var associated with bb'
			     * at the end of the bb basic block.
			     */
			    QOperand operandSource = phiJoin.getOperandForBlock(bb).getOperand();
			    QOperand operandTarget = phiJoin.getTarget();
			    if (operandTarget instanceof QSSAVar &&
				operandSource instanceof QSSAVar) {
				int x = ((QSSAVar)operandTarget).getUniqueIndex();
				int z = ((QSSAVar)operandSource).getUniqueIndex();
				//compute out and in sets
				out = in; //out[a] = in[b]

				//in[a] = use[a] union (out[a] - def[a])
				in = (BitSet) out.clone();
				in.clear(x); //out[a] - def[a]
				in.set(z); //add uses in 'in'

				//compute the interference
				// x interfere with out\{z}
				boolean zRemoved = false;
				if (out.get(z)) {
				    zRemoved = true;
				    out.clear(z);
				}

				interference.addInterference(x, out);
				if (zRemoved) {
				    out.set(z);
				}
			    }
			}
		    }
		}
	    }


	    QInstArray insts = bb.getInstructionsArray();
	    for (int i = insts.size() - 1; i >= 0; --i) {
		QInst inst = insts.getInstructionAt(i);

		//compute out and in sets
		out = in; //out[a] = in[b]

		//in[a] = use[a] union (out[a] - def[a])
		in = (BitSet) out.clone();
		if (inst.defVar() && inst.getDefined().getOperand() instanceof QSSAVar) {
		    //out[a] - def[a].
		    in.clear(((QSSAVar)inst.getDefined().getOperand()).getUniqueIndex());
		}
		QOperandBox[] operands = inst.getUses();
		for (int u = 0; u < operands.length; ++u) {
		    if (operands[u].getOperand() instanceof QSSAVar) {
			//add all uses in 'in'
			in.set(((QSSAVar)operands[u].getOperand()).getUniqueIndex());
		    }
		}
		//end computing out and in sets

		//compute the interference
		/*
		 * if the instruction define x :
		 *   if the instruction is x := z, then x interfere with out\{z}
		 *   else x interfere with out.
		 */
		boolean copy = false;
		int z = 0;
		int x = 0;
		if (inst.defVar() && inst.getDefined().getOperand() instanceof QSSAVar) {
		    x = ((QSSAVar)inst.getDefined().getOperand()).getUniqueIndex();

		    // if the instruction is x := z
		    if (inst instanceof QAssignment &&
			((QAssignment) inst).getExpression() instanceof QSimpleExpression &&
			inst.getUses()[0].getOperand() instanceof QSSAVar) {
			z = ((QSSAVar)inst.getUses()[0].getOperand()).getUniqueIndex();
			if (out.get(z)) {
			    //remove temporarely z from out set.
			    copy = true;
			    out.clear(z);
			}
		    }
		    //x interfere with out
		    interference.addInterference(x, out);

		    //add z if it were removed from out set
		    if (copy) {
			out.set(z);
		    }

		    /*
		     * if the instruction is in a protected basic block,
		     *   the defined variable interferes with all target
		     *   of a phi catch if the variable is not an operand
		     *   of this phi catch
		     */
		    Iterator phiCatchs = phis.iterator();
		phis:
		    while (phiCatchs.hasNext()) {
			QPhiCatch phiCatch = (QPhiCatch) phiCatchs.next();
			if (phiCatch.getTarget() instanceof QSSAVar) {
			    QSSAVar target = (QSSAVar) phiCatch.getTarget();
			    operands = phiCatch.getUses();
			    for (int j = 0; j < operands.length; ++j) {
				if (operands[j].getOperand() instanceof QSSAVar &&
				    ((QSSAVar) operands[j].getOperand()).getUniqueIndex() == x) {
				    continue phis;
				}
			    }
			    //x is not an operand of the phi catch
			    interference.addInterference(x, target.getUniqueIndex());
			}
		    }

		}
	    }

	    //I concider that phi catch functions have the same out set
	    out = in;
	    Iterator allPhis = bb.getPhis();
	    while (allPhis.hasNext()) {
		QPhi phi = (QPhi) allPhis.next();
		if (phi instanceof QPhiCatch) {
		    if (phi.getTarget() instanceof QSSAVar) {
			int x = ((QSSAVar)phi.getTarget()).getUniqueIndex();
			interference.addInterference(x, out);

			//same as non-phi instructions
			Iterator phiCatchs = phis.iterator();
		    phis:
			while (phiCatchs.hasNext()) {
			    QPhiCatch phiCatch = (QPhiCatch) phiCatchs.next();
			    if (phiCatch.getTarget() instanceof QSSAVar) {
				QSSAVar target = (QSSAVar) phiCatch.getTarget();
				QOperandBox[] operands = phiCatch.getUses();
				for (int j = 0; j < operands.length; ++j) {
				    if (operands[j].getOperand() instanceof QSSAVar &&
					((QSSAVar) operands[j].getOperand()).getUniqueIndex() == x) {
					continue phis;
				    }
				}
				//x is not an operand of the phi catch
				interference.addInterference(x, target.getUniqueIndex());
			    }
			}
		    }
		}
	    }
	}

	/*
	 * I make the target of a phi catch interfere with all
	 * interferences of all the variable operand of the phi catch
	 */
	//search all phi catch functions.
	Vector phiCatchs = new Vector();
	for (int block = 0; block < nodesNumber; ++block) {
	    BasicBlock bb = (BasicBlock) nodes[block];
	    addPhiCatchFrom(bb, phiCatchs);
	}
	//add the interference
	Iterator phiCatchsIterator = phiCatchs.iterator();
	while (phiCatchsIterator.hasNext()) {
	    QPhiCatch phiCatch = (QPhiCatch) phiCatchsIterator.next();
	    if (phiCatch.getTarget() instanceof QSSAVar) {
		int x = ((QSSAVar) phiCatch.getTarget()).getUniqueIndex();
		QOperandBox[] operands = phiCatch.getUses();
		for (int j = 0; j < operands.length; ++j) {
		    if (operands[j].getOperand() instanceof QSSAVar) {
			int y = ((QSSAVar) operands[j].getOperand()).getUniqueIndex();
			Iterator interfs = interference.interfereFor(y);
			while (interfs.hasNext()) {
			    int interf = ((Integer)interfs.next()).intValue();
			    if (interf != y) {
				interference.addInterference(x, interf);
			    }
			}
		    }
		}
	    }
	}

	if (ControlFlowGraph.DEBUG) {
	    System.out.println(interference);
	}
	return interference;
    }

    // -------------------------------------------------------------------
    // LIVENESS ANALYSIS
    // -------------------------------------------------------------------
    /**
     * Compute a liveness analysis on the basic blocks
     * Fill ins and outs.
     *
     * @param ins variables lives at the entrance of each basic blocks
     * @param outs variables lives at the end of each basic blocks
     */
    protected void computeSSALivenessAnalysis(BitSet[] ins, BitSet[] outs) {
	int nodesNumber = nodes.length;
	//init def and use sets
	//be carefull index in these arrays are index of basic blocks in
	// 'nodes' array and not in 'topologicalOrder'
	BitSet[] defs = new BitSet[nodesNumber];
	BitSet[] uses = new BitSet[nodesNumber];
	for (int i = 0; i < nodesNumber; ++i) {
	    defs[i] = new BitSet();
	    uses[i] = new BitSet();
	}

	//compute def and use sets for each basic block
	for (int i = 0; i < nodesNumber; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];
	    BitSet def = defs[i];
	    BitSet use = uses[i];

	    /*
	     * if the basic block as n instruction i1, i2, ... in
	     * then def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
	     *      and  use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
	     */

	    //iterates on phi catch instructions
	    BitSet temp = new BitSet();
	    Iterator phis = bb.getPhis();
	    while (phis.hasNext()) {
		QPhi phi = (QPhi) phis.next();
		if (phi instanceof QPhiCatch) {

		    //compute temp = (use[ij] - def[i1i2..i(j-1)]).
		    temp.clear();
		    QOperandBox[] operands = phi.getUses();
		    for (int u = 0; u < operands.length; ++u) {
			if (operands[u].getOperand() instanceof QSSAVar) {
			    int ssaVarIndex = ((QSSAVar)operands[u].getOperand()).getUniqueIndex();
			    if (!def.get(ssaVarIndex)) {
				temp.set(ssaVarIndex);
			    }
			}
		    }

		    //use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
		    use.or(temp);

		    if (phi.getTarget() instanceof QSSAVar) {
			//def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
			def.set(((QSSAVar)phi.getTarget()).getUniqueIndex());
		    }
		}
	    }

	    //iterates on non phi instructions
	    Iterator insts = bb.getInstructions();
	    while (insts.hasNext()) {

		//compute temp = (use[ij] - def[i1i2..i(j-1)]).
		temp.clear();
		QInst inst = (QInst) insts.next();
		QOperandBox[] operands = inst.getUses();
		for (int u = 0; u < operands.length; ++u) {
		    if (operands[u].getOperand() instanceof QSSAVar) {
			int ssaVarIndex = ((QSSAVar)operands[u].getOperand()).getUniqueIndex();
			if (!def.get(ssaVarIndex)) {
			    temp.set(ssaVarIndex);
			}
		    }
		}

		//use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
		use.or(temp);

		//def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
		if (inst.defVar() && inst.getDefined().getOperand() instanceof QSSAVar) {
		    def.set(((QSSAVar)inst.getDefined().getOperand()).getUniqueIndex());
		}
	    }

	    //look at phi join in the successors
	    Iterator succs = bb.getSuccessors();
	    while (succs.hasNext()) {
		BasicBlock succ = (BasicBlock) succs.next();
		//iterates on phi join instructions
		phis = succ.getPhis();
		while (phis.hasNext()) {
		    QPhi phi = (QPhi) phis.next();
		    if (phi instanceof QPhiJoin) {
			QPhiJoin phiJoin = (QPhiJoin) phi;
			if (phiJoin.hasOperandForBlock(bb)) {
			    /**
			     * We concider that there is an affectation
			     *  'phi catch target' = 'var associated with bb'
			     * at the end of the bb basic block.
			     */
			    QOperand operandSource = phiJoin.getOperandForBlock(bb).getOperand();
			    QOperand operandTarget = phiJoin.getTarget();
			    if (operandTarget instanceof QSSAVar &&
				operandSource instanceof QSSAVar) {
				int ssaVarIndex = ((QSSAVar)operandSource).getUniqueIndex();

				//use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
				if (!def.get(ssaVarIndex)) {
				    use.set(ssaVarIndex);
				}

				//def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
				def.set(((QSSAVar) operandTarget).getUniqueIndex());
			    }
			}
		    }
		}
	    } /* end iteration of phi join on the successors */
	} /* end computation of def and use sets */

	//compute in and out sets for each basic blocks
	/*
	 * in[n] = use[n] union (out[n] - def[n])
	 * out[n] = union {s in succ[n]}  in[s]
	 */
	boolean changed = true;
	while (changed) {
	    changed = false;
	    for (int i = topologicalOrder.length - 1; i >= 0; --i) {
		int nodeIndex = topologicalOrder[i];
		BitSet out = outs[nodeIndex];
		BitSet in = ins[nodeIndex];

		BitSet oldIn = in;


		//out[n] = union {s in succ[n]}  in[s]
		out.clear();
		Iterator succs = nodes[nodeIndex].getSuccessors();
		while (succs.hasNext()) {
		    out.or(ins[((Node)succs.next()).getIndex()]);
		}

		//in[n] = use[n] union (out[n] - def[n])
		BitSet tmp = (BitSet)out.clone();
		tmp.andNot(defs[nodeIndex]);
		in = (BitSet) uses[nodeIndex].clone();
		ins[nodeIndex] = in;
		in.or(tmp);

		if (!in.equals(oldIn)) {
		    changed = true;
		}
	    }
	}
    }

    /**
     * Compute a liveness analysis on the basic blocks
     * Fill ins and outs.
     *
     * @param ins variables lives at the entrance of each basic blocks
     * @param outs variables lives at the end of each basic blocks
     */
    public void computeNonSSALivenessAnalysis(BitSet[] ins, BitSet[] outs) {
	int nodesNumber = nodes.length;
	//init def and use sets
	//be carefull index in these arrays are index of basic blocks in
	// 'nodes' array and not in 'topologicalOrder'
	BitSet[] defs = new BitSet[nodesNumber];
	BitSet[] uses = new BitSet[nodesNumber];
	for (int i = 0; i < nodesNumber; ++i) {
	    defs[i] = new BitSet();
	    uses[i] = new BitSet();
	}

	//compute def and use sets for each basic block
	for (int i = 0; i < nodesNumber; ++i) {
	    BasicBlock bb = (BasicBlock) nodes[i];
	    BitSet def = defs[i];
	    BitSet use = uses[i];

	    /*
	     * if the basic block as n instruction i1, i2, ... in
	     * then def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
	     *      and  use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
	     */

	    //iterates on non instructions
	    BitSet temp = new BitSet();
	    Iterator insts = bb.getInstructions();
	    while (insts.hasNext()) {

		//compute temp = (use[ij] - def[i1i2..i(j-1)]).
		temp.clear();
		QInst inst = (QInst) insts.next();
		QOperandBox[] operands = inst.getUses();
		for (int u = 0; u < operands.length; ++u) {
		    if (operands[u].getOperand() instanceof QVar) {
			int varIndex = ((QVar)operands[u].getOperand()).getRegister();
			if (!def.get(varIndex)) {
			    temp.set(varIndex);
			}
		    }
		}

		//use[i1i2..ij] = use[i1i2..i(j-1)] union (use[ij] - def[i1i2..i(j-1)])
		use.or(temp);

		//def[i1i2..ij] = def[i1i2..i(j-1)] union def[ij]
		if (inst.defVar() && inst.getDefined().getOperand() instanceof QVar) {
		    def.set(((QVar)inst.getDefined().getOperand()).getRegister());
		}
	    }
	} /* end computation of def and use sets */

	//compute in and out sets for each basic blocks
	/*
	 * in[n] = use[n] union (out[n] - def[n])
	 * out[n] = union {s in succ[n]}  in[s]
	 */
	boolean changed = true;
	while (changed) {
	    changed = false;
	    for (int i = topologicalOrder.length - 1; i >= 0; --i) {
		int nodeIndex = topologicalOrder[i];
		BitSet out = outs[nodeIndex];
		BitSet in = ins[nodeIndex];

		BitSet oldIn = in;


		//out[n] = union {s in succ[n]}  in[s]
		out.clear();
		Iterator succs = nodes[nodeIndex].getSuccessors();
		while (succs.hasNext()) {
		    out.or(ins[((Node)succs.next()).getIndex()]);
		}

		//in[n] = use[n] union (out[n] - def[n])
		BitSet tmp = (BitSet)out.clone();
		tmp.andNot(defs[nodeIndex]);
		in = (BitSet) uses[nodeIndex].clone();
		ins[nodeIndex] = in;
		in.or(tmp);

		if (!in.equals(oldIn)) {
		    changed = true;
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // PROTECTED BLOCK SEARCH
    // -------------------------------------------------------------------
    /**
     * Add all phi catch in the catch block of a protected basic block
     *
     * @param bb the protected basic block
     * @param phis the vector of phis to fill
     */
    protected void addPhiCatch(BasicBlock bb, Vector phis) {
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    if (exceptionHandlers[i].contains(bb)) {
		addPhiCatchFrom(exceptionHandlers[i].getHandlerBlock(), phis);
	    }
	}
    }

    /**
     * Add all phi catch from a basic block
     *
     * @param bb the basic block containing phi catch
     * @param phis the vector of phis to fill
     */
    protected void addPhiCatchFrom(BasicBlock bb, Vector phis) {
	Iterator allPhis = bb.getPhis();
	while (allPhis.hasNext()) {
	    QPhi phi = (QPhi) allPhis.next();
	    if (phi instanceof QPhiCatch) {
		phis.addElement(phi);
	    }
	}
    }

    // -------------------------------------------------------------------
    // GRAPH SEARCH
    // -------------------------------------------------------------------
    /**
     * Fill the array topologicalOrder with the nodes in topological
     * order
     *
     * @param nodes all graph nodes
     * @param start the start node in the graph
     */
    protected void topologicalSort(Node[] nodes, Node start) {
	int nodesNumber = nodes.length;
	for (int i = 0; i < nodesNumber; ++i) {
	    nodes[i].setMarked(false);
	}

	num = nodesNumber;
	depthFirstSearch(start);
	for (int i = 0; i < nodesNumber; ++i) {
	    if (!nodes[i].getMarked()) {
		depthFirstSearch(nodes[i]);
	    }
	}
    }

    /**
     * Depth first search from a given node
     *
     * @param n the node to start the search
     */
    protected void depthFirstSearch(Node n) {
	if (!n.getMarked()) {
	    n.setMarked(true);
	    Iterator it  = n.getSuccessors();
	    while (it.hasNext()) {
		depthFirstSearch((Node)it.next());
	    }
	    topologicalOrder[--num] = n.getIndex();
	}
    }


    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Graph graph;
    protected int num;
    protected BasicBlock start;
    protected Node[] nodes;
    protected int[] topologicalOrder;
    protected ExceptionHandler[] exceptionHandlers;
}
