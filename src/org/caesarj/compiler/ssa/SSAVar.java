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
 * $Id: SSAVar.java,v 1.2 2004-02-09 17:33:55 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.caesarj.classfile.ClassfileConstants2;

/**
 * A SSA variable
 *
 * keep the definition and all uses of the variable
 */
public class SSAVar {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a new SSA variable
     *
     * @param sourceIndex the variable index in non SSA form
     * @param type the type of the variable
     * @param count the index of the SSA Variable for this variable
     */
    public SSAVar(int sourceIndex, byte type, int count) {
	this.sourceIndex = sourceIndex;
	this.type = type;
	this.count = count;
	this.uniqueIndex = allSSAVar.size();
	allSSAVar.addElement(this);
	this.uses = new HashSet();
	this.color = -1; //no colored
    }

    // -------------------------------------------------------------------
    // ACCESSORS
    // -------------------------------------------------------------------
    /**
     * Add a use of the SSA Variable
     *
     * @param use the use
     */
    public void addUse(QOperandBox use) {
	uses.add(use);
    }

    /**
     * Remove a use of the SSA Variable
     *
     * @param use to remove
     */
    public void removeUse(QOperandBox use) {
	uses.remove(use);
    }

    /**
     * Set the definition point of the variable
     *
     * @param definition the definition point
     */
    public void setDef(QOperandBox definition) {
	this.definition = definition;
    }

    /**
     * Get the definition point of the variable
     */
    public QOperandBox getDefinition() {
	return definition;
    }

    /**
     * Get all uses of the Variable
     *
     * @return the list of uses
     */
    public Iterator getUses() {
	return uses.iterator();
    }

    /**
     * Test if the variable is used
     */
    public boolean isUsed() {
	return !uses.isEmpty();
    }

    /**
     * Get the type of the variable
     *
     * @return the variable type
     */
    public byte getType() {
	return type;
    }

    /**
     * Get the size of the variable
     *
     * @return the slot size of the variable
     */
    public int getSize() {
	return (type == ClassfileConstants2.TYP_LONG || type == ClassfileConstants2.TYP_DOUBLE)? 2 : 1;
    }

    /**
     * Set the type of the variable
     *
     * @param type the variable type
     */
    public void setType(byte type) {
	this.type = type;
    }

    /**
     * Get the index of the variable in non SSA form
     */
    public int getSourceIndex() {
	return sourceIndex;
    }

    /**
     * Get the index for the variable
     */
    public int getCount() {
	return count;
    }

    /**
     * Get a representation of the variable
     */
    public String toString() {
	return "v{"+sourceIndex+","+count+"}"  + "(" + uniqueIndex + ")";
    }

    /**
     * Get the unique index associated with the ssa variable
     *
     * @return unique index of the ssa variable
     */
    public int getUniqueIndex() {
	return uniqueIndex;
    }

    /**
     * Get the number of SSA variable constructed
     */
    public static int getSSAVariableNumber() {
	return allSSAVar.size();
    }

    /**
     * Set the color of the variable
     *
     * @param int the color number
     */
    public void setColor(int color) {
	this.color = color;
    }

    /**
     * Get the color of the variable
     *
     * @return the color number
     */
    public int getColor() {
	return color;
    }

    /**
     * Test if the variable is colored
     */
    public boolean isColored() {
	return color != -1;
    }


    // -------------------------------------------------------------------
    // STATIC METHODS
    // -------------------------------------------------------------------
    /**
     * Init the generation of SSA Variable number
     */
    public static void init() {
	allSSAVar.clear();
    }

    /**
     * Get all SSA Variables
     */
    public static Vector getAllSSAVariables() {
	return allSSAVar;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected int color;
    protected int sourceIndex;
    protected byte type;
    protected int count;
    protected int uniqueIndex;
    protected Set uses;
    protected QOperandBox definition;

    protected static Vector allSSAVar = new Vector();
}
