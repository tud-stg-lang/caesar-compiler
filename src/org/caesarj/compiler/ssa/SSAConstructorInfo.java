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
 * $Id: SSAConstructorInfo.java,v 1.2 2004-02-09 17:33:54 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.caesarj.classfile.ClassfileConstants2;

/**
 * Class containing informations for a variables during SSA transformation
 */
public class SSAConstructorInfo {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the SSA constructor information
     *
     * @param ssaConstructor the SSA Constructor
     * @param var the variable index.
     */
    public SSAConstructorInfo(SSAConstructor ssaConstructor, int var) {
	this.ssaConstructor = ssaConstructor;
	this.var = var;
	this.definitionBlocks = new HashSet();
	int graphSize = ssaConstructor.getBasicBlockArray().length;
	this.usesOperandBox = new LinkedList[graphSize];
	this.phis = new QPhi[graphSize];
	for (int i = 0; i < usesOperandBox.length; ++i) {
	    usesOperandBox[i] = new LinkedList();
	}
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the variable register
     */
    public int getVariableRegister() {
	return var;
    }

    /**
     * Add a definition block for the variable
     *
     * @param def definition block
     */
    public void addDefinitionBlock(BasicBlock def) {
	definitionBlocks.add(new Integer(def.getIndex()));
    }

    /**
     * Add a use of a variable in the list for a given basic block
     * A use can be here a definition.
     *
     * @param bb use block
     * @param operand operand where the variable is used
     */
    public void addUse(BasicBlock bb, QOperandBox operand) {
	LinkedList uses = usesOperandBox[bb.getIndex()];
	uses.add(operand);
    }

    /**
     * Get all uses (and definition) of a variable in a basic block
     *
     * @param bb the basic block
     * @param the list of QOperandBox for the variable
     */
    public Iterator getUsesAtBlock(BasicBlock bb) {
	return usesOperandBox[bb.getIndex()].iterator();
    }

    /**
     * Get a set of index blocks which define the variable
     */
    public Set getDefinitionBlocks() {
	return definitionBlocks;
    }

    /**
     * Get the phi function defined for a basic block
     *
     * @param bb the basic block
     */
    public QPhi getPhiAtBlock(BasicBlock bb) {
	return phis[bb.getIndex()];
    }

    /**
     * Add a phi catch for a given basic block
     *
     * @param bb block
     */
    public void addPhiCatch(BasicBlock bb) {
	if (phis[bb.getIndex()] != null) return;
	//the type is not used, the rigth type of all operands,
	// will be defined when passing operands to SSA form.
	QVar variable = new QVar(var, ClassfileConstants2.TYP_REFERENCE);
	phis[bb.getIndex()] = new QPhiCatch(variable);
    }

    /**
     * Add a phi return for a given basic block
     *
     * @param bb block
     * @param s the concerned sub-routine
     */
    public void addPhiReturn(BasicBlock bb, SubRoutine s) {
	if (phis[bb.getIndex()] != null) return;
	//the type is not used, the rigth type of all operands,
	// will be defined when passing operands to SSA form.
	QVar variable = new QVar(var, ClassfileConstants2.TYP_REFERENCE);
	phis[bb.getIndex()] = new QPhiReturn(variable, s);
    }

    /**
     * Add a phi join for a given basic block
     *
     * @param bb block
     */
    public void addPhiJoin(BasicBlock bb) {
	if (phis[bb.getIndex()] != null) return;
	//the type is not used, the rigth type of all operands,
	// will be defined when passing operands to SSA form.
	QVar variable = new QVar(var, ClassfileConstants2.TYP_REFERENCE);
	phis[bb.getIndex()] = new QPhiJoin(variable, bb);
    }

    /**
     * Remove the phi function for a given basic block
     *
     * @param bb the basic block
     */
    public void removePhiAtBlock(BasicBlock bb) {
	phis[bb.getIndex()] = null;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected int var;
    protected Set definitionBlocks; //index of definition blocks
    protected LinkedList[] usesOperandBox;//uses for each block
    protected SSAConstructor ssaConstructor;
    protected QPhi[] phis; //phi function for each block
}
