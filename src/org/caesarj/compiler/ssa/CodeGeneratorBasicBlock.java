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
 * $Id: CodeGeneratorBasicBlock.java,v 1.2 2004-02-09 17:33:54 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.BitSet;
import java.util.Stack;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.classfile.IincInstruction;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.NoArgInstruction;

/**
 * To generate classfile instructions for a basic block
 * Use a stack scheduling to generate dup instructions (use the idea
 * of Philip Koopman).
 *
 * This generation can be used iff the stack heigth is 0 (or 1 for a block
 * which catch an exception or a subroutine) at the entrance of
 * the basic block.
 */
public class CodeGeneratorBasicBlock extends CodeGenerator {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the code generator
     *
     * @param out variable lived out the basic block
     * @param nbLocals local variables number
     */
    public CodeGeneratorBasicBlock(BitSet out, int nbLocals) {
	super();
	this.nbLocalVars = nbLocals;
	this.out = out;
	this.first = new Inst(null, new StackElement(-1, 0), 0);
	this.last = this.first;
	this.stack = new Stack();
	//to never have an empty stack.
	stack.push(new StackElement(-1, 0));
	//for a catch block or a sub routine
	stack.push(new StackElement(-1, 1));
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Add an new instruction
     *
     * @param inst the instruction to add.
     */
    public void addInstruction(Instruction inst) {
	//Construct a list of instruction
	//Add each instruction at the end of the list
	//Compute the stack before executing the instruction.
	//Each element as a size and if it's a variable, its index.
	if (inst instanceof LocalVarInstruction) {
	    LocalVarInstruction lvi = (LocalVarInstruction) inst;
	    if (lvi.isLoad()) {
		Inst newInst = new Inst(lvi, (StackElement)stack.peek(),
					stack.size());
		last.setNext(newInst);
		last = newInst;

		StackElement var = new StackElement(lvi.getIndex(),
						    lvi.getPushedOnStack());
		stack.push(var);
		return;
	    } else if (lvi.isStore()) {
		StackElement var = (StackElement) stack.pop();
		var.setRegister(lvi.getIndex());

		Inst newInst = new Inst(lvi, var, stack.size() + 1);
		last.setNext(newInst);
		last = newInst;

		return;
	    }

	}
	//if it is not a load or a store.
	Inst newInst = new Inst(inst, (StackElement) stack.peek(),
				stack.size());
	last.setNext(newInst);
	last = newInst;

	int nbPopped = inst.getPoppedFromStack();

	// Because getPoppedFromStack is wrong for the jsr instruction
	// in kopi
	if (inst.getOpcode() == ClassfileConstants2.opc_jsr)
	    nbPopped = 0;
	// End

	newInst.minStackHeight = newInst.stackHeight - nbPopped;

	while (nbPopped > 0) {
	    StackElement elt = (StackElement) stack.pop();
	    nbPopped -= elt.getSize();
	}
	int nbPushed = inst.getPushedOnStack();
	if (nbPushed > 0) {
	    stack.push(new StackElement(-1, nbPushed));
	}
    }

    /**
     * Generate the instructions of the basic block.
     *
     * @param codeGen code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	Inst currentInst = first;
	while (currentInst.hasNext()) {
	    currentInst = currentInst.getNext();
	    Instruction inst = currentInst.inst;
	    if (inst.getOpcode() != ClassfileConstants2.opc_nop) {
		if (currentInst.dead && currentInst.isStore()) {
		    //store (dead) --> pop
		    if (currentInst.stackTop.getSize() == 2) {
			codeGen.addInstruction(new NoArgInstruction(ClassfileConstants2.opc_pop2));
		    } else {
			codeGen.addInstruction(new NoArgInstruction(ClassfileConstants2.opc_pop));
		    }
		    if (ControlFlowGraph.DEBUG) {
			System.out.println("Code Generator : store (dead)");
		    }
		} else if (currentInst.inst.getOpcode() == ClassfileConstants2.opc_dup &&
			   currentInst.hasNext() &&
			   currentInst.getNext().dead &&
			   currentInst.getNext().isStore()) {
		    //dup; store (dead) --> nop
		    //nothing to generate
		    if (ControlFlowGraph.DEBUG) {
			System.out.println("Code Generator : dup ; store (dead");
		    }
		    currentInst = currentInst.getNext();
		} else {
		    codeGen.addInstruction(inst);
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // STACK SCHEDULING
    // -------------------------------------------------------------------

    /**
     * Use the idea of Philip Koopman to generate dup instructions
     */
    public void stackSchedule() {
	if (first == last)
	    return;

	ListUseReuse list = new ListUseReuse();
	//we search all load (skeep the first instruction)
	for (Inst currentInst = first.getNext(); currentInst != last;
	     currentInst = currentInst.getNext()) {
	    if (currentInst.isLoad()) {
		//we search an instruction which have on the top
		// of the stack, the searched variable.
		int var = currentInst.getIndex();
		int dist = 1;
		for (Inst use = currentInst.getPrev(); use != first;
		     use = use.getPrev(), ++dist) {
		    if (use.stackTop.sameVariable(var)) {
			list.insertUseReuse(use, currentInst, dist);
			break;
		    }
		}
	    }
	}

	//process for each pair use/reuse
	UseReuse currentUR = list.getFirst();
	UseReuse lastUR = list.getLast();
	for ( ; currentUR != lastUR; currentUR = currentUR.next) {
	    Inst use = currentUR.use;
	    Inst reuse = currentUR.reuse;//this must be a load
	    if (!reuse.isLoad())
		continue;
	    int var = reuse.getIndex();
	    if (use.stackTop.sameVariable(var) &&
		use.stackHeight == reuse.stackHeight + 1) {
		Inst ins = use;

		if (!(ins.minStackHeight < reuse.stackHeight ||
		      (ins.inst instanceof IincInstruction &&
		       ((IincInstruction) ins.inst).getVariable() == var))) {
		    ins = ins.getNext();
		    //the instruction between use and reuse must not
		    // modify the variable and the stack height must
		    // not be less than reuse.stackHeight.
		    //this instruction musn't be a dup with the stack
		    // height equal to reuse.stackHeight
		    for ( ; ins != reuse; ins = ins.getNext()) {
			if (ins.minStackHeight < reuse.stackHeight ||
			    (ins.isStore() && ins.getIndex() == var) ||
			    (ins.inst instanceof IincInstruction &&
			     ((IincInstruction) ins.inst).getVariable() == var) ||
			    (ins.minStackHeight == reuse.stackHeight  &&
			     ins.isDup())) {
			    break;
			}
		    }
		}
		if (ins == reuse) {//stack scheduling
		    //we insert a dup before the use
		    // and the reuse become a nop instruction
		    Instruction dup;
		    int size = use.stackTop.getSize();
		    if (size == 1) {
			dup = new NoArgInstruction(ClassfileConstants2.opc_dup);
		    } else {
			dup = new NoArgInstruction(ClassfileConstants2.opc_dup2);
		    }
		    //in fact, I replace the use instruction by a dup,
		    // and I insert a new instruction ('use instruction')
		    // after. I do this because the use instruction can
		    // be in an other pair use/reuse with a higher distance.
		    Inst newInst = new Inst(use.inst, use.stackTop,
					    use.stackHeight + 1);
		    use.insertAfter(newInst);
		    use.inst = dup;

		    // reuse is now a nop instruction
		    reuse.inst = new NoArgInstruction(ClassfileConstants2.opc_nop);

		    //modify the stack height between the two instructions
		    for (ins = newInst; ins != reuse; ins = ins.getNext()) {
			ins.stackHeight++;
			ins.minStackHeight++;
		    }
		    reuse.stackHeight++;
		    reuse.minStackHeight++;
		    reuse.stackTop = use.stackTop;
		}
	    }
	}

	markDeadInstructions();
    }

    /**
     * Mark the dead instructions.
     */
    protected void markDeadInstructions() {
	/*
	 * This array shows for each variable the type
	 * of the last (by begin from the end) instruction
	 * which uses this variable.
	 * This array is initialized to NON_USED (0).
	 */
	byte[] lastInstruction = new byte[nbLocalVars*2];

	//for all variable lived out the basic block
	// the last instruction is a load.
	for (int register = 0; register < lastInstruction.length; ++register) {
	    if (out.get(register)) {
		lastInstruction[register] = LAST_LOAD;
	    }
	}


	for (Inst currentInst = last; currentInst != first;
	     currentInst = currentInst.getPrev()) {
	    if (currentInst.isLoad()) {
		int register = currentInst.getIndex();
		if (lastInstruction[register] == NON_USED) {
		    currentInst.dead = true;
		}
		lastInstruction[register] = LAST_LOAD;
	    } else if (currentInst.isStore()) {
		int register = currentInst.getIndex();
		if (lastInstruction[register] == LAST_STORE ||
		    lastInstruction[register] == NON_USED) {
		    currentInst.dead = true;
		}
	    } else if (currentInst.isRet()) {
		int register = currentInst.getIndex();
		lastInstruction[register] = LAST_LOAD;
	    } else if (currentInst.inst instanceof IincInstruction) {
		int register = ((IincInstruction) currentInst.inst).getVariable();
		lastInstruction[register] = LAST_LOAD;
	    }
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected BitSet out;
    protected int nbLocalVars;
    protected Stack stack;
    protected Inst first;
    protected Inst last;

    //constants used to mark instructions as dead.
    private final static byte NON_USED = 0;
    private final static byte LAST_LOAD = 1;
    private final static byte LAST_STORE = 2;

    // -------------------------------------------------------------------
    // INNER CLASS
    // -------------------------------------------------------------------
    /**
     * A handle to an instruction
     * Allow to link instruction.
     * Keep the element on the top of the stack, and the height of this one
     *  when this instruction is executed.
     * minStackHeight represent the minimum height of the stack during
     *  the execution of the instruction.
     */
    protected class Inst {
	/**
	 * Construction a new instruction handle
	 *
	 * @param inst the instruction
	 * @param stackTop element on the top of the stack
	 * @param stackHeight height of the stack.
	 */
	public Inst(Instruction inst, StackElement stackTop, int stackHeight) {
	    this.inst = inst;
	    this.stackTop = stackTop;
	    this.stackHeight = stackHeight;
	    this.dead = false;
	    this.minStackHeight = stackHeight;
	}

	/**
	 * Set the next instruction in the list
	 */
	public void setNext(Inst next) {
	    if (next != null)
		next.prev = this;
	    this.next = next;
	}

	/**
	 * Get the next instruction in the list
	 */
	public Inst getNext() {
	    return next;
	}

	/**
	 * Get the previous instruction in the list
	 */
	public Inst getPrev() {
	    return prev;
	}

	/**
	 * Insert an instruction before this one in the list
	 */
	public void insertBefore(Inst newPrev) {
	    newPrev.prev = prev;
	    newPrev.next = this;
	    this.prev.next = newPrev;
	    this.prev = newPrev;
	}

	/**
	 * Insert an instruction after this one in the list
	 */
	public void insertAfter(Inst newNext) {
	    newNext.prev = this;
	    newNext.next = next;
	    this.next.prev = newNext;
	    this.next = newNext;
	}

	/**
	 * Test if this is not the last instruction of the list
	 */
	public boolean hasNext() {
	    return next != null;
	}

	/**
	 * Test if the instruction is a dup
	 */
	public boolean isDup() {
	    return inst.getOpcode() == ClassfileConstants2.opc_dup ||
		inst.getOpcode() == ClassfileConstants2.opc_dup2;
	}

	/**
	 * Test if the instruction is a load instruction
	 */
	public boolean isLoad() {
	    return (inst instanceof LocalVarInstruction &&
		    ((LocalVarInstruction) inst).isLoad());
	}

	/**
	 * Test if the instruction is a store instruction
	 */
	public boolean isStore() {
	    return (inst instanceof LocalVarInstruction &&
		    ((LocalVarInstruction) inst).isStore());
	}

	/**
	 * Test if the instruction is a ret instruction
	 */
	public boolean isRet() {
	    return inst.getOpcode() == ClassfileConstants2.opc_ret;
	}

	/**
	 * Get the index of the variable used by the instruction
	 *
	 * Precondition : isStore() || isLoad() || isRet()
	 */
	public int getIndex() {
	    return ((LocalVarInstruction) inst).getIndex();
	}
	public Instruction inst;
	public StackElement stackTop;
	public int stackHeight;
	public int minStackHeight;
	//is this instruction dead.
	public boolean dead;

	protected Inst prev;
	protected Inst next;
    }

    /**
     * To represent an element on the stack
     *
     * register : index of the variable if the element is a variable
     *            -1 else
     * size : size of the element.
     */
    protected class StackElement {
	/**
	 * Construct a stack element
	 *
	 * @param register index of the variable if the element is a variable
	 *                 -1 else
	 * @param size size of the element.
	 */
	public StackElement(int register, int size) {
	    this.register = register;
	    this.size = size;
	}
	/**
	 * Change the index of the variable
	 */
	public void setRegister(int register) {
	    this.register = register;
	}
	/**
	 * Get the index of the variable
	 */
	public int getRegister() {
	    return register;
	}
	/**
	 * Get the size of the element
	 */
	public int getSize() {
	    return size;
	}
	/**
	 * Test if the element is a variable of index <code>var</code>
	 */
	public boolean sameVariable(int var) {
	    if (this.register == -1) {
		return false;
	    }
	    return var == register;
	}
	protected int register;
	protected int size;
    }

    /**
     * To manipulate a list of pair use/reuse
     * The list is sorted by distance.
     */
    protected class ListUseReuse {
	/**
	 * Construct a new list
	 */
	public ListUseReuse() {
	    first = new UseReuse();
	    last = first;
	}
	/**
	 * Insert a new pair use/reuse in the list
	 *
	 * @param use use instruction
	 * @param reuse reuse instruction
	 */
	public void insertUseReuse(Inst use, Inst reuse, int dist) {
	    UseReuse current = first;
	    while (current.dist < dist) {
		current = current.next;
	    }
	    UseReuse newUseReuse = new UseReuse(use, reuse, dist);
	    current.insertBefore(newUseReuse);
	    if (current == first) {
		first = newUseReuse;
	    }
	}
	/**
	 * Get the first element of the list
	 */
	public UseReuse getFirst() {
	    return first;
	}
	/**
	 * Get the last element of the list
	 * Rem : this is an element to mark the end
	 *  of the list, this is not a real pair use/reuse.
	 */
	public UseReuse getLast() {
	    return last;
	}
	protected UseReuse first;
	protected UseReuse last;
    }
    /**
     * Represent a pair use/reuse
     *
     * Pair are linked to form a list.
     *
     * Keep the distance between the two instructions.
     */
    protected class UseReuse {
	/**
	 * Construct an empty pair with the maximum distance
	 */
	public UseReuse() {
	    this(null, null, Integer.MAX_VALUE);
	}
	/**
	 * Construct a pair
	 *
	 * @param use use instruction
	 * @param reuse reuse instruction
	 * @param dist distance between the two instructions
	 */
	public UseReuse(Inst use, Inst reuse, int dist) {
	    this.use = use;
	    this.reuse = reuse;
	    this.dist = dist;
	}
	/**
	 * Insert an object UseReuse before this one in the list
	 *
	 * @param newPrev the pair to insert before this one
	 */
	public void insertBefore(UseReuse newPrev) {
	    newPrev.prev = this.prev;
	    newPrev.next = this;
	    if (this.prev != null) {
		this.prev.next = newPrev;
	    }
	    this.prev = newPrev;
	}
	public Inst use;
	public Inst reuse;
	public int dist;

	public UseReuse prev;
	public UseReuse next;
    }
}

