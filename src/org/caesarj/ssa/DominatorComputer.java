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
 * $Id: DominatorComputer.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.ssa;

import java.util.Set;
import java.util.BitSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Calculate the dominators in a graph using Lengauer and Tarjan algorithm
 * (advanced Compiler Design Implementation - Steven S. Muchnick)
 *
 * Computation of dominance frontier and iterated dominance frontier are done
 * with the algorithm presented by S. Muchnick.
 */
public class DominatorComputer {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a new dominator calculator
     *
     * @param nodes nodes of the graph
     * @param start the start node
     * @param end the end node
     */
    public DominatorComputer(Node[] nodes, Node start) {
	this.nodes = nodes;

	n0 = nodes.length;
	label = new int[n0 + 1]; //elements of all arrays are 0.
	parent = new int[n0 + 1];
	ancestor = new int[n0 + 1];
	child = new int[n0 + 1];
	ndfs = new int[n0 + 1];
	sdno = new int[n0 + 1];
	size = new int[n0 + 1];
	bucket = new Set[n0 + 1];
	idom = new int[n0 + 1];
	for (int i = 0; i < n0 + 1; ++i) {
	    bucket[i] = new HashSet();
	}
	ancestor[n0] = n0;
	label[n0] = n0;
	n = 0;

	depthFirstSearchDom(getNodeIndex(start));
	for (int i = n; i >= 1; --i) {
	    //compute initial values for semidominators and store
	    //nodes with the same semidominator in the same bucket
	    int w = ndfs[i];
	    Iterator pred = nodes[w].getPredecessors();
	    while(pred.hasNext()) {
		int v = getNodeIndex((Node) pred.next());
		int u = eval(v);
		if (sdno[u] < sdno[w]) {
		    sdno[w] = sdno[u];
		}
	    }
	    bucket[ndfs[sdno[w]]].add(new Integer(w));
	    link(parent[w],w);
	    //compute immediate dominators for nodes in the bucket
	    // of w's parent
	    Iterator itBucket = bucket[parent[w]].iterator();
	    while (itBucket.hasNext()) {
		int v = ((Integer) itBucket.next()).intValue();
		itBucket.remove();
		int u = eval(v);
		if (sdno[u] < sdno[v]) {
		    idom[v] = u;
		} else {
		    idom[v] = parent[w];
		}
	    }
	}
	// adjust immediate dominators of nodes whose current version is
	// the immediate dominator differs from the node with the
	// depth-first number of the node's semidominator
	for (int  i = 1; i <= n; ++i) {
	    int w = ndfs[i];
	    if (idom[w] != ndfs[sdno[w]]) {
		idom[w] = idom[idom[w]];
	    }
	}
	//all array not useful
	label = null;
	parent = null;
	ancestor = null;
	child = null;
	ndfs = null;
	sdno = null;
	size = null;
	bucket = null;

	//construct the dominator tree.

	//construct the nodes of the dominator tree
        treeNodes = new DominatorTreeNode[nodes.length];
	for (int i = 0; i < treeNodes.length; ++i) {
	    treeNodes[i] = new DominatorTreeNode(i, nodes[i]);
	}
	//add edges in the tree
	for (int i = 0; i < idom.length - 1; ++i) {
	    if (idom[i] != n0 && idom[i] != i) {
		treeNodes[idom[i]].addSuccessor(new TreeEdge(), treeNodes[i]);
	    }
	}
	treeRoot = treeNodes[getNodeIndex(start)];

	//compute the dominance frontier
	computeDomFront();
    }

    // -------------------------------------------------------------------
    // DOMINATOR ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get an array of immediate dominator
     */
    public int[] getIDom() {
	return idom;
    }

    /**
     * Get the graph nodes in post order in the dominator tree
     *
     * @return nodes in post order
     */
    public int[] postOrder() {
	n = 0;
	int[] postOrder = new int[nodes.length];
	depthFirstDominatorTree(treeRoot, postOrder, true);
	return postOrder;
    }

    /**
     * Get the graph nodes in pre order in the dominator tree
     *
     * @return nodes in pre order
     */
    public int[] preOrder() {
	n = 0;
	int[] preOrder = new int[nodes.length];
	depthFirstDominatorTree(treeRoot, preOrder, false);
	return preOrder;
    }

    /**
     * Get the dominance frontier for a given node
     *
     * @param i index of the node
     */
    public Set getDF(int i) {
	return domFront[i];
    }

    /**
     * Compute the iterated dominance frontier of a flowgraph
     *
     * @param nodes a set of nodes (Integer)
     * @return iterated dominance frontier
     */
    public Set getDFPlus(Set nodes) {
	boolean change = true;
	Set dFP = getDFSet(nodes);
	while (change) {
	    change = false;
	    Set tmp = new HashSet();
	    tmp.addAll(nodes);
	    tmp.addAll(dFP);
	    Set d = getDFSet(tmp);
	    if (!d.equals(dFP)) {
		dFP = d;
		change = true;
	    }
	}
	return dFP;
    }

    /**
     * Get the root of the dominator tree
     */
    public DominatorTreeNode getTreeRoot() {
	return treeRoot;
    }

