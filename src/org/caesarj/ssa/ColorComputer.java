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
 * $Id: ColorComputer.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * To color SSA Variables with an interence graph.
 */
public class ColorComputer {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a color computer with the interference graph
     *
     * @param interference the interference graph.
     */
    public ColorComputer(InterferenceGraph interference) {
	this.interference = interference;
	this.ssaVars = SSAVar.getAllSSAVariables();
	this.interferenceVarColors = new BitSet[this.ssaVars.size()];
	for (int i = 0; i < interferenceVarColors.length; ++i) {
	    interferenceVarColors[i] = new BitSet();
	}
	this.variables = new QVar[interferenceVarColors.length];
    }

    // -------------------------------------------------------------------
    // COLOR METHOD
    // -------------------------------------------------------------------
    /**
     * Color all variables
     */
    public void color() {
	/*
	 * find all precolored variables and add interference in the graph
	 */
	Iterator vars = ssaVars.iterator();
	while (vars.hasNext()) {
	    SSAVar var = (SSAVar) vars.next();
	    if (var.isColored()) {
		setColor(var, var.getColor());
	    }
	}

	/*
	 * color all non colored variables
	 */
	vars = ssaVars.iterator();
	while (vars.hasNext()) {
	    SSAVar var = (SSAVar) vars.next();
	    if (!var.isColored()) {
		setColor(var, findEmptySpace(var.getUniqueIndex(), var.getSize()));
	    }
	}

	for (int i = 0; i < ssaVars.size(); ++i ) {
	    SSAVar var = (SSAVar) ssaVars.elementAt(i);
	    if (ControlFlowGraph.DEBUG) {
		System.out.println(" ssa var " + var.getUniqueIndex() + " : color " + var.getColor());
	    }
	}

    }

    /**
     * Set the color for a variable
     *
     * Maintain the interence graph up to date
     *
     * @param var the variable
     * @param color the color
     */
    public void setColor(SSAVar var, int color) {
	//set the color
	var.setColor(color);
	int x = var.getUniqueIndex();

	//set the color to interference with all variables
	// that interfere with var
	if (var.getSize() == 1) {
	    Iterator varInterference = interference.interfereFor(x);
	    while (varInterference.hasNext()) {
		int y = ((Integer)varInterference.next()).intValue();
		interferenceVarColors[y].set(color);
	    }
	} else {
	    Iterator varInterference = interference.interfereFor(x);
	    while (varInterference.hasNext()) {
		int y = ((Integer)varInterference.next()).intValue();
		interferenceVarColors[y].set(color);
		interferenceVarColors[y].set(color + 1);
	    }
	}
    }

    /**
     * find the minimum index of <code>size<code> consecutive non intefere
     * colors  for a given variable.
     *
     * @param var the variable
     * @param size the size of consecutive non interfere colors
     * @return the minimum index of size empty space.
     */
    protected int findEmptySpace(int var, int size) {
	int find = 0;
	int begin = -1;
	BitSet set = interferenceVarColors[var];
	for (int i = 0; i < set.size(); ++i) {
	    if (set.get(i)) {
		find = 0;
	    } else {
		if (find == 0) {
		    begin = i;
		}
		++find;
		if (find == size) {
		    return begin;
		}
	    }
	}
	return set.size() - find;
    }

    // -------------------------------------------------------------------
    // NON SSA VARIABLE GENERATOR
    // -------------------------------------------------------------------
    /**
     * Get the variable associated with a SSA Variable after coloring
     *
     * @param var the SSA Variable
     * @return variable in non SSA form
     */
    public QVar getVariable(SSAVar var) {
	int index = var.getUniqueIndex();

	//we also create a new variable if the type has changed
	if (variables[index] == null ||
	    variables[index].getType() != var.getType()) {
	    variables[index] = new QVar(var.getColor(), var.getType());
	}
	return variables[index];
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected InterferenceGraph interference;
    protected Vector ssaVars;
    protected BitSet[] interferenceVarColors;
    protected QVar[] variables;
}
