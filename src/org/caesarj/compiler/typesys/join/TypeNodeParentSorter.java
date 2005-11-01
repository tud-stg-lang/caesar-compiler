package org.caesarj.compiler.typesys.join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caesarj.compiler.typesys.graphsorter.GraphSorter;

public class TypeNodeParentSorter extends GraphSorter {
	
	protected Map<JoinedTypeNode, NodeWrapper> wrapperMap = new HashMap<JoinedTypeNode, NodeWrapper>();
	
	public TypeNodeParentSorter(JoinedTypeNode root) {
		setRoot(wrapTypeNode(root));
	}
	
	protected class NodeWrapper extends Node {
		protected JoinedTypeNode typeNode;
				
		public NodeWrapper(JoinedTypeNode typeNode) {
			this.typeNode = typeNode;			
		}
		
		public JoinedTypeNode getTypeNode() {
			return typeNode;
		}
		
		public List<Node> calcTargets() {
			List<Node> nodes = new ArrayList<Node>();
			for (JoinedTypeNode parent : typeNode.getDirectParents()) {
				nodes.add(wrapTypeNode(parent));
			}
			return nodes;
		}
		
		public String toString() {
			return typeNode.getQualifiedName().toString();
		}
	}
	
	public NodeWrapper wrapTypeNode(JoinedTypeNode typeNode) {
		NodeWrapper wrp = wrapperMap.get(typeNode);
		if (wrp == null) {
			wrp = new NodeWrapper(typeNode);
			wrapperMap.put(typeNode, wrp);
		}
		return wrp;
	}
	
	public List<JoinedTypeNode> getSortedTypeNodes() {
		List<JoinedTypeNode> typeNodes = new ArrayList<JoinedTypeNode>();
		for (Node n : getSortedNodes()) {
			typeNodes.add(((NodeWrapper)n).getTypeNode());
		}
		return typeNodes;
	}
}
