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
 * $Id: Graph.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.Iterator;
import java.util.Vector;

/**
 * Represent a graph.
 *
 * A graph contain graph nodes (GraphNode class)
 */
public class Graph {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a new graph
     */
    public Graph() {
	nodes = new Vector();
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Add a node in the graph
     *
     * @param node node to add
     */
    public void addNode(Node node) {
	nodes.addElement(node);
    }

    /**
     * remove a node from the graph
     *
     * @param node node to remove
     */
    public void removeNode(Node node) {
	nodes.removeElement(node);
    }

    /**
     * Return an array containing all node of the graph
     *
     * @return an array containing all node of the graph
     */
    public Node[] getNodes() {
	Node[] nodeArray = new Node[nodes.size()];
	nodes.toArray(nodeArray);
	return nodeArray;
    }

    /**
     * Set the correct index of all nodes in the array of nodes
     */
    public void setNodesIndex() {
	for (int i = 0; i < nodes.size(); ++i) {
	    ((Node) nodes.elementAt(i)).setIndex(i);
	}
    }

    /**
     * Visit all the graph in depth first search
     * Begin the exploration by start node.
     *
     * @param start the virst node to visit.
     * @param nodeVisitor the node visitor
     */
    public void visitGraph(Node start, NodeVisitor nodeVisitor) {
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    Node n = (Node) it.next();
	    n.setMarked(false);
	}
	visitNode(start, nodeVisitor);
	it = nodes.iterator();
	while (it.hasNext()) {
	    Node n = (Node) it.next();
	    if (!n.getMarked()) {
		visitNode(n, nodeVisitor);
	    }
	}
    }

    /**
     * Visit a graph in depth first search from a given node
     *
     * @param node the search source
     * @param nodeVisitor the node visitor
     */
    public void visitGraphFromNode(Node start, NodeVisitor nodeVisitor) {
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    Node n = (Node) it.next();
	    n.setMarked(false);
	}
	visitNode(start, nodeVisitor);
    }

    /**
     * Visit a graph in depth first search from a given node
     *
     * @param node the search source
     * @param nodeVisitor the node visitor
     */
    private boolean visitNode(Node node, NodeVisitor nodeVisitor) {
	node.setMarked(true);
	if (!nodeVisitor.visit(node)) return false;
	Iterator it = node.getSuccessors();
	while (it.hasNext()) {
	    Node n = (Node) it.next();
	    if (!n.getMarked()) {
		if (!visitNode(n, nodeVisitor)) return false;
	    }
	}
	return true;
    }

    /**
     * Get the number of nodes in the graph
     */
    public int size() {
	return nodes.size();
    }
    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Vector nodes;
}

