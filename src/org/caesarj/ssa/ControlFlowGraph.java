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
 * $Id: ControlFlowGraph.java,v 1.2 2003-10-29 12:29:11 kloppenburg Exp $
 */

package org.caesarj.ssa;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.caesarj.classfile.AccessorContainer;
import org.caesarj.classfile.AccessorTransformer;
import org.caesarj.classfile.BadAccessorException;
import org.caesarj.classfile.CodeInfo;
import org.caesarj.classfile.Constants;
import org.caesarj.classfile.HandlerInfo;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.InstructionAccessor;
import org.caesarj.classfile.JumpInstruction;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.classfile.SwitchInstruction;
import org.caesarj.util.InconsistencyException;

/**
 * The control flow graph of a method.
 * Each node are basic blocks.
 *
 * @author Michael Fernandez
 */
public class ControlFlowGraph {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the control flow graph with the information code of a
     * method.
     *
     * @param methodInfo informations on the method
     * @param info informations on the method code
     */
    public ControlFlowGraph(MethodInfo methodInfo, CodeInfo info) {
	this.info = info;
	graph = new Graph();
	subroutines = new HashMap();

	findBasicBlocks();

	generateQInsts(info.getInstructions(), methodInfo);

	constructSSAForm();

	optimize();

	if (DEBUG) {
	    for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()) {
		System.out.println("\n"+bb);
		Iterator insts = bb.getAllSSAInstructions();
		while (insts.hasNext()) {
		    QInst inst = (QInst) insts.next();
		    System.out.println(inst);
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // OPTIMIZATIONS
    // -------------------------------------------------------------------
    /**
     * Run optimizations
     */
    public void optimize() {
	performCopyPropagation();
    }

    /**
     * Remove SSA unuse variables
     */
    public void removeUnusedVariables() {
	UnusedComputer unusedComputer = new UnusedComputer(graph.getNodes());
	unusedComputer.removeUnusedVariables();
	unusedComputer.removeUnusefullPhis();
    }

    /**
     * Copy propagation
     */
    public void performCopyPropagation() {
	CopyPropagation copyPropagation = new CopyPropagation(graph.getNodes());
	copyPropagation.propagate();
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Find non-locals variables.
     * Algorithm from Briggs, Cooper, Harvey and Simpson
     */
    public BitSet findNonLocals() {
	BitSet nonLocals = new BitSet(nbVar);
	BitSet killed = new BitSet(nbVar);
	BitSet emptySet = new BitSet(nbVar);
	for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()){
	    //clear killed set
	    killed.and(emptySet);

	    Iterator insts = bb.getInstructions();
	    while (insts.hasNext()) {
		QInst inst = (QInst) insts.next();
		QOperandBox[] ops = inst.getUses();
		for (int i = 0; i < ops.length; ++i) {
		    QOperand op = ops[i].getOperand();
		    if (op instanceof QVar) {
			int r = ((QVar)op).getRegister();
			if (!killed.get(r)) {
			    nonLocals.set(r);
			}
		    }
		}
		if (inst.defVar()) {
		    QOperand op = inst.getDefined().getOperand();
		    if (op instanceof QVar) {
			int r = ((QVar)op).getRegister();
			killed.set(r);
		    }
		}
	    }
	}
	return nonLocals;
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate instructions of the method.
     *
     * @return the instructions.
     */
    public Instruction[] getInstructions() {
	//remove SSA form

	//find the interference graph
	LivenessComputer liveness = new LivenessComputer(graph, start, exceptionHandlers);
	InterferenceGraph interfGraph = liveness.computeInterferenceGraph();
	//color the graph
	final ColorComputer colorer = new ColorComputer(interfGraph);
	colorer.color();

	SSADestructor.removeSSAForm(graph, start, exceptionHandlers, colorer);


	//simplify consecutive jumps and remove inaccessible blocks from trace
	final Node end = this.end;
	graph.visitGraphFromNode(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    if (n != end)
			((BasicBlock) n).simplifyJumps();
		    return true;
		}
	    });
	final HashSet accessibleBlocks = new HashSet();
	graph.visitGraphFromNode(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    accessibleBlocks.add(n);
		    return true;
		}
	    });
	for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()){
	    if (!accessibleBlocks.contains(bb)) {
		bb.removeFromList();
	    }
	}
	graph.setNodesIndex();

