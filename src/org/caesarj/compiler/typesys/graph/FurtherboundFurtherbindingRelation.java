package org.caesarj.compiler.typesys.graph;

/**
 * represents a furtherbound <-> furtherbinding relation
 * 
 * @author Ivica Aracic
 */
public class FurtherboundFurtherbindingRelation extends BidirectionalRelation {
	public FurtherboundFurtherbindingRelation(CaesarTypeNode furtherboundNode, CaesarTypeNode furtherbindingNode) {
		this(false, furtherboundNode, furtherbindingNode);
	}

	public FurtherboundFurtherbindingRelation(boolean implicit, CaesarTypeNode furtherboundNode, CaesarTypeNode furtherbindingNode) {
		super(implicit, furtherboundNode, furtherbindingNode);
		furtherboundNode.addFurtherboundBy(this);
		furtherbindingNode.addFurtherbindingFor(this);
	}
		
	public CaesarTypeNode getFurtherboundNode() {
		return getFirst();
	}

	public CaesarTypeNode getFurtherbindingNode() {
		return getSecond();
	}
}
