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
 * $Id: Node.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.Iterator;
import java.util.Vector;

/**
 * A node of a graph.
 *
 * You can can had information in a node by inheritance
 *
 * Node of the graph are linked by Edge. But we can work as if there
 * was not these.
 *
 * Iterator are defined to browse predecessors or successors whithout
 * concider edges.
 *
 */
public class Node {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a node not linked.
     */
    public Node() {
	predecessors = new Vector();
	successors = new Vector();
	marked = false;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Return an iterator on the predecessors of this node
     *
     * @return predecessors of this node
     */
    public Iterator getPredecessors() {
	return new PredecessorsIterator(predecessors.iterator());
    }

    /**
     * Return an iterator on the successors of this node
     *
     * @return successors of this node
     */
    public Iterator getSuccessors() {
	return new SuccessorsIterator(successors.iterator());
    }

    /**
     * Test if this node has at least a child.
     *
     * @return true iff this node as at least a child.
     */
    public boolean hasSuccessor() {
	return successors.size() != 0;
    }

    /**
     * Get an iterator on the edges which are pointing on this node
     *
     * @return the edges which are pointing on this node
     */
    public Iterator getInEdges() {
	return predecessors.iterator();
    }

    /**
     * Get an iterator on the edges which are going out this node
     *
     * @return the edges which are going out this node
     */
    public Iterator getOutEdges() {
	return successors.iterator();
    }

    /**
     * Get the number of edges which are pionting on this node
     *
     * @return the number of edges which are pionting on this node
     */
    public int getInEdgesNumber() {
	return predecessors.size();
    }

    /**
     * Get the number of edges which are going out this node
     *
     * @return the number of edges which are going out this node
     */
    public int getOutEdgesNumber() {
	return successors.size();
    }

    /**
     * Change the target of an edge
     *
     * Precondition : the source of the edge is this node
     *
     * @param edge the edge to change
     * @param target the new node targe of the edge
     */
    public void changeEdgeTarget(Edge edge, Node target) {
	edge.getTarget().predecessors.removeElement(edge);
	edge.setTarget(target);
	target.predecessors.addElement(edge);
    }

    /**
     * Add a child on this node
     * Target et source of the edge are initialized with this method.
     * This node is automaticaly added as a predecessor of
     * the new successor.
     *
     * @param edge edge between the two nodes.
     * @param node new sucessor.
     * @return the edge between the two nodes.
     */
    public Edge addSuccessor(Edge edge, Node node) {
	edge.setSource(this);
	edge.setTarget(node);
	successors.addElement(edge);
	node.predecessors.addElement(edge);
	return edge;
    }

    /**
     * Remove a child from this node
     *
     * @param node the node to remove.
     */
    public void removeSuccessor(Node node) {
	Iterator outEdges = getOutEdges();
	while (outEdges.hasNext()) {
	    Edge edge = (Edge) outEdges.next();
	    if (edge.getTarget() == node) {
		predecessors.removeElement(edge);
		successors.removeElement(edge);
	    }
	}
    }

    /**
     * mark the node
     */
    public void setMarked(boolean marked) {
	this.marked = marked;
    }

    /**
     * Test if the node is marked
     */
    public boolean getMarked() {
	return marked;
    }

    /**
     * Get the index of the node in the graph
     */
    public int getIndex() {
	return index;
    }

    /**
     * Set the index of the node in the graph
     */
    /* package */ void setIndex(int index) {
	this.index = index;
    }
    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Vector predecessors;
    protected Vector successors;
    protected boolean marked;
    protected int index; //index in the graph.

}

// -------------------------------------------------------------------
// OTHER CLASSES
// -------------------------------------------------------------------
/**
 * An abstract class to be used as an iterator.
 * This class uses an iterator of edges.
 */
abstract class EdgeIterator implements Iterator {
    /**
     * Construction the edge iterator with an iterator
     * containing edges.
     *
     * @param it set of edges.
     */
    protected EdgeIterator(Iterator it) {
	this.itEdges = it;
    }
    public boolean hasNext() {
	return itEdges.hasNext();
    }
    public abstract Object next();
    public void remove() {
    }
    protected Iterator itEdges;
}

/**
 * To iterate on the predecessors of edges
 */
class PredecessorsIterator extends EdgeIterator {
    protected PredecessorsIterator(Iterator it) {
	super(it);
    }
    public Object next() {
	return ((Edge)itEdges.next()).getSource();
    }
}

/**
 * To iterate on the successors of edges
 */
class SuccessorsIterator extends EdgeIterator {
    protected SuccessorsIterator(Iterator it) {
	super(it);
    }
    public Object next() {
	return ((Edge)itEdges.next()).getTarget();
    }
}
