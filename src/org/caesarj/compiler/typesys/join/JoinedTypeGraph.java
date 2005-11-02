package org.caesarj.compiler.typesys.join;

import java.util.HashMap;
import java.util.Map;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.typesys.input.InputTypeGraph;
import org.caesarj.compiler.typesys.input.InputTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * Graph of relationships between Caesar classes
 *     - provides information about class parents, furtherbounds, mixin lists
 * 	   - computed on-demand 
 * 
 * @author vaidas
 *
 */
public class JoinedTypeGraph {
	
	/* map: qualified name to type node */
	protected Map<JavaQualifiedName, JoinedTypeNode> nameToNode 
				= new HashMap<JavaQualifiedName, JoinedTypeNode>();
	
	/* reference to input graph */
	protected InputTypeGraph inputGraph;
	
	/* reference to compiler (for error reporting) */
	protected CompilerBase compiler;
	
	/**
	 * Constructor
	 */
	public JoinedTypeGraph(InputTypeGraph inputGraph, CompilerBase compiler) {
		this.inputGraph = inputGraph;
		this.compiler = compiler;
	}
	
	/**
	 *	Get input type graph 
	 */
	public InputTypeGraph getInputGraph() {
		return inputGraph;
	}
	
	/**
	 *	Get compiler 
	 */
	public CompilerBase getCompiler() {
		return compiler;
	}

	/**
	 * Get type node by qualified name, create on demand 
	 */
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
	
	/**
	 * Get input type node for qualified name (null if not exists) 
	 */
	public InputTypeNode getInputNode(JavaQualifiedName qualName) {
		if (qualName == null)
			return null;
		else
			return inputGraph.getNodeByName(qualName);
	}
}
