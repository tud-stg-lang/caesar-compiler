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
 * $Id: Propagator.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;


/**
 * To propagate operand inside a basic blocks, to remove temporary
 * variables
 */
public class Propagator {
    // -------------------------------------------------------------------
    // PROPAGATION CALL
    // -------------------------------------------------------------------
    /**
     * Compute de propagation on a list of basic block.
     *
     * @param fbb the first basic block of the trace
     * @param outs out set for each basic block
     */
    public static void propagate(BasicBlock fbb, BitSet[] outs) {
	VarListDefUse vars = new VarListDefUse();
	for (BasicBlock bb = fbb; bb != null; bb = bb.getNext()){
	    QInstArray insts = bb.getInstructionsArray();
	    findDefUse(insts, vars);
	    removeDefUseThrowEdges(vars, outs[bb.getIndex()]);
	    vars.verifyAllLists();
	    propagate(insts, vars);

	}

    }

    // -------------------------------------------------------------------
    // PRIVATE METHODS
    // -------------------------------------------------------------------
    /**
     * Remove from list vars definition that are not used in the
     * the basic block
     *
     * @param vars list of def/use for the basic block
     * @param out variables alive at the out of the basic block
     */
    private static void removeDefUseThrowEdges(VarListDefUse vars,
					       BitSet out) {
	for (int i = 0; i < out.size(); ++i) {
	    if (out.get(i)) {
		//add two uses to be sure the definition
		//will be removed from the list
		vars.addUse(i, -2);
		vars.addUse(i, -2);
	    }
	}
    }


    /**
     * Find the variables with one use for one definition in the
     * list of instructions
     */
    private static void findDefUse(QInstArray insts, VarListDefUse vars) {
	vars.clear();
	for (int index = 0; index < insts.size(); ++index) {
	    QInst inst = insts.getInstructionAt(index);
	    QOperandBox[] ops = inst.getUses();
	    for (int j = 0; j < ops.length; ++j) {
		QOperand op = ops[j].getOperand();
		if (op instanceof QVar) {
		    int r = ((QVar)op).getRegister();
		    vars.addUse(r, index);
		}
	    }
	    if (inst instanceof QAssignment) {
		QOperand op = ((QAssignment)inst).getDefined().getOperand();
		if (op instanceof QVar) {
		    int r = ((QVar)op).getRegister();
		    vars.addDef(r, index);
		}
	    }
	}
    }

    /**
     * Do the propagation when it's possible
     */
    private static void propagate(QInstArray insts, VarListDefUse vars) {
	for (int index = insts.size() - 1; index >= 0; --index) {
	    QOperandBox[] ops = insts.getInstructionAt(index).getUses();
	    propagate(insts, ops, index, index, vars);
	}
    }

    /**
     * Do the propagation for given operands
     *
     * @param insts instructions
     * @param ops operands to propagate if it's possible
     * @param line the line of the instruction with this operands
     * @param lineExpr the line from which the expression is extracted
     */
    private static void propagate(QInstArray insts, QOperandBox[] ops,
				  int line, int lineExpr, VarListDefUse vars) {
	for (int j = ops.length - 1; j >= 0; --j) {
	    QOperand op = ops[j].getOperand();
	    if (op instanceof QVar) {
		int r = ((QVar)op).getRegister();
		if (vars.isUniqUse(r, lineExpr)) {
		    int indexFrom = vars.getDefIndex(r);
		    if (isMovePossible(insts, indexFrom, line)) {
			QExpression expr = moveExpression(insts, indexFrom, ops[j]);
			//we search if a propagation is possible
			// in the expression moved.
			//remember the fact that this expression
			// wasn't first in this position.
			propagate(insts, expr.getUses(), line, indexFrom, vars);
		    }
		}
	    }
	}
    }

    /**
     * Test if the move of an expression is possible
     *
     * Precondition : insts[from] must be a QAssignment
     *
     * @param insts instructions
     * @param from the line where the expression is defined
     * @param to the line in which we want to put the expression
     */
    private static boolean isMovePossible(QInstArray insts, int from, int to) {
	QAssignment inst = (QAssignment) insts.getInstructionAt(from);
	BitSet varUsedInExpr = new BitSet();
	QOperandBox[] ops = inst.getUses();
	for (int i = 0; i < ops.length; ++i) {
	    QOperand op = ops[i].getOperand();
	    if (op instanceof QVar) {
		int r = ((QVar)op).getRegister();
		varUsedInExpr.set(r);
	    }
	}
	if (inst.mayThrowException()) {
	    for (int i = from + 1; i < to; ++i) {
		if (!(insts.getInstructionAt(i) instanceof QNop))
		    return false;
	    }
	    return true;
	} else {
	    for (int i = from + 1; i < to; ++i) {
		QInst interInst = (QInst)insts.getInstructionAt(i);
		if (interInst.mayThrowException()) {
		    return false;
		}
		//a use of a variable cannot be moved after
		// a redefinition of this one.
		if (interInst instanceof QAssignment) {
		    QOperand op = ((QAssignment)interInst).getDefined().getOperand();
		    if (op instanceof QVar) {
			int r = ((QVar)op).getRegister();
			if (varUsedInExpr.get(r)) {
			    return false;
			}
		    }
		}
	    }
	    return true;
	}
    }

