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
 * $Id: DominatorTreeNode.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Iterator;

/**
 * A node in the dominator tree.
 * This node is associed with a node of the original graph.
 */
public class DominatorTreeNode extends Node {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a node linked to the node in the original graph.
     *
     * @param index the index of the node
     * @param node the node in the original graph.
     */
    public DominatorTreeNode(int index, Node node) {
	super();
	this.index = index;
	this.graphNode = node;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Return the node in the original graph
     *
     * @return node in the original graph.
     */
    public Node getNode() {
	return graphNode;
    }

    /**
     * Return the index of the node
     *
     * @return index of the node
     */
    public int getIndex() {
	return index;
    }

    /**
     * Test if a children of this node has i as index
     *
     * @param i index searched
     */
    public boolean hasChildIndex(int i) {
	Iterator succ = getSuccessors();
	while (succ.hasNext()) {
	    DominatorTreeNode s = (DominatorTreeNode) succ.next();
	    if (s.getIndex() == i) {
		return true;
	    }
	}
	return false;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected int index;
    protected Node graphNode;
}
