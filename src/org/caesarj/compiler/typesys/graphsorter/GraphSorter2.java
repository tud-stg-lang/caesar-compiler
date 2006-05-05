package org.caesarj.compiler.typesys.graphsorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Linearizes (sorts) given acyclic graph using a modified deep-first search
 * 		- if cycle is detected, exception is thrown	
 * 
 * @author vaidas
 */
public class GraphSorter2 {
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
	public static class CycleFoundException extends RuntimeException {
		protected String nodeName;
		
		public CycleFoundException(String nodeName) {
			this.nodeName = nodeName;
		}
		
		public String getNodeName() {
			return nodeName;
		}
	}
	
	/**
	 *  The node of the graph. Must be connected to a concrete data model by
	 *  deriving a subclass and implementing the calcTargets() method
	 */
	abstract static public class Node {
		/* outgoing nodes (computed on-demand) */
		protected List<Node> outgoing = null;
		
		/* incoming nodes (computed as part of algorithm) */
		protected List<Node> incoming = null;

		/* sorting process of the node started */
		boolean sorting = false;
		
		/* sorting process of the node finished */
		boolean sorted = false;
				
		/**
		 *	The node name to be displayed in messages
		 */
		abstract public String getDisplayName();
		
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
				Collections.reverse(outgoing);
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
		 * Sort the node, append to the given list
		 */
		public void sort(List<Node> sortedLst) {
			if (!sorted) {
				/* called inside sorting process -> cycle */
				if (sorting) {
					throw new CycleFoundException(getDisplayName());
				}
				
				sorting = true;
				
				/* recurse to the outgoing nodes */
				for (Node target: getOutgoing()) {
					target.sort(sortedLst);
				}
				
				sortedLst.add(this);
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
			rootNode.sort(sortedNodes);
			Collections.reverse(sortedNodes);
		}
		return sortedNodes;
	}
}
