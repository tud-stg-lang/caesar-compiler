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
 * $Id: BasicBlock.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import org.caesarj.classfile.Constants;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.JumpInstruction;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.SwitchInstruction;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * A basic block of the flow graph.
 *
 * This is a node of the flow graph.
 *
 * All nodes are linked to compose the trace of the program.
 * This list is used to know the order to generate basic blocks.
 *
 * @author Michael Fernandez
 */
public class BasicBlock extends Node {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a basic block with no relation with the source
     * code
     * The associated basic block is created.
     *
     * @param graph the graph of the basic block
     */
    public BasicBlock(Graph graph) {
	this(-1,-1, graph);
    }

    /**
     * Construct a basic block by defining the index of the
     * first and the last instructions this one in the source
     * code.
     *
     * The associated basic block is created.
     *
     * @param graph the graph of the basic block
     */
    public BasicBlock(int begin, int end, Graph graph) {
	this.firstInst = begin;
	this.lastInst = end;
	this.entryStack = null;
	this.insts = new QInstArray(this);
	this.phis = new QInstArray(this);
	graph.addNode(this);
    }

    // -------------------------------------------------------------------
    // ACCESSOR TO INSTRUCTIONS
    // -------------------------------------------------------------------
    /**
     * Add a 3-adress instruction to the basic block.
     *
     * @param newInst the instruction to add.
     */
    public void addInstruction(QInst newInst) {
	insts.addInstruction(newInst);
    }

    /**
     * Return the instructions of the basic block
     *
     * @return instructions of the basic block.
     */
    public Iterator getInstructions() {
	return insts.iterator();
    }

    /**
     * Add a phi function in the basic block
     *
     * @param phi phi function to add
     */
    public void addPhi(QPhi phi) {
	phis.addInstruction(phi);
    }

    /**
     * Return the list of phi for the current basic block
     *
     * @return phis of the basic block.
     */
    public Iterator getPhis() {
	return phis.iterator();
    }

    /**
     * Get the list of all SSA instructions (phis + other instruction)
     *
     * @return all SSA instructions of the basic block
     */
    public Iterator getAllSSAInstructions() {
	return new DoubleIterator(phis.iterator(), insts.iterator());
    }


    /**
     * Remove the last instruction
     *
     * @return the instruction removed
     */
    public QInst removeLastInstruction() {
	return insts.removeLastInstruction();
    }

    /**
     * Get the vector of instruction
     *
     * @return the vector of instruction
     */
    public QInstArray getInstructionsArray() {
	return insts;
    }

    /**
     * Get the vector of phis
     *
     * @return the vector of instruction
     */
    public QInstArray getPhisArray() {
	return phis;
    }

