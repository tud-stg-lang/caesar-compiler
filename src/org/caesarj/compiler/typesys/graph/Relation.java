package org.caesarj.compiler.typesys.graph;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public abstract class Relation {
	private boolean implicit;
	private CaesarTypeNode first;
	private CaesarTypeNode second;
	
	public Relation(CaesarTypeNode outerNode, CaesarTypeNode innerNode) {
		this(false, outerNode, innerNode);
	}

	public Relation(boolean implicit, CaesarTypeNode first, CaesarTypeNode second) {
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
		Relation other = (Relation)o;
		return other.first==first && other.second==second && other.implicit==implicit;
	}
	
	public int hashCode() {
		return 
			first.hashCode() 
			* second.hashCode()
			* new Boolean(implicit).hashCode();
	}
}
