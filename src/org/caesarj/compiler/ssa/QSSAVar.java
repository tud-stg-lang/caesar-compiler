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
 * $Id: QSSAVar.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.util.InconsistencyException;


/**
 * A class to represent SSA variable for quadruple instructions.
 *
 */
public class QSSAVar extends QOperand {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Create a new variable
     * A SSA variable can only be created with the static methods
     *  newSSAVarUse and newSSAVarDefinition.
     *
     * @param var SSA variable
     */
    protected QSSAVar(SSAVar var) {
	this.ssaVar = var;
    }

    /**
     * Construct a new use of a SSA variable
     *
     * I add the type of the variable here because we can't know
     * the type of the target of a phi function at the definition
     * point. So I set the type when using it.
     *
     * @param dest where the variable will be used
     * @param var the SSA variable
     * @param type the type of the variable
     */
    public static QSSAVar newSSAVarUse(QOperandBox dest, SSAVar var, byte type) {
	QSSAVar qvar = new QSSAVar(var);
	var.setType(type);
	var.addUse(dest);
	dest.setOperand(qvar);
	return qvar;
    }

    /**
     * Construct the definition point of a SSA variable
     *
     * @param dest where the variable will be defined
     * @param var the SSA variable
     */
    public static QSSAVar newSSAVarDefinition(QOperandBox dest, SSAVar var) {
	QSSAVar qvar = new QSSAVar(var);
	var.setDef(dest);
	dest.setOperand(qvar);
	return qvar;
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Get the unique index of the SSA Variable
     */
    public int getUniqueIndex() {
	return ssaVar.getUniqueIndex();
    }

    /**
     * Get the ssa var
     */
    public SSAVar getSSAVar() {
	return ssaVar;
    }

    /**
     * Test if the operand may throw an exception
     */
    public boolean mayThrowException() {
	return false;
    }

    /**
     * Return the type of the operand.
     */
    public byte getType() {
	return ssaVar.type;
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this operand
     *
     * @param codeGen the code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	throw new InconsistencyException("GENERATION FROM SSA FORM");
    }

    /**
     * Generate the classfile instructions for save in
     * this operand the value actually son the stack.
     */
    public void generateStore(CodeGenerator codeGen) {
	throw new InconsistencyException("GENERATION FROM SSA FORM");
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * A representation of the instruction
     */
    public String toString() {
	return ssaVar.toString();
    }
    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected SSAVar ssaVar;
}
