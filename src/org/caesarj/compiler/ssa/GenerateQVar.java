/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
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
 * $Id: GenerateQVar.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.Stack;
import java.util.Vector;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.classfile.LocalVarInstruction;
/**
 * Class used to generate quadruple variable.
 * Simulate the stack to generate stack variables.
 * Stack variables are all different, to resolve problem of life analysis.
 *
 * Keep all QVar not to generate multiple instance for the same variable (
 *  if the type of the variable is not change).
 */
public class GenerateQVar {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Create a generator of quadruple instructions.
     *
     * @param nbSourceVar number of local variables used in the source code
     */
    public GenerateQVar(int nbSourceVar) {
	this.stackIndex = nbSourceVar;
	this.vars = new Vector();
	this.stack = new Stack();
    }


    //--------------------------------------------------------------
    // VARIABLES GENERATION
    //--------------------------------------------------------------
    /**
     * Get the variable with the index specified
     *
     * @param index index of the variable
     * @param type the type of the variable
     */
    public QVar getVar(int index, byte type) {
	if (index >= vars.size()) {
	    for (int i = vars.size(); i < index; ++i) {
		vars.addElement(null);
	    }
	    vars.addElement(new QVar(index, type));
	}
	QVar var = (QVar) vars.elementAt(index);
	if (var == null || var.getType() != type) {
	    var = new QVar(index, type);
	    vars.set(index, var);
	}
	return var;
    }

    /**
     * Get the variable corresponding to a classfile instruction
     * of load or store.
     *
     * @param inst the classfile instruction
     */
    public QVar getVar(LocalVarInstruction inst) {
	return getVar(inst.getIndex(), inst.getReturnType());
    }

    /**
     * Get a new local variable
     *
     * @param type the type of the variable
     */
    public QVar getNewVar(byte type) {
	QVar var = new QVar(stackIndex++, type);
	if (type == ClassfileConstants2.TYP_LONG ||
	    type == ClassfileConstants2.TYP_DOUBLE) {
	    stackIndex++;
	}
	return var;
    }

    /**
     * Get a stack variable corresponding to an instruction which
     * add an element on the stack
     *
     * @param type the type of the element put on the stack
     */
    public QVar push(byte type) {
	QVar var = new QVar(stackIndex++, type);
	if (type == ClassfileConstants2.TYP_LONG ||
	    type == ClassfileConstants2.TYP_DOUBLE) {
	    stackIndex++;
	}
	stack.push(var);
	return var;
    }

    /**
     * Put a variable on the stack.
     *
     * @param element the element to put on the stack.
     */
    public QOperand push(QOperand element) {
	stack.push(element);
	return element;
    }

    /**
     * Get the number of variables used
     *
     * @return the number of variables used
     */
    public int getVarNumber() {
	return stackIndex;
    }

    //--------------------------------------------------------------
    // STACK MANIPULATION
    //--------------------------------------------------------------
    /**
     * Get the stack variable corresponding to an instruction which
     * remove an element from the stack.
     */
    public QOperand pop() {
	return (QOperand) stack.pop();
    }

    /**
     * Simulate the instruction pop2 on the stack.
     */
    public void pop2() {
	QOperand var = (QOperand) stack.pop();
	if (var.getType() != ClassfileConstants2.TYP_LONG &&
	    var.getType() != ClassfileConstants2.TYP_DOUBLE) {
	    stack.pop();
	}
    }

    /**
     * Simulate the instruction dup on the stack
     */
    public void dup() {
	QOperand var = (QOperand) stack.peek();
	stack.push(var);
    }

    /**
     * Simulate the instruction dup2 on the stack
     */
    public void dup2() {
	QOperand var1 = (QOperand) stack.pop();
	if (var1.getType() != ClassfileConstants2.TYP_LONG &&
	    var1.getType() != ClassfileConstants2.TYP_DOUBLE) {

	    QOperand var2 = (QOperand) stack.pop(); //we can replace this two
	    stack.push(var2);        // instruction by a peek.

	    stack.push(var1);
	    stack.push(var2);
	} else {
	    stack.push(var1);
	}
	stack.push(var1);
    }

    /**
     * Simulate the instruction dup_x1 on the stack
     */
    public void dup_x1() {
	QOperand var1 = (QOperand) stack.pop();
	QOperand var2 = (QOperand) stack.pop();
	stack.push(var1);
	stack.push(var2);
	stack.push(var1);
    }

