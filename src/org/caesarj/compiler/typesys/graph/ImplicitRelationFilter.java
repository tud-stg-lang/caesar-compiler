package org.caesarj.compiler.typesys.graph;

import java.util.Iterator;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class ImplicitRelationFilter implements Iterator {

	private boolean ignoreImplicit;
	private Iterator it;
	private Relation nextRelation = null;
	private boolean hasNext;
	
	public ImplicitRelationFilter(Iterator it, boolean ignoreImplicit) {
		this.it = it;
		this.ignoreImplicit = ignoreImplicit;
		searchNextValidElement();
	}
	
	private void searchNextValidElement() {
		hasNext = false;
		while(it.hasNext()) {
			Relation rel = (Relation)it.next();
			if((rel.isImplicit() && !ignoreImplicit) || (!rel.isImplicit() && ignoreImplicit)) {
				hasNext = true;
				nextRelation = rel;
				break;
			} 
		}
	}
	
	public boolean hasNext() {
		return hasNext;
	}
	
	public Object next() {
		Object res = nextRelation;
		searchNextValidElement();
		return res;
	}
	
	public void remove() {
		it.remove();
	}
}
