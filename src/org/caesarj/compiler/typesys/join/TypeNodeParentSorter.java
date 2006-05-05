package org.caesarj.compiler.typesys.join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caesarj.compiler.typesys.graphsorter.GraphSorter2;

/**
 * Sorts the parent graph of a Caesar class, using a modified deep-first search
 * 
 * @author vaidas
 *
 */
public class TypeNodeParentSorter extends GraphSorter2 {
	
	/* map from type node to internal graph node */
	protected Map<JoinedTypeNode, NodeWrapper> wrapperMap = new HashMap<JoinedTypeNode, NodeWrapper>();
	
	/**
	 * Constructor
	 * 
	 * @param root 	node to be sorted
	 */
	public TypeNodeParentSorter(JoinedTypeNode root) {
		setRoot(wrapTypeNode(root));
	}
	
	/**
	 * Adapts the graph of class parents to the graph of the sorting algorithm 
	 */
	protected class NodeWrapper extends Node {
		/* corresponding type node */
		protected JoinedTypeNode typeNode;
				
		/**
		 * Constructor
		 */
		public NodeWrapper(JoinedTypeNode typeNode) {
			this.typeNode = typeNode;			
		}
		
		/**
		 * Get corresponding type node 
		 */
		public JoinedTypeNode getTypeNode() {
			return typeNode;
		}
		
		/**
		 *	The node name to be displayed in messages
		 */
		public String getDisplayName() {
			return typeNode.getQualifiedName().toString();
		}
		
		/**
		 * Return the parents of the type node as outgoing nodes of the sorting graph
		 */
		public List<Node> calcOutgoingNodes() {
			List<Node> nodes = new ArrayList<Node>();
			for (JoinedTypeNode parent : typeNode.getDirectParents()) {
				nodes.add(wrapTypeNode(parent));
			}
			return nodes;
		}
		
		/**
		 * Display as string, for debugging purposes
		 */
		public String toString() {
			return getDisplayName();
		}
	}
	
	/**
	 *	Get the adapter node for the given type node 
	 */
	public NodeWrapper wrapTypeNode(JoinedTypeNode typeNode) {
		NodeWrapper wrp = wrapperMap.get(typeNode);
		if (wrp == null) {
			wrp = new NodeWrapper(typeNode);
			wrapperMap.put(typeNode, wrp);
		}
		return wrp;
	}
	
	/**
	 *	Get the sorted list of the parents 
	 */
	public List<JoinedTypeNode> getSortedTypeNodes() {
		List<JoinedTypeNode> typeNodes = new ArrayList<JoinedTypeNode>();
		for (Node n : getSortedNodes()) {
			typeNodes.add(((NodeWrapper)n).getTypeNode());
		}
		return typeNodes;
	}
}