	//propagation of expression
	liveness = new LivenessComputer(graph, start, exceptionHandlers);
	BitSet[] ins = new BitSet[graph.size()];
	BitSet[] outs = new BitSet[ins.length];
	for (int i = 0; i < ins.length; ++i) {
	    ins[i] = new BitSet();
	    outs[i] = new BitSet();
	}
	liveness.computeNonSSALivenessAnalysis(ins, outs);
	ins = null;
	Propagator.propagate(firstBB, outs);

	//classfile instructions generation
	CodeGeneratorMethod codeGen = new CodeGeneratorMethod();
	//init block can have affectation instruction to generate
	start.setNext(firstBB);
	start.generate(codeGen, outs[start.getIndex()], nbVar);
	start.removeFromList();
	for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()){
	    bb.generate(codeGen, outs[bb.getIndex()], nbVar);
	}
	Instruction[] insts = codeGen.getInstructions();

	//find real labels
	findLabelAdresses(insts);

	return insts;
	//return info.getInstructions();
    }

    /**
     * Generate handler info for exception with the generated instructions.
     *
     * @param insts the generated instructions.
     * @return handlers of exception for classfile.
     */
    public HandlerInfo[] getHandlerInfos(Instruction[] insts) {
	HandlerInfo[] handlers = info.getHandlers();
	for (int i = 0; i < handlers.length; ++i) {
	    HandlerInfo handler = handlers[i];
	    ExceptionHandler eh = exceptionHandlers[i];
	    eh.searchIndex();
	    handler.setStart(insts[eh.getStart()]);
	    handler.setEnd(insts[eh.getEnd()]);
	    handler.setHandler(insts[eh.getHandle()]);
	}
	return handlers;
    }

    // -------------------------------------------------------------------
    // PRIVATE METHOD TO GENERATION CLASSFILE INSTRUCTIONS
    // -------------------------------------------------------------------

    /**
     * Modify instruction wich point on an edge, and find the correponding
     * instruction.
     *
     * @param insts the generated instructions.
     */
    private void findLabelAdresses(final Instruction[] insts) {
	AccessorTransformer transformer = new AccessorTransformer() {
	   public InstructionAccessor transform(InstructionAccessor accessor,
						AccessorContainer container) {
	       Edge edge = ((EdgeLabel)accessor).getEdge();
	       return insts[((BasicBlock)edge.getTarget()).getStartGen()];
	   }
	 };
	try {
	    for (int i = 0; i < insts.length; i++) {
		if(insts[i] instanceof AccessorContainer) {
		    ((AccessorContainer)insts[i]).transformAccessors(transformer);
		}
	    }
	} catch (BadAccessorException e) {
	    throw new RuntimeException("INTERNAL ERROR");
	}
    }


    // -------------------------------------------------------------------
    // PRIVATE METHOD TO CONSTRUCT SSA FORM
    // -------------------------------------------------------------------
    /**
     * Transform the variable in SSA form
     */
    private void constructSSAForm() {
	SSAConstructor constructor = new SSAConstructor(graph, start, end, findNonLocals(), exceptionHandlers, subroutines.values());
	constructor.constructSSAForm();

	precolorInitVariables();

	removeUnusedVariables();
    }

    // -------------------------------------------------------------------
    // PRIVATE METHOD TO INSERT INSTRUCTIONS IN THE BASIC BLOCKS
    // -------------------------------------------------------------------
    /**
     * Generate 3-adress instructions in all basic blocks.
     *
     * @param insts method instructions
     * @param method informations on the method
     */
    private void generateQInsts(final Instruction[] insts, MethodInfo method) {
	addInitMethodInstruction(method);

	final QuadrupleGenerator genQ = new QuadrupleGenerator(info.getMaxLocals());
	graph.visitGraph(start, new NodeVisitor() {
		public boolean visit(Node n) {
		    ((BasicBlock) n).constructQuadruple(insts, genQ);
		    ((BasicBlock) n).simplifyNewInstructions();
		    return true;
		}
	    });
	nbVar = genQ.getVarNumber();
    }

    // -------------------------------------------------------------------
    // PRIVATE METHOD TO CONSTRUCT BASIC BLOCKS
    // -------------------------------------------------------------------
    /**
     * find the basic blocks and construct the control flow graph with them.
     */
    private void findBasicBlocks() {
	Instruction[] insts = info.getInstructions();
	HandlerInfo[] handlers = info.getHandlers();

	short[] numInsts;
	initExceptionHandlers(handlers, insts);

	numInsts = markStartblock(insts);

	int nbBlock = numberBlock(numInsts, insts);

	//to find easily the basic block to construct the CFG
	BasicBlock[] bbs = createBB(nbBlock, numInsts, insts);

	firstBB = bbs[0];

	findCFGEdges(insts, numInsts, bbs);

	findSubRoutines(numInsts, insts, bbs);

	//this procedure add blocks so it's important to execute it after others
	findExceptionBlocks(numInsts, insts, bbs);

	//this array is no usefull because we don't need the source code
	//We can now use the list of HandleBasicBlock with firstBB.
	bbs = null;

	addStartEndBlocks();

	removeCriticalEdges();
    }

    /**
     * Remove critical edges. A critical edge is an edge from a block with
     * more than one successor to a block with more than one predecessor.
     * Critical edges can hinder code motion and should be removed.
     */
    private void removeCriticalEdges() {
	LinkedList list = new LinkedList();
	for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()) {
	    if (bb.getOutEdgesNumber() > 1) {
		Iterator outEdges = bb.getOutEdges();
		while (outEdges.hasNext()) {
		    Edge outEdge = (Edge) outEdges.next();
		    BasicBlock succ = (BasicBlock) outEdge.getTarget();
		    if (succ.getInEdgesNumber() > 1 &&
			!succ.isFirstBlockSubroutine() &&
			!succ.isCatchBlock()) {

			list.addLast(outEdge);
		    }
		}
	    }
	}

	while (!list.isEmpty()) {
	    Edge edge = (Edge) list.removeFirst();
	    BasicBlock source = (BasicBlock) edge.getSource();
	    BasicBlock target = (BasicBlock) edge.getTarget();
	    if (source.getOutEdgesNumber() > 1 &&
		target.getInEdgesNumber() > 1) {

		//insert a new block in the CFG between source and target
		BasicBlock insert = new BasicBlock(graph);
		edge.getSource().changeEdgeTarget(edge, insert);
		insert.addDefaultNextBlock(target);

		//insert the new block before target in the trace
		target.insertBefore(insert);

		for (int i = 0; i < exceptionHandlers.length; ++i) {
		    if (exceptionHandlers[i].contains(target)) {
			exceptionHandlers[i].addProtectedBlock(insert);
			insert.addExceptionNextBlock(target);
		    }
		}
	    }
	}
    }

    /**
     * add the start and the end blocks in the CFG
     */
    private void addStartEndBlocks() {
	//this blocks have no code to generate and to
	start = new BasicBlock(graph);
	end = new BasicBlock(graph);

	start.addDefaultNextBlock(firstBB);
	start.addCFGNextBlock(end);
	int i=0;
	//all blocks with no succesor are linked to the end block
	for (BasicBlock bb = firstBB; bb != null; bb = bb.getNext()){
	    if (!bb.hasSuccessor()) {
		++i;
		bb.addCFGNextBlock(end);
	    }
	}
    }


    /**
     * Initialize the array of Exception handlers.
     *
     * @param sourceHandlers the handlers of the source code
     * @param insts the source instructions.
     */
    private void initExceptionHandlers(HandlerInfo[] sourceHandlers,
				       Instruction[] insts) {
	exceptionHandlers = new ExceptionHandler[sourceHandlers.length];
	for (int i = 0; i < sourceHandlers.length; ++i) {
	    int start = getInstructionLine(insts,sourceHandlers[i].getStart());
	    int end = getInstructionLine(insts,sourceHandlers[i].getEnd());
	    int handle = getInstructionLine(insts,sourceHandlers[i].getHandler());
	    exceptionHandlers[i] = new ExceptionHandler(start, end, handle);
	}
    }

    /**
     * Get the index of the instruction ins in the array insts.
     *
     * Complexity : O(insts.length)
     *
     * @param insts the array of instructions
     * @param ins the instruction to find in it
     */
    private int getInstructionLine(Instruction[] insts,
				   InstructionAccessor ins) {
	int i;
	for(i=0; i<insts.length && ins != insts[i]; ++i) /* Nothing */;
	return i;
    }

    /**
     * Return an array corresponding to the array <code>insts</code> and
     * mark if an instruction is the start point of a basic block.
     *
     * @param insts the array of instructions
     * @return an array which the same length and the values are
     *   tab[i] = 1 if insts[i] is a start instruction of a basic block,
     *            0 else.
     */
    private short[] markStartblock(Instruction[] insts) {
	int length = insts.length;
	short[] startblock = new short[length]; //initialized to zero.
	for (int j = 0; j < length; j++) {
	    //if the curent instruction is a jump, the destination
	    // instruction and the next instruction are marked as a
	    // begining of a basic block
	    if (insts[j] instanceof JumpInstruction) {
		if (j+1 < length) {
		    startblock[j+1] = 1;
		}
		JumpInstruction ji = (JumpInstruction) insts[j];
		startblock[getInstructionLine(insts,ji.getTarget())] = 1;
	    }
	    // all labels of a switch start a basic block.
	    if (insts[j] instanceof SwitchInstruction) {
		SwitchInstruction si = (SwitchInstruction) insts[j];
		for (int k=-1;k<si.getSwitchCount();k++) {
		    startblock[getInstructionLine(insts,si.getTarget(k))] = 1;
		}
	    }
	    //if the next instruction is not reached, this a new basic block.
	    if (!insts[j].canComplete()&&j+1<length) {
		startblock[j+1] = 1;
	    }
	}
	//mark the beginning of try block as a new basic block.
	//the instruction after the last instruction of a try block
	// is also a start point of a basic block.
	//The catch block is also a new basic block.
	for (int j = 0; j < exceptionHandlers.length; ++j) {
	    startblock[exceptionHandlers[j].getStart()] = 1;
	    int afterEnd = exceptionHandlers[j].getEnd() + 1;
	    if (afterEnd < length) {
		startblock[afterEnd] = 1;
	    }
	    startblock[exceptionHandlers[j].getHandle()] = 1;
	}
	return startblock;
    }

    /**
     * Number blocks in the array startblock :
     *  startblock[i]=j iff insts[i] is in the block j.
     * Give the number of basic blocks.
     *
     * @param insts the instructions
     * @param startblock array showing wich instruction is the beginning of
     *                  a basic block.
     * @return the number of basic blocks.
     */
    private int numberBlock(short[] startblock, Instruction[] insts) {
	short blockNumber = 0;
	if (startblock.length == 0)
	    return 0;

	startblock[0] = blockNumber;
	for(int j=1;j<insts.length;j++) {
	    if (startblock[j] == 1) {
		++blockNumber;
	    }
	    startblock[j] = blockNumber;
	}
	return blockNumber + 1;
    }


    /**
     * Create the basic blocks.
     *
     * @param number the number of blocks
     * @param numBlocks an array containing the number of basic block for
     *                 each instruction.
     * @param insts the instructions
     * @return an array of handlers
     */
    private BasicBlock[] createBB(int number,
				  short[] numBlocks,
				  Instruction[] insts) {
	BasicBlock[] bbs = new BasicBlock[number];
	int inst = 0;
	for (int num = 0; num < number; ++num) {
	    //find the first and the last instruction of each block
	    int start = inst;
	    while (inst < numBlocks.length && numBlocks[inst] == num) {
		++inst;
	    }
	    int end = inst - 1;
	    bbs[num] = new BasicBlock(start, end, graph);
	    //create the list
	    if (num > 0) {
		bbs[num - 1].setNext(bbs[num]);
	    }
	}
	return bbs;
    }

    /**
     * Create the edges in the control flow graph with the basic blocks.
     *
     * @param insts the instructions
     * @param numBlocks an array containing the number of basic block for
     *                 each instruction.
     * @param bbs the basic blocks
     */
    private void findCFGEdges(Instruction[] insts, short[] numBlocks,
			      BasicBlock[] bbs) {
	for(int i = 0; i < bbs.length; ++i) {
	    BasicBlock bb = bbs[i];
	    Instruction lastInst = insts[bb.getEnd()];
	    if (lastInst.canComplete() && i != bbs.length - 1) {
		//the next block is accessible
		bb.addDefaultNextBlock(bbs[i+1]);
	    }
	    if (lastInst instanceof JumpInstruction) {
		//goto, jsr are distincts than other jump instructions.
		Edge edge;
		JumpInstruction jumpInst = (JumpInstruction) lastInst;
		if (lastInst.getOpcode() == Constants.opc_goto) {
		     edge = bb.addDefaultNextBlock(bbs[numBlocks[getInstructionLine(insts, jumpInst.getTarget())]]);
		     jumpInst.setTarget(new EdgeLabel(edge));
		} else if (lastInst.getOpcode() == Constants.opc_jsr) {
		    //do nothing here
		    //see method findSubRoutines
		} else {
		    edge = bb.addConditionNextBlock(bbs[numBlocks[getInstructionLine(insts, jumpInst.getTarget())]]);
		    jumpInst.setTarget(new EdgeLabel(edge));
		}

	    }
	    if (lastInst instanceof SwitchInstruction) {
		SwitchInstruction si = (SwitchInstruction) lastInst;
		for (int k=-1;k<si.getSwitchCount();k++) {
		    Edge edge = bb.addSwitchNextBlock(bbs[numBlocks[getInstructionLine(insts, si.getTarget(k))]]);
		    si.setTarget(k, new EdgeLabel(edge));
		}
	    }

	}
    }

    /**
     * Find the blocks protected by each ExceptionHandler.
     * Add "exception edges" in the control flow graph.
     * Add a basic block before the block which catch the exception.
     *  This last block store the exception in a variable.
     *
     * Modification 02/04/2002 : add an exception edge between the blocks
     *  before the first block protected and the catch block.
     *
     * @param numBlocks an array containing the number of basic block for
     *                 each instruction.
     * @param insts the instructions
     * @param bbs the basic blocks
     */
    private void findExceptionBlocks(short[] numBlocks,
				     Instruction[] insts,
				     BasicBlock[] bbs) {
	//shows if we had ever add a block for the catch block
	boolean[] marqued = new boolean[bbs.length];
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    ExceptionHandler handle = exceptionHandlers[i];
	    int numBlock = numBlocks[handle.getHandle()];
	    BasicBlock catchBlock = bbs[numBlock];
	    if (!marqued[numBlock]) {
		//add a new block

		//if the first instruction is the saving of the
		// exception put this instruction in the
		// new block else this saving will be added during
		// the 3-adress code generation
		BasicBlock newBlock;
		Instruction first = insts[catchBlock.getStart()];
		if (first instanceof LocalVarInstruction &&
		    ((LocalVarInstruction) first).isStore()) {
		    int instructionLine = catchBlock.getStart();
		    newBlock = new BasicBlock(instructionLine,
					      instructionLine,
					      graph);
		    //remove this instruction from the next block
		    catchBlock.setStart(instructionLine +  1);
		} else {
		    newBlock = new BasicBlock(graph);
		}
		newBlock.setCatchBlock(true);
		catchBlock.insertBefore(newBlock);
		newBlock.addDefaultNextBlock(catchBlock);
		bbs[numBlock] = newBlock;
		catchBlock = newBlock;
		marqued[numBlock] = true;
	    }
	    handle.setHandlerBlock(catchBlock);
	}
	for (int i = 0; i < exceptionHandlers.length; ++i) {
	    ExceptionHandler handle = exceptionHandlers[i];
	    BasicBlock catchBlock = handle.getHandlerBlock();
	    int numInst = handle.getStart();
	    int instEnd = handle.getEnd();
	    int numBlock = -1;

	    // MODIFICATION 02/04/2002
	    if (numInst <= instEnd) {
		BasicBlock first = bbs[numBlocks[numInst]];
		if (catchBlock != first) { // this is possible !
		    Iterator preds = first.getPredecessors();
		    while (preds.hasNext()) {
			BasicBlock pred = (BasicBlock) preds.next();
			pred.addExceptionNextBlock(catchBlock);
		    }
		}
	    }
	    //END MODIFICATION 02/04/2002

	    numBlock = -1;
	    for ( ; numInst <= instEnd; ++numInst) {
		if (numBlocks[numInst] != numBlock) {
		    numBlock = numBlocks[numInst];
		    //add blocks in exception handler.
		    BasicBlock bb = bbs[numBlock];
		    handle.addProtectedBlock(bb);
		    //add exception edges
		    bb.addExceptionNextBlock(catchBlock);

		    //if it's a catch block add also the next block
		    if (marqued[numBlock]) {
			BasicBlock next = bb.getNext();
			handle.addProtectedBlock(next);
			next.addExceptionNextBlock(catchBlock);
		    }
		}
	    }

	}
    }

    /**
     * Find subroutines in the method.
     * Create an object SubRoutine for each one.
     * Add the edges.
     *
     * @param numBlocks an array containing the number of basic block for
     *                 each instruction.
     * @param insts the instructions
     * @param bbs the basic blocks
     */

    public void findSubRoutines(short[] numBlocks,
				final Instruction[] insts,
				BasicBlock[] bbs) {
	for(int i = 0; i < bbs.length; ++i) {
	    Instruction lastInst = insts[bbs[i].getEnd()];
	    if (lastInst.getOpcode() == Constants.opc_jsr) {
		JumpInstruction jumpInst = (JumpInstruction) lastInst;
		BasicBlock subroutineCall = bbs[i];

		BasicBlock subroutineReturn = bbs[i+1];
		final BasicBlock startSubroutine = bbs[numBlocks[getInstructionLine(insts, jumpInst.getTarget())]];
		SubRoutine sub = (SubRoutine) subroutines.get(startSubroutine);
		if (sub == null) {
		    startSubroutine.setFirstBlockSubroutine(true);
		    //we have to create the SubRoutine object
		    //so we research the end block of the subroutine
		    //we follow a path from start block until we find the ret
		    final Map subs = subroutines;
		    final BasicBlock currentBB = startSubroutine;
		    //the search will not enter into nested subroutine
		    // because there is an edge after a block with
		    // a jsr and its next block.
		    graph.visitGraphFromNode(currentBB, new NodeVisitor() {
			    public boolean visit(Node n) {
				BasicBlock bb = (BasicBlock) n;
				Instruction last = insts[bb.getEnd()];
				if (last.getOpcode() == Constants.opc_ret) {
				    //we found the end of the subroutine
				    SubRoutine s = new SubRoutine(startSubroutine,
								  currentBB);
				    subs.put(startSubroutine, s);
				    return false;
				}
				return true;
			    }
			});
		    if ((sub = (SubRoutine)subroutines.get(startSubroutine)) == null) {
			//error
			throw new RuntimeException("NO RETURN TO SUBROUTINE");
		    }

		}
	        //add edges
		Edge edgeCall = subroutineCall.addSubRoutineCallNextBlock(startSubroutine);
		jumpInst.setTarget(new EdgeLabel(edgeCall));

		Edge edgeReturn = sub.getEnd().addSubRoutineReturnNextBlock(subroutineReturn);

		sub.addCall(edgeCall, edgeReturn);
	    }
	}
	//remove all edges from basic blocks which end by jsr with
	// its next block.
	for(int i = 0; i < bbs.length; ++i) {
	    Instruction lastInst = insts[bbs[i].getEnd()];
	    if (lastInst.getOpcode() == Constants.opc_jsr) {
		BasicBlock subroutineCall = bbs[i];
		BasicBlock subroutineReturn = bbs[i+1];
		subroutineCall.removeSuccessor(subroutineReturn);
	    }
	}
    }

    /**
     * Add 3-adress instructions symbolising parameters initialization
     *
     * @param methodInfod informations on the method
     */
    private void addInitMethodInstruction(MethodInfo methodInfo) {
	String	signature = methodInfo.getSignature();
	int		paramNum = 0;

	if ((methodInfo.getModifiers() & Constants.ACC_STATIC) == 0) {
	    // an instance method always passes "this" as first, hidden parameter
	    start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_REFERENCE), false));

	    paramNum += 1;
	}

	if (signature.charAt(0) != '(') {
	    throw new InconsistencyException("invalid signature " + signature);
	}

	int		pos = 1;

    _method_parameters_:
	for (;;) {
	    switch (signature.charAt(pos++)) {
	    case ')':
		break _method_parameters_;

	    case '[':
		while (signature.charAt(pos) == '[') {
		    pos += 1;
		}
		if (signature.charAt(pos) == 'L') {
		    while (signature.charAt(pos) != ';') {
			pos += 1;
		    }
		}
		pos += 1;

		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_REFERENCE), false));

		paramNum += 1;
		break;

	    case 'L':
		while (signature.charAt(pos) != ';') {
		    pos += 1;
		}
		pos += 1;

		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_REFERENCE), false));

		paramNum += 1;
		break;

	    case 'Z':
	    case 'B':
	    case 'C':
	    case 'S':
	    case 'I':
		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_INT), false));

		paramNum += 1;
		break;
	    case 'F':
		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_FLOAT), false));

		paramNum += 1;
		break;

	    case 'D':
		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_DOUBLE), false));
		paramNum += 2;
		break;

	    case 'J':
		start.addInstruction(new QDeclareInitialised(new QVar(paramNum, Constants.TYP_LONG), false));
		paramNum += 2;
		break;

	    default:
		throw new InconsistencyException("invalid signature " + signature);
	    }
	}
    }

    /**
     * Precolor the SSA init variable
     */
    protected void precolorInitVariables() {
	Iterator insts = start.getInstructions();
	while (insts.hasNext()) {
	    QInst inst = (QInst) insts.next();
	    if (inst instanceof QDeclareInitialised) {
		QOperand op = inst.getDefined().getOperand();
		if (op instanceof QSSAVar) {
		    SSAVar var = ((QSSAVar)op).getSSAVar();
		    var.setColor(var.getSourceIndex());
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------

    //the code information of the method.
    protected CodeInfo info;

    //the array of exception handlers.
    protected ExceptionHandler[] exceptionHandlers;

    //map a block starting a subroutine with his correponding object SubRoutine
    protected Map subroutines;

    //the first basic block from the trace.
    protected BasicBlock firstBB;

    //the graph containing basic blocks
    protected Graph graph;

    //the start block of the CFG
    protected BasicBlock start;

    //the end block of the CFG
    protected BasicBlock end;

    //the number of variables used
    protected int nbVar;

    //debug mode for all transformations
    public static final boolean DEBUG = false;
}