    // -------------------------------------------------------------------
    // ACCESSOR TO THE GRAPH STRUCTURE
    // -------------------------------------------------------------------
    /**
     * Add a basic block as default next block in the CFG
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addDefaultNextBlock(BasicBlock b) {
	defaultNext = new CFGEdge(CFGEdge.DEFAULT_EDGE);
	return addSuccessor(defaultNext, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * This path is used if the condition is true.
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addConditionNextBlock(BasicBlock b) {
        conditionNext = new CFGEdge(CFGEdge.CONDITION_EDGE);
	return addSuccessor(conditionNext, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * Used for a switch.
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addSwitchNextBlock(BasicBlock b) {
	Edge edge = new CFGEdge(CFGEdge.SWITCH_EDGE);
	return addSuccessor(edge, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * Used for an exception edge.
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addExceptionNextBlock(BasicBlock b) {
	Edge edge = new CFGEdge(CFGEdge.EXCEPTION_EDGE);
	return addSuccessor(edge, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * Used for a subroutine call or return.
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addSubRoutineReturnNextBlock(BasicBlock b) {
	Edge edge = new CFGEdge(CFGEdge.SUBROUTINE_EDGE);
	return addSuccessor(edge, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * Used for a subroutine call or return.
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addSubRoutineCallNextBlock(BasicBlock b) {
	defaultNext = new CFGEdge(CFGEdge.SUBROUTINE_EDGE);
	return addSuccessor(defaultNext, b);
    }

    /**
     * Add a basic block as next block in the CFG
     * Used for a control flow edge
     * (which links a block with the end block for example);
     *
     * @param b the block to add.
     * @return the edge created by the addition.
     */
    public Edge addCFGNextBlock(BasicBlock b) {
	Edge edge = new CFGEdge(CFGEdge.CFG_EDGE);
	return addSuccessor(edge, b);
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Return a list of  basic blocks which are succesors of the
     * current block. This not include EXCEPTION_EDGE and CFG_EDGE
     * SUBROUTINE_EDGE.
     */
    public BasicBlock[] getNextBasicBlocks() {
	Vector v = new Vector();
	Iterator it = getOutEdges();
	while (it.hasNext()) {
	    CFGEdge edge = (CFGEdge) it.next();
	    int type = edge.getType();
	    if (type != CFGEdge.EXCEPTION_EDGE &&
		type != CFGEdge.CFG_EDGE && true &&
		type != CFGEdge.SUBROUTINE_EDGE) {
		BasicBlock bb = ((BasicBlock)edge.getTarget());
		if (bb != null)
		    v.addElement(bb);
	    }
	}
	BasicBlock[] basicBlocks = new BasicBlock[v.size()];
	v.toArray(basicBlocks);
	return basicBlocks;
    }

    /**
     * Get the default next bloc
     *
     * @return the edge to the default next bloc
     */
    public Edge getDefaultNext() {
	return defaultNext;
    }

    /**
     * Get the conditional next bloc
     *
     * @return the edge to the conditional next bloc
     */
    public Edge getConditionNext() {
	return conditionNext;
    }

    /**
     * Test if there is just one instruction wich is a QJump in
     * the basic bloc
     */
    public boolean isEmpty() {
	if (insts.size() == 0)
	    return true;
	return (insts.size() == 1 &&
		insts.getInstructionAt(0) instanceof QJump);
    }

    /**
     * Get the QJump instruction in the basic block.
     * Return null if there is no QJump instruction.
     */
    public QJump getJump() {
	if (insts.size() == 0)
	    return null;
	QInst inst = insts.getLastInstruction();
	if (inst instanceof QJump)
	    return (QJump) inst;
	return null;
    }

    /**
     * Get the index of the first instruction generated for this block
     *
     * Precondition : the method generate should be called before to
     *  know the correct index
     *
     * @return index of the first instruction.
     */
    public int getStartGen() {
	return firstInstGen;
    }

    /**
     * Get the number of the generated instructions for this block
     *
     * Precondition : the method generate should be called before to
     *  know the correct number
     *
     * @return number of instructions.
     */
    public int getNbGen() {
	return nbGen;
    }

    /**
     * Get the index of the first instruction in the source code for
     * the basic block associated.
     *
     * @return  index of the first instruction in the source code.
     */
    public int getStart() {
	return firstInst;
    }

    /**
     * Change the index of the first instruction in the source code for
     * the basic block associated.
     *
     * @param start index of the first instruction in the source code.
     */
    /*package*/ void setStart(int start) {
	firstInst = start;
    }

    /**
     * Get the index of the last instruction in the source code for
     * the basic block associated.
     *
     * @return  index of the last instruction in the source code.
     */
    public int getEnd() {
	return lastInst;
    }

    public String toString() {
	String tmp = "";
	tmp += super.toString();
	/*	if (firstInst != 0) {
	    for (int i = firstInst; i <= lastInst; ++i) {
		tmp += "\t"+ i +  "\n";
	    }
	}
	tmp +="\t\tNext : ";
	java.util.Iterator it = getSuccessors();
	while (it.hasNext()) {
	    tmp +="\n\t\t\t" +  it.next();
	    }*/
	return tmp;
    }

    /**
     * Test if this block is the first block of a subroutine
     */
    public boolean isFirstBlockSubroutine() {
	return firstBlockSubroutine;
    }

    /**
     * Test if this block is a catch block
     */
    public boolean isCatchBlock() {
	return catchBlock;
    }

    /**
     * Set or unset this block as the first block of a subroutine
     */
    public void setFirstBlockSubroutine(boolean firstBlockSubroutine) {
	this.firstBlockSubroutine = firstBlockSubroutine;
    }

    /**
     * Set or unset this block as a catch block
     */
    public void setCatchBlock(boolean catchBlock) {
	this.catchBlock = catchBlock;
    }

    /**
     * Get the entry stack
     * Return null the entry stack hasn't been initialized
     */
    public QOperand[] getEntryStack() {
	return entryStack;
    }

    /**
     * Set the entry stack
     */
    public void setEntryStack(QOperand[] entryStack) {
	this.entryStack = entryStack;
    }

    // -------------------------------------------------------------------
    // OPERATION ON THE LIST OF BASIC BLOCK (TRACE)
    // -------------------------------------------------------------------
    /**
     * Return the previous basic block
     *
     * @return the previous basic block if this one is not the first
     *         null else.
     */
    public BasicBlock getPrevious() {
	return previous;
    }

    /**
     * Return the next basic block
     *
     * @return the next basic block if this one is not the last
     *         null else.
     */
    public BasicBlock getNext() {
	return next;
    }

    /**
     * Test if this basic block is the last of the list.
     *
     * @return true iff if this basic block has no successor.
     */
    public boolean hasNext() {
	return next != null;
    }

    /**
     * Set the previous basic block in the list
     * The double chaining is automatically done.
     *
     * @param previous the previous node in the list
     */
    public void setPrevious(BasicBlock previous) {
	if (previous != null)
	    previous.next = this;
	this.previous = previous;
    }

    /**
     * Set the next basic block in the list
     * The double chaining is automatically done.
     *
     * @param previous the next node in the list
     */
    public void setNext(BasicBlock next) {
	if (next != null)
	    next.previous = this;
	this.next = next;
    }

    /**
     * Insert a basic block before this one
     * The double chaining is automatically done.
     *
     * @param newPrevious the node to insert.
     */
    public void insertBefore(BasicBlock newPrevious) {
	if (previous == null)
	    setPrevious(newPrevious);
	previous.next = newPrevious;
	newPrevious.previous = previous;
	previous = newPrevious;
	newPrevious.next = this;
    }

    /**
     * Insert a basic block after this one
     * The double chaining is automatically done.
     *
     * @param newPrevious the node to insert.
     */
    public void insertAfter(BasicBlock newNext) {
	if (next == null)
	    setNext(newNext);
	next.previous = newNext;
	newNext.next = next;
	next = newNext;
	newNext.previous = this;
    }

    /**
     * Remove this block from the list
     */
    public void removeFromList() {
	if (previous != null) {
	    previous.next = next;
	}
	if (next != null) {
	    next.previous = previous;
	}
    }

    // -------------------------------------------------------------------
    // GENERATION OF INSTRUCTIONS
    // -------------------------------------------------------------------
    /**
     * Regroup instructions (new, invokespecial) in one instruction when
     * it's possible.
     *
     * This method must be call after construct Quadruple
     */
    /*package*/ void simplifyNewInstructions() {
	// The index of the map is a variable register
	// A Value is an index of instruction.
	HashMap map = new HashMap();

	//Loop Invariant:
	// The integer #inst is associated with an integer #var (in #map)  iff :
	//  - there is no use or definition of the variable #var from the
	//     index #inst to the index #current (not included) in the
	//     #insts Vector.
	//  - the instruction at #inst index in the #insts Vector  is
	//     a new instruction which define the #var variable.
	//     (#var = new ...).
	for (int current = 0; current < insts.size(); ++current) {
	    //the invariant is true here

	    QInst currentInst = insts.getInstructionAt(current);
	    if (currentInst instanceof QAssignment) {
		QAssignment assign = (QAssignment) currentInst;
		Integer varDefined = new Integer(((QVar)assign.getDefined().getOperand()).getRegister());
		if (assign.getExpression() instanceof QNew) {
		    map.put(varDefined, new Integer(current));
		} else {
		    map.remove(varDefined);
		}
	    }
	    QOperandBox[] ops = currentInst.getUses();
	    int i = 0;
	    if (currentInst instanceof QVoidMethodCall &&
		((QVoidMethodCall) currentInst).isInvokespecial()) {
		i = 1;
	    }
	    for (; i < ops.length; ++i) {
		if (ops[i].getOperand() instanceof QVar) {
		    Integer varUsed = new Integer(((QVar)ops[i].getOperand()).getRegister());
		    map.remove(varUsed);
		}
	    }
	    if (currentInst instanceof QVoidMethodCall &&
		((QVoidMethodCall) currentInst).isInvokespecial()) {
		QVoidMethodCall init = (QVoidMethodCall) currentInst;
		if (ops[0].getOperand() instanceof QVar) {
		    int var = ((QVar) ops[0].getOperand()).getRegister();
		    Integer instNew = (Integer) map.get(new Integer(var));
		    if (instNew != null &&
			insts.getInstructionAt(instNew.intValue()) instanceof QAssignment) {
			//the variable is not used from the new instruction
			QVar varDefined = (QVar) ((QAssignment) insts.getInstructionAt(instNew.intValue())).getDefined().getOperand();
			QNew newInst = (QNew) ((QAssignment) insts.getInstructionAt(instNew.intValue())).getExpression();
			insts.replaceInstruction(instNew.intValue(), new QNop());
			insts.replaceInstruction(current, new QAssignment(varDefined, new QNewInitialized(newInst.getClassConstant(), init.getReferenceConstant(), init.getUses())));
		    }
		}
	    }
	}

    }


    /**
     * Simplify consecutive jumps
     * This must be done just before generation.
     */
    /*package*/ void simplifyJumps() {
	if (insts.size() == 0)
	    return;
	QInst last = insts.getLastInstruction();
	if (last instanceof QAbstractJumpInst) {
	    ((QAbstractJumpInst)last).simplifyAllJumps();
	}
    }

    /**
     * Generate classfile instructions for the basic block associated.
     *
     * Initialize attributes firstInstGen and nbGen.
     *
     * @param codeGen  code generator
     * @param out variable lived out the basic block
     * @param nbVar number of variables used in the method
     */
    public void generate(CodeGeneratorMethod codeGen, BitSet out, int nbVar) {
	CodeGeneratorBasicBlock codeBB = new CodeGeneratorBasicBlock(out, nbVar);
	codeBB.setCurrentBasicBlock(this);
	java.util.Iterator it = getInstructions();
	while (it.hasNext()) {
	    QInst inst = (QInst) it.next();
	    inst.generateInstructions(codeBB);
	}

	codeBB.stackSchedule();

	codeGen.setCurrentBasicBlock(this);
	firstInstGen = codeGen.currentIndex();
	codeBB.generateInstructions(codeGen);
	nbGen = codeGen.currentIndex() - firstInstGen;

	//Without code gen for a basic block.
	/*
	CodeGeneratorBasicBlock codeBB = new CodeGeneratorBasicBlock(nonLocals);
	firstInstGen = codeGen.currentIndex();
	codeGen.setCurrentBasicBlock(this);
	java.util.Iterator it = getInstructions();
	while (it.hasNext()) {
	    QInst inst = (QInst) it.next();
	    inst.generateInstructions(codeGen);
	}
	nbGen = codeGen.currentIndex() - firstInstGen;*/
    }

    /**
     * Generate 3-adress code for the basic block from the source code
     *
     * @param insts source instructions
     */
    public void constructQuadruple(Instruction[] insts, QuadrupleGenerator genQ) {
	genQ.setStack(entryStack);

	if (catchBlock && firstInst < 0) {
	    //save the variable in a new variable and put it on the stack
	    genQ.addInitException(this);
 	}
	boolean firstSubRoutineInstructionIsStore = false;
	if (firstBlockSubroutine) {
	    Instruction first = insts[firstInst];
	    firstSubRoutineInstructionIsStore = first instanceof LocalVarInstruction &&
		((LocalVarInstruction) first).isStore();
	    if (!firstSubRoutineInstructionIsStore) {
		genQ.addInitSubroutine(this);
	    }
	}
	if (firstInst >= 0) {
	    int i = firstInst;
	    if (catchBlock) { // the store instruction is in the block
		genQ.addInitException(this, insts[i++]);
	    }
	    if (firstSubRoutineInstructionIsStore) {
		genQ.addInitSubroutine(this, insts[i++]);
	    }
	    for (; i < lastInst; ++i) {
		if (genQ.generate(insts[i], insts[i+1], this))
		    ++i;
	    }
	    if (i <= lastInst) {
		genQ.generate(insts[lastInst], null, this);
		//we add a goto if the last instruction is not a jump
		// and can complete.
		Instruction lastAdded = insts[lastInst];
		if (lastAdded.canComplete() &&
		    !(lastAdded instanceof JumpInstruction ||
		      lastAdded instanceof SwitchInstruction)) {
		    genQ.addJump(this);
		}
	    } else {
		genQ.addJump(this);
	    }
	} else {
	    genQ.addJump(this);
	}

	//we remove the last instruction (a jump)
	QInst jump = removeLastInstruction();

	QOperand[] outStack = genQ.getStack();
	BasicBlock[] nexts = getNextBasicBlocks();
	for (int i = 0; i < nexts.length; ++i) {
	    if (nexts[i].entryStack == null) {
		genQ.initEntryStack(nexts[i], outStack);
	    }
	    genQ.addVariableConversionInstruction(this, outStack,
						  nexts[i].entryStack);
	}

	addInstruction(jump);
    }



    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    //the default next basic block in the control flow graph
    protected Edge defaultNext;
    protected Edge conditionNext;

    //the 3-adress instructions of the block.
    protected QInstArray insts;

    //the phi instructions
    protected QInstArray phis;


    // the index of the first instruction of the basic block in the array
    protected int firstInst;
    // the index of the last instruction of the basic block in the array
    protected int lastInst;

    //the index of the first instruction generated
    protected int firstInstGen;
    //the number of instructions generated
    protected int nbGen;

    // the previous block in the trace
    protected BasicBlock previous;
    // the next block in the trace
    protected BasicBlock next;

    // is this block the first block of a subroutine
    protected boolean firstBlockSubroutine;
    // is this block a catch block
    protected boolean catchBlock;

    //the entry stack of the block
    protected QOperand[] entryStack;
}

/**
 * Allow to use two iterators like one
 */
class DoubleIterator implements Iterator {
    public DoubleIterator(Iterator it1, Iterator it2) {
	this.it1 = it1;
	this.it2 = it2;
    }
    public boolean hasNext() {
	return it1.hasNext() || it2.hasNext();
    }
    public Object next() {
	if (it1.hasNext()) {
	    return it1.next();
	}
	return it2.next();
    }
    public void remove() {
	if (it1.hasNext()) {
	    it1.remove();
	} else {
	    it2.remove();
	}
    }
    private Iterator it1;
    private Iterator it2;

}
