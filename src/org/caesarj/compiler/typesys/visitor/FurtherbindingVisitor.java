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
 * $Id: FurtherbindingVisitor.java,v 1.2 2005-01-21 18:16:25 aracic Exp $
 */

package org.caesarj.compiler.typesys.visitor;

import java.util.Iterator;

import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.FurtherboundFurtherbindingRelation;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.graph.SuperSubRelation;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FurtherbindingVisitor implements ICaesarTypeVisitor {
	
	private final CaesarTypeGraph g;
	
	public FurtherbindingVisitor(CaesarTypeGraph g) {
		this.g = g;        
	}
	
	public void visitCaesarTypeNode(CaesarTypeNode n) {        
		
		for (Iterator it = n.parents(); it.hasNext();) {
			CaesarTypeNode parent = ((SuperSubRelation)it.next()).getSuperNode();
			
			for (Iterator it2 = parent.inners(); it2.hasNext();) {
				CaesarTypeNode furtherbound = ((OuterInnerRelation)it2.next()).getInnerNode();
				
				CaesarTypeNode furtherbinding = n.lookupInner(furtherbound.getQualifiedName().getIdent());
				if(furtherbinding != null) {
					new FurtherboundFurtherbindingRelation(furtherbound, furtherbinding);
				}
			}
		}
        
		for (Iterator it = n.inners(); it.hasNext();)
			((OuterInnerRelation)it.next()).getInnerNode().accept(this);
	}
	
	public void run() {
    	for (Iterator it = g.getTopClassRoot().iterator(); it.hasNext();) {
    		((CaesarTypeNode)it.next()).accept(this);
		}
	}	
}
