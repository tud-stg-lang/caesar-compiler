package org.caesarj.compiler.typesys.graphsorter;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.util.InconsistencyException;

/**
 * Linearizes (sorts) given acyclic graph using a modified deep-first search
 * 		- if cycle is detected, exception is thrown	
 * 
 * @author vaidas
 */
public class GraphSorter {
	protected Node rootNode;
	protected List<Node> sortedNodes = null;

	/**
	 * Set the root node, from which sorting should start
	 */
	public void setRoot(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	/**
	 * Exception, thrown when cycle is found, recommended to be handled 
	 */
	@SuppressWarnings("serial")
	public static class CycleFoundException extends RuntimeException { }
	
	/**
	 *  The node of the graph. Must be connected to a concrete data model by
	 *  deriving a subclass and implementing the calcTargets() method
	 */
	abstract static public class Node {
		/* outgoing nodes (computed on-demand) */
		protected List<Node> outgoing = null;
		
		/* incoming nodes (computed as part of algorithm) */
		protected List<Node> incoming = null;
		
		/* node is already added to the sorted list */
		boolean added = false;
		
		/* sorting process of the node started */
		boolean sorting = false;
		
		/* sorting process of the node finished */
		boolean sorted = false;
		
		/**
		 *	Compute the outgoing nodes 
		 */
		abstract public List<Node> calcOutgoingNodes();
		
		/**
		 * Get the outgoing nodes (cached)
		 */
		protected List<Node> getOutgoing() {
			if (outgoing == null) {
				outgoing = calcOutgoingNodes();
			}
			return outgoing;
		}
		
		/**
		 * Build the inverse relationships (compute incoming nodes)
		 */
		public void buildInverse() {
			if (incoming == null) {
				incoming = new ArrayList<Node>();
				for (Node outgoing: getOutgoing()) {
					/* recurse further */
					outgoing.buildInverse();
					/* add itself as incoming node */
					outgoing.addIncoming(this);
				}
			}
		}
		
		/**
		 *	Add incoming node 
		 */
		protected void addIncoming(Node n) {
			incoming.add(n);
		}
		
		/**
		 *	Add itself to the sorted list 
		 */
		protected void add(List<Node> sortedLst, List<Node> pendingLst) {
			sortedLst.add(this);
			added = true;			
			/* try to add the pending nodes */
			for (Node node: pendingLst) {
				if (node.canBeAdded()) {
					pendingLst.remove(node);
					node.add(sortedLst, pendingLst);
					break;
				}
			}			
		}
		
		/**
		 *	Check if can add itself to the sorted list 
		 */
		protected boolean canBeAdded() {
			/* can be added only if all incoming nodes are already added */
			for (Node src: incoming) {
				if (!src.added) {
					return false;
				}				
			}
			return true;
		}
		
		/**
		 * Sort the node, append to the given list
		 */
		public void sort(List<Node> sortedLst, List<Node> pendingLst) {
			if (!sorted) {
				/* called inside sorting process -> cycle */
				if (sorting) {
					throw new CycleFoundException();
				}
				sorting = true;
				/* try add itself to the list */
				if (canBeAdded()) {
					/* add itself */
					add(sortedLst, pendingLst);			
				}
				else {
					/* if cannot be added add itself to the pending list */
					pendingLst.add(this);
				}
				/* recurse to the outgoing nodes */
				for (Node target: getOutgoing()) {
					target.sort(sortedLst, pendingLst);
				}
				sorted = true;
			}
		}
	}
	
	/**
	 * Sort starting from the root node 
	 * Call setRootNode() before
	 */
	public List<Node> getSortedNodes() {
		if (sortedNodes == null) {
			/* compute incoming nodes */
			rootNode.buildInverse();
			/* sort */
			sortedNodes = new ArrayList<Node>();
			List<Node> pendingNodes = new ArrayList<Node>();
			rootNode.sort(sortedNodes, pendingNodes);
			if (pendingNodes.size() != 0) {
				throw new InconsistencyException("Failed to sort all nodes");
			}
		}
		return sortedNodes;
	}
}
