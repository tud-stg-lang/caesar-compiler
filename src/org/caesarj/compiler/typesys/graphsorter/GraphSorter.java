package org.caesarj.compiler.typesys.graphsorter;

import java.util.ArrayList;
import java.util.List;

public class GraphSorter {
	protected Node rootNode;
	protected List<Node> sortedNodes = null;

	public void setRoot(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	@SuppressWarnings("serial")
	public static class CycleFoundException extends RuntimeException { }
	
	abstract static public class Node {
		protected List<Node> targets = null;
		protected List<Node> sources = null;
		boolean added = false;
		boolean sorting = false;
		boolean sorted = false;
		
		abstract public List<Node> calcTargets();
		
		public List<Node> getTargets() {
			if (targets == null) {
				targets = calcTargets();
			}
			return targets;
		}
		
		public void buildInverse() {
			if (sources == null) {
				sources = new ArrayList<Node>();
				for (Node target: getTargets()) {
					target.buildInverse();
					target.addSource(this);
				}
			}
		}
		
		public void addSource(Node n) {
			sources.add(n);
		}
		
		public void add(List<Node> sortedLst) {
			if (!added) {
				added = true;
				for (Node src: sources) {
					src.add(sortedLst);
				}
				sortedLst.add(this);				
			}
		}
		
		public void sort(List<Node> sortedLst) {
			if (!sorted) {
				if (sorting) {
					throw new CycleFoundException();
				}
				sorting = true;
				add(sortedLst);
				for (Node target: getTargets()) {
					target.sort(sortedLst);
				}
				sorted = true;
			}
		}
	}
	
	public List<Node> getSortedNodes() {
		if (sortedNodes == null) {
			rootNode.buildInverse();
			sortedNodes = new ArrayList<Node>();
			rootNode.sort(sortedNodes);
		}
		return sortedNodes;
	}
}
