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
 * $Id: InterferenceGraph.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An interference graph
 */
public class InterferenceGraph {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct an interference graph
     *
     * @param size the total number of variables
     */
    public InterferenceGraph(int size) {
	this.size = size;
	matrix = new BitSet[size];
	adjacence = new List[size];
	for (int i = 0; i < size; ++i) {
	    matrix[i] = new BitSet(size);
	    adjacence[i] = new LinkedList();
	}
    }

    // -------------------------------------------------------------------
    // ACCESSORS
    // -------------------------------------------------------------------
    /**
     * Add an interference between a variable and a set of variable
     */
    public void addInterference(int x, BitSet set) {
	//can use here new method from jdk 1.4 to scan a bitset
	for (int i = 0; i < set.size(); ++i) {
	    if (set.get(i)) {
		addInterference(x, i);
	    }
	}
    }

    /**
     * Add an interference between two variables
     *
     * @param x the first variable
     * @param y the second variable
     */
    public void addInterference(int x, int y) {
	if (!matrix[x].get(y)) {
	    matrix[x].set(y);
	    matrix[y].set(x);
	    adjacence[x].add(new Integer(y));
	    adjacence[y].add(new Integer(x));
	}
    }

    /**
     * Test if two variables interfere
     *
     * @param x the first variable
     * @param y the second variable
     * @return true iff x and y interfere
     */
    public boolean interfere(int x, int y) {
	return matrix[x].get(y);
    }

    /**
     * Get the list of all interference for a variable
     *
     * @param x the variable
     */
    public Iterator interfereFor(int x) {
	return adjacence[x].iterator();
    }

    /**
     * Provide a representation of the interfere graph
     */
    public String toString() {
	String tmp = "";
	for (int i = 0; i < size; ++i) {
	    tmp += i + " : " + matrix[i];
	}
	return tmp;
    }


    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected int size;
    protected BitSet[] matrix;
    protected List[] adjacence;
}
