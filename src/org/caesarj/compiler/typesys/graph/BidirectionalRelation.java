package org.caesarj.compiler.typesys.graph;

/**
 * base class for all bidirectional relations
 * 
 * @author Ivica Aracic
 */
public abstract class BidirectionalRelation {
	private boolean implicit;
	private CaesarTypeNode first;
	private CaesarTypeNode second;
	
	public BidirectionalRelation(CaesarTypeNode outerNode, CaesarTypeNode innerNode) {
		this(false, outerNode, innerNode);
	}

	public BidirectionalRelation(boolean implicit, CaesarTypeNode first, CaesarTypeNode second) {
		this.implicit = implicit;
		this.first  = first;
		this.second = second;
	}
		
	protected CaesarTypeNode getFirst() {
		return first;
	}

	protected CaesarTypeNode getSecond() {
		return second;
	}

	public boolean isImplicit() {
		return implicit;
	}
	
	public boolean equals(Object o) {
		BidirectionalRelation other = (BidirectionalRelation)o;
		return other.first==first && other.second==second && other.implicit==implicit;
	}
	
	public int hashCode() {
		return 
			first.hashCode() 
			* second.hashCode()
			* new Boolean(implicit).hashCode();
	}
}
