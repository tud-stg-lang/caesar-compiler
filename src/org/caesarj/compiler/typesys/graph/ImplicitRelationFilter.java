/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: ImplicitRelationFilter.java,v 1.3 2005-01-21 18:16:25 aracic Exp $
 */

package org.caesarj.compiler.typesys.graph;

import java.util.Iterator;


/**
 * Iterates over a collection of Relation objects.
 * Can filter out implicit/explicit relations.
 * 
 * @author Ivica Aracic
 */
public class ImplicitRelationFilter implements Iterator {

	private boolean ignoreImplicit;
	private Iterator it;
	private BidirectionalRelation nextRelation = null;
	private boolean hasNext;
	
	public ImplicitRelationFilter(Iterator it, boolean ignoreImplicit) {
		this.it = it;
		this.ignoreImplicit = ignoreImplicit;
		searchNextValidElement();
	}
	
	private void searchNextValidElement() {
		hasNext = false;
		while(it.hasNext()) {
			BidirectionalRelation rel = (BidirectionalRelation)it.next();
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