    // -------------------------------------------------------------------
    // PRIVATE METHODS
    // -------------------------------------------------------------------
    /**
     * Compute the dominance frontier of each node
     */
    private void computeDomFront() {
	domFront = new HashSet[nodes.length];
	for (int i = 0; i < domFront.length; ++i) {
	    domFront[i] = new HashSet();
	}
	int[] p = postOrder();
	for (int i = 0; i < p.length; ++i) {
	    int currentNode = p[i];
	    //compute local component
	    Iterator succ = nodes[currentNode].getSuccessors();
	    while (succ.hasNext()) {
		int y = getNodeIndex((Node) succ.next());
		if (!treeNodes[currentNode].hasChildIndex(y)) {
		    domFront[currentNode].add(new Integer(y));
		}
	    }
	    //add on up component
	    succ = treeNodes[currentNode].getSuccessors();
	    while (succ.hasNext()) {
		int z = ((DominatorTreeNode) succ.next()).getIndex();
		Iterator itDomFront = domFront[z].iterator();
		while (itDomFront.hasNext()) {
		    int y = ((Integer)itDomFront.next()).intValue();
		    if (!treeNodes[currentNode].hasChildIndex(y)) {
			domFront[currentNode].add(new Integer(y));
		    }
		}
	    }
	}
    }

    /**
     * Compute the union of the dominance frontiers of a set of nodes
     *
     * @param nodes the set of nodes
     */
    private Set getDFSet(Set nodes) {
	Set d = new HashSet();
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    int x = ((Integer) it.next()).intValue();
	    d.addAll(domFront[x]);
	}
	return d;
    }

    /**
     * Perform depth-first search and initialize data structure
     *
     * @param node search from this node
     */
    private void depthFirstSearchDom(int node) {
	sdno[node] = ++n;
	ndfs[n] = node;
	label[node] = node;
	ancestor[node] = n0;
	child[node] = n0;
	size[node] = 1;
	Iterator succ = nodes[node].getSuccessors();
	while(succ.hasNext()) {
	    int w = getNodeIndex((Node) succ.next());
	    if (sdno[w] == 0) {
		parent[w] = node;
		depthFirstSearchDom(w);
	    }
	}
    }

    /**
     * Perform depth-first cover of the dominator tree
     *
     * @param treeNode the tree node
     * @param tab array in which the nodes are placed in order.
     * @param postOrder post order (true) pre order (false).
     */
    private void depthFirstDominatorTree(DominatorTreeNode treeNode,
					 int[] tab,
					 boolean postOrder) {
	if (!postOrder) { //pre order
	    tab[n++] = treeNode.getIndex();
	}
	Iterator succ = treeNode.getSuccessors();
	while (succ.hasNext()) {
	    DominatorTreeNode next = (DominatorTreeNode) succ.next();
	    depthFirstDominatorTree(next, tab, postOrder);
	}
	if (postOrder) { //post order
	    tab[n++] = treeNode.getIndex();
	}
    }


    /**
     * determine the ancestor of v whose semidominatorhas the
     * minimal depth-first number
     *
     * @param v the node
     */
    private int eval(int v) {
	if (ancestor[v] == n0) {
	    return label[v];
	} else {
	    compress(v);
	    if (sdno[label[ancestor[v]]] >= sdno[label[v]]) {
		return label[v];
	    } else {
		return label[ancestor[v]];
	    }
	}
    }

    /**
     * Compress ancestor path to node v to the node whose lable has
     * the maximal semidominator number
     *
     * @param v the node
     */
    private void compress(int v) {
	if (ancestor[ancestor[v]] != n0) {
	    compress(ancestor[v]);
	    if (sdno[label[ancestor[v]]] < sdno[label[v]]) {
		label[v] = label[ancestor[v]];
	    }
	    ancestor[v] = ancestor[ancestor[v]];
	}
    }

    /**
     * Find the index of a node in the nodes array
     *
     * @param node a node of the array
     * @return index of the node
     */
    private int getNodeIndex(Node node) {
	return node.getIndex();
    }

    /**
     * Rebalance the forest to trees maintaindes by the child and ancestor
     * data structures
     */
    private void link(int v, int w) {
	int s = w;
	while (sdno[label[w]] < sdno[label[child[s]]]) {
	    if ((size[s] + size[child[child[s]]]) >= 2 * size[child[s]]) {
		ancestor[child[s]] = s;
		child[s] = child[child[s]];
	    } else {
		size[child[s]] = size[s];
		s = ancestor[s] = child[s];
	    }
	}
	label[s] = label[w];
	size[v] += size[w];
	if (size[v] < 2 * size[w]) {
	    int tmp = s;
	    s = child[v];
	    child[v] = tmp;
	}
	while (s != n0) {
	    ancestor[s] = v;
	    s = child[s];
	}
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Node[] nodes;
    //to find index in the nodes array
    protected HashMap map;

    protected int[] label;
    protected int[] parent;
    protected int[] ancestor;
    protected int[] child;
    protected int[] ndfs;
    protected int[] sdno;
    protected int[] size;
    protected int n0;
    protected int n;
    protected Set[] bucket;

    //the immediate dominator
    protected int[] idom;
    //root of the dominator tree
    protected DominatorTreeNode treeRoot;
    //nodes in the dominator tree
    protected DominatorTreeNode[] treeNodes;
    //the dominance frontier of each node
    protected Set[] domFront;
}


/**
 * Edge in the dominator tree
 */
class TreeEdge implements Edge {
    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the origin of the edge
     *
     * @return the origin of the edge
     */
    public Node getSource() {
	return source;
    }

    /**
     * Set the origin of the edge
     *
     * @return the origin of the edge
     */
    public void setSource(Node newSource) {
	newSource = source;
    }

    /**
     * Get the target of the edge
     *
     * @return the target of the edge
     */
    public Node getTarget() {
	return target;
    }

    /**
     * Set the target of the edge
     *
     * @param newSon the new target of the edge
     */
    public void setTarget(Node newTarget) {
	target = newTarget;
    }

    // -------------------------------------------------------------------
    // ATTRIBUTES
    // -------------------------------------------------------------------
    protected Node source;
    protected Node target;
}
