package org.caesarj.compiler.typesys.graph;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class SuperSubRelation extends Relation {
	public SuperSubRelation(CaesarTypeNode superNode, CaesarTypeNode subNode) {
		this(false, superNode, subNode);
	}

	public SuperSubRelation(boolean implicit, CaesarTypeNode superNode, CaesarTypeNode subNode) {
		super(implicit, superNode, subNode);
		superNode.addInheritedBy(this);
		subNode.addInheritsFrom(this);
	}
		
	public CaesarTypeNode getSuperNode() {
		return getFirst();
	}
	
	public CaesarTypeNode getSubNode() {
		return getSecond();
	}
}
