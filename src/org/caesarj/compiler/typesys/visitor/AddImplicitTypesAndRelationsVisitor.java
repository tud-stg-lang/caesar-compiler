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
 * $Id: AddImplicitTypesAndRelationsVisitor.java,v 1.5 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.typesys.visitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.graph.SuperSubRelation;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AddImplicitTypesAndRelationsVisitor implements ICaesarTypeVisitor {
	
	private final CaesarTypeGraph g;
	private Set visited = new HashSet();
	
	public AddImplicitTypesAndRelationsVisitor(CaesarTypeGraph g) {
		this.g = g;        
	}
	
	public void visitCaesarTypeNode(CaesarTypeNode n) {
		if(visited.contains(n)) return;
        
        visited.add(n);
        
        // add implicit relations
        for(Iterator it = n.parents(); it.hasNext(); ) {
            CaesarTypeNode superClass = ((SuperSubRelation)it.next()).getSuperNode();
            
            superClass.accept(this);
            
            // add implicit inner types and inheritance relation between virtual and further-binding
            for(Iterator it2 = superClass.inners(); it2.hasNext(); ) {
                CaesarTypeNode virtualType = 
                    ((OuterInnerRelation)it2.next()).getInnerNode();
                
                CaesarTypeNode furtherbindingType = 
                    n.lookupInner(virtualType.getQualifiedName().getIdent());
                
                if(furtherbindingType == null) {
                    // type doesn't exist -> create                	
                	furtherbindingType = g.getTypeCreateIfNotExsistent(
            			new JavaQualifiedName(
        					n.getQualifiedName().toString()+JavaQualifiedName.innerSep+(virtualType.getQualifiedName().getIdent())
						),
						CaesarTypeNode.IMPLICIT
        			);
                	                	
                	new OuterInnerRelation(true, n, furtherbindingType);
                }
            
                new SuperSubRelation(true, virtualType, furtherbindingType);            
            }
            
            // now establish inherited inheritance relations among created inners
            for(Iterator it2 = superClass.inners(); it2.hasNext(); ) {
                CaesarTypeNode virtualType = 
                    ((OuterInnerRelation)it2.next()).getInnerNode();
                
                for (Iterator it3 = virtualType.parents(); it3.hasNext();) {
                    CaesarTypeNode virtualTypeSuper = ((SuperSubRelation)it3.next()).getSuperNode();
                    // we need only inheritance relations inside the enclosing class
                    if(virtualTypeSuper.getOuter() == virtualType.getOuter()) {
                        CaesarTypeNode furtherbindingType = 
                            n.lookupInner(virtualType.getQualifiedName().getIdent());
                        
                        CaesarTypeNode virtualTypeSuperFurtherbinding = 
                            n.lookupInner(virtualTypeSuper.getQualifiedName().getIdent());
                        
                        // connect
                        new SuperSubRelation(true, virtualTypeSuperFurtherbinding, furtherbindingType);
                    }
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
    	
    	visited.clear();
	}
}