    /**
     * Move an expression to a new QOperandBox
     * Remove the origin instruction
     *
     * Precondition : insts[from] must be a QAssignment
     *
     * @param insts instructions
     * @param from the line where the expression is used
     * @param to the operand in wich the expression is put
     * @return the moved expression
     */
    private static QExpression moveExpression(QInstArray insts, int from, QOperandBox to) {
	QAssignment inst = (QAssignment) insts.getInstructionAt(from);
	to.setOperand(inst.getExpression());
	insts.replaceInstruction(from, new QNop());
	return inst.getExpression();
    }

}
// -------------------------------------------------------------------
// NON PUBLIC CLASSES
// -------------------------------------------------------------------
/**
 * List of pair def/use for each variable
 * (1 use for 1 definition).
 */
class VarListDefUse {
    /**
     * Construct the list container
     */
    public VarListDefUse() {
	map = new HashMap();
    }

    /**
     * Verify if the lists are correct
     * The list is not correct if the last definition has no use
     */
    public void verifyAllLists() {
	Iterator lists = map.values().iterator();
	while (lists.hasNext()) {
	    ListDefUse list = (ListDefUse) lists.next();
	    //if the last definition has no use remove it.
	    if (!list.isEmpty() && !list.hasUse()) {
		list.removeFirst();
	    }
	}
    }

    /**
     * Add a define for a given variable
     */
    public void addDef(int register, int index) {
	ListDefUse list = (ListDefUse) map.get(new Integer(register));
	if (list == null) {
	    list = new ListDefUse();
	    map.put(new Integer(register), list);
	} else {
	    //if the last definition has no use.
	    if (!list.isEmpty() && !list.hasUse()) {
		list.removeFirst();
	    }
	}

	list.add(index);
	list.lastActionIsRemove = false;
    }
    /**
     * Add a use for a given variable
     * If there is more than 1 use for a def, the def/use is remove
     * from the list
     */
    public void addUse(int register, int index) {
	ListDefUse list = (ListDefUse) map.get(new Integer(register));
	if (list != null && !list.isEmpty() && !list.lastActionIsRemove) {
	    if (list.hasUse()) {
		//two use, so we remove it
		list.removeFirst();
		list.lastActionIsRemove = true;
	    } else {
		list.setUse(index);
	    }
	}
    }

    /**
     * Test if the use of a variable at a given index is the
     * uniq use of the first pair def/use of the list.
     */
    public boolean isUniqUse(int register, int index) {
	ListDefUse list = (ListDefUse) map.get(new Integer(register));
	if (list != null && !list.isEmpty()) {
	    return list.isUniqUse(index);
	}
	return false;
    }

    /**
     * Get the definition index for a variable
     *
     * Precondition : isUniqUse(register, index) with index
     *                the index of the uses of this variable
     */
    public int getDefIndex(int register) {
	ListDefUse list = (ListDefUse) map.get(new Integer(register));
	return list.removeFirst();
    }
    /**
     * Remove all lists
     */
    public void clear() {
	map.clear();
    }

    protected HashMap map;
}

/**
 * List of pair def/use.
 */
class ListDefUse {
    /**
     * Construct a new list
     */
    public ListDefUse() {
	head = null;
    }
    /**
     * Test if the list is empty.
     */
    public boolean isEmpty() {
	return head == null;
    }
    /**
     * Remove the first def/use of the list
     *
     * Precondition : !isEmpty()
     *
     * @return the definition index of the list head
     */
    public int removeFirst() {
	DefUse first = head;
	head = head.getNext();
	return first.getIndexDef();
    }
    /**
     * Test if an index correspond to the uniq use of
     * the variable.
     */
    public boolean isUniqUse(int index) {
	return head.getIndexUse() == index;
    }
    /**
     * Add a new pair in front of the list
     */
    public void add(int indexDef) {
	head = new DefUse(indexDef, head);
    }
    /**
     * Test if the first def/use of the list has a use
     */
    public boolean hasUse() {
	return head.hasUse();
    }
    /**
     * Define the use index of the first def/usr of the list
     */
    public void setUse(int index) {
	head.setIndexUse(index);
    }

    protected DefUse head;
    public boolean lastActionIsRemove;
}

/**
 * A pair def/use
 */
class DefUse {
    /**
     * Construct a pair with no use.
     *
     * @param defineIndex the index where the variable is define
     * @param next the next couple in the list
     */
    public DefUse(int defineIndex, DefUse next) {
	this.indexDef = defineIndex;
	this.indexUse = -1;
	this.next = next;
    }
    /**
     * Test if the variable is used
     */
    public boolean hasUse() {
	return indexUse != -1;
    }
    /**
     * Set the index where the variable is used
     */
    public void setIndexUse(int indexUse) {
	this.indexUse = indexUse;
    }
    /**
     * Get the index where the variable is used
     *
     * @return the use index, -1 if the variable is not used
     */
    public int getIndexUse() {
	return indexUse;
    }
    /**
     * Get the index where the variable is defined
     */
    public int getIndexDef() {
	return indexDef;
    }

    /**
     * Get the next pair def/use in the list
     */
    public DefUse getNext() {
	return next;
    }

    protected int indexDef;
    protected int indexUse;
    protected DefUse next;
}
