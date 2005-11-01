package org.caesarj.compiler.typesys.join;

import java.util.HashMap;
import java.util.Map;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.typesys.input.InputTypeGraph;
import org.caesarj.compiler.typesys.input.InputTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

public class JoinedTypeGraph {
	
	protected Map<JavaQualifiedName, JoinedTypeNode> nameToNode 
				= new HashMap<JavaQualifiedName, JoinedTypeNode>();
	
	protected InputTypeGraph inputGraph;
	protected CompilerBase compiler;
	
	public JoinedTypeGraph(InputTypeGraph inputGraph, CompilerBase compiler) {
		this.inputGraph = inputGraph;
		this.compiler = compiler;
	}
	
	public InputTypeGraph getInputGraph() {
		return inputGraph;
	}
	
	public CompilerBase getCompiler() {
		return compiler;
	}

	public JoinedTypeNode getNodeByName(JavaQualifiedName qualName) {
		if (qualName == null) {
			return null;
		}
		JoinedTypeNode node = nameToNode.get(qualName);
		if (node == null) {
			node = new JoinedTypeNode(qualName, this);
			nameToNode.put(qualName, node);
		}
		return node;
	}
	
	public InputTypeNode getInputNode(JavaQualifiedName qualName) {
		if (qualName == null)
			return null;
		else
			return inputGraph.getNodeByName(qualName);
	}
}