    /**
     * Simulate the instruction dup_x2 on the stack
     */
    public void dup_x2() {
	QOperand var1 = (QOperand) stack.pop();
	QOperand var2 = (QOperand) stack.pop();
	if (var2.getType() != ClassfileConstants2.TYP_LONG &&
	    var2.getType() != ClassfileConstants2.TYP_DOUBLE) {
	    QOperand var3 = (QOperand) stack.pop();
	    stack.push(var1);
	    stack.push(var3);
	} else {
	    stack.push(var1);
	}
	stack.push(var2);
	stack.push(var1);
    }

    /**
     * Simulate the instruction dup2_x1 on the stack
     */
    public void dup2_x1() {
	QOperand var1 = (QOperand) stack.pop();
	if (var1.getType() != ClassfileConstants2.TYP_LONG &&
	    var1.getType() != ClassfileConstants2.TYP_DOUBLE) {

	    QOperand var2 = (QOperand) stack.pop();
	    QOperand var3 = (QOperand) stack.pop();
	    stack.push(var2);
	    stack.push(var1);
	    stack.push(var3);
	    stack.push(var2);
	} else {
	    QOperand var2 = (QOperand) stack.pop();
	    stack.push(var1);
	    stack.push(var2);
	}
	stack.push(var1);
    }

    /**
     * Simulate the instruction dup2_x2 on the stack
     */
    public void dup2_x2() {
	QOperand var1 = (QOperand) stack.pop();
	if (var1.getType() != ClassfileConstants2.TYP_LONG &&
	    var1.getType() != ClassfileConstants2.TYP_DOUBLE) {

	    QOperand var2 = (QOperand) stack.pop();
	    QOperand var3 = (QOperand) stack.pop();

	    if (var3.getType() != ClassfileConstants2.TYP_LONG &&
		var3.getType() != ClassfileConstants2.TYP_DOUBLE) {

		QOperand var4 = (QOperand) stack.pop();

		stack.push(var2);
		stack.push(var1);
		stack.push(var4);
		stack.push(var3);
		stack.push(var2);
		stack.push(var1);
	    } else {
		stack.push(var2);
		stack.push(var1);
		stack.push(var3);
		stack.push(var2);
		stack.push(var1);
	    }
	} else {

	    QOperand var2 = (QOperand) stack.pop();

	    if (var2.getType() != ClassfileConstants2.TYP_LONG &&
		var2.getType() != ClassfileConstants2.TYP_DOUBLE) {

		QOperand var3 = (QOperand) stack.pop();

		stack.push(var1);
		stack.push(var3);
		stack.push(var2);
		stack.push(var1);
	    } else {
		stack.push(var1);
		stack.push(var2);
		stack.push(var1);
	    }
	}
    }

    /**
     * Simulate the instruction swap on the stack
     */
    public void swap() {
	QOperand var1 = (QOperand) stack.pop();
	QOperand var2 = (QOperand) stack.pop();

	stack.push(var1);
	stack.push(var2);
    }

    /**
     * Get the stack variable corresponding on the top of the stack
     */
    public QOperand peek() {
	return (QOperand) stack.peek();
    }

    /**
     * Get the size of the stack
     */
    public int getStackSize() {
	return stack.size();
    }

    /**
     * Put all elements from the stack in an array, and remove
     * all elements from the stack.
     */
    public QOperand[] removeElements() {
	QOperand[] elts = new QOperand[stack.size()];
	stack.toArray(elts);
	stack.setSize(0);
	return elts;
    }

    /**
     * Get a variable on the stackwith a given index
     *
     * @param index index of the variable searched
     * @return the variable is found, null else
     */
    public QVar findVariableOnStack(int index) {
	for (int i = 0; i < stack.size(); ++i) {
	    QOperand tmp = (QOperand) stack.elementAt(i);
	    if (tmp instanceof QVar) {
		QVar var = (QVar) tmp;
		if (var.getRegister() == index)
		    return var;
	    }
	}
	return null;
    }

    /**
     * Replace a variable on the stack by a new operand
     *
     * @param index index of the variable to replace
     * @param op new operand which replace the variable
     */
    public void replaceVar(int index, QOperand op) {
	for (int i = 0; i < stack.size(); ++i) {
	    QOperand tmp = (QOperand) stack.elementAt(i);
	    if (tmp instanceof QVar) {
		QVar var = (QVar) tmp;
		if (var.getRegister() == index) {
		    stack.setElementAt(op, i);
		}
	    }
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected int stackIndex;
    protected Vector vars;
    protected Stack stack;
}
