package org.caesarj.compiler.typesys.visitor;

import org.caesarj.compiler.typesys.graph.CaesarTypeNode;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public interface ICaesarTypeVisitor {
	void visitCaesarTypeNode(CaesarTypeNode n);
}
