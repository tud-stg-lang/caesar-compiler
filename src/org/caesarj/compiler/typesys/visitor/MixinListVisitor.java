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
 * $Id: MixinListVisitor.java,v 1.6 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.typesys.visitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.graph.SuperSubRelation;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.mixer.Linearizator;
import org.caesarj.mixer.MixerException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class MixinListVisitor implements ICaesarTypeVisitor {
	
	private final CaesarTypeGraph g;
	private Set visited = new HashSet();
	
	public MixinListVisitor(CaesarTypeGraph g) {
		this.g = g;        
	}
	
	public void visitCaesarTypeNode(CaesarTypeNode n) {        
		if(visited.contains(n)) return;
		visited.add(n);
        
        List outerMixinList = null;
        
        CaesarTypeNode outer = n.getOuter();
        
        if(outer != null) {
            outer.accept(this);
            outerMixinList = outer.getMixinList();
        }
        
        createMixinList(n.getMixinList(), outerMixinList, 0, n.getQualifiedName());        
        
		for (Iterator it = n.inners(); it.hasNext();)
			((OuterInnerRelation)it.next()).getInnerNode().accept(this);
	}
	
    private void createMixinList(    
        List mixinListToAppend,
        List outerMixinList, 
        int m,         
        JavaQualifiedName qualifiedName        
    ) {    	    
        CaesarTypeNode currentMixin;
        CaesarTypeNode t;

        if(outerMixinList != null) {
            currentMixin = (CaesarTypeNode)outerMixinList.get(m);
            t = currentMixin.lookupDeclaredInner(qualifiedName.getIdent());
        }
        else {
            t = g.getType(qualifiedName);
        }
        
        if(t != null) {
            mixinListToAppend.add(t);
            
            List superClassMixinLists = new LinkedList();
            
            if(t.isFurtherbinding()) {
            	LinkedList s = new LinkedList();
                createMixinList(s, outerMixinList, m+1, qualifiedName);
                superClassMixinLists.add(s);
            }
            
            for (Iterator it = t.declaredParents(); it.hasNext();) {
                CaesarTypeNode superType = ((SuperSubRelation)it.next()).getSuperNode();
                LinkedList l = new LinkedList();
                createMixinList(l, outerMixinList, 0, superType.getQualifiedName());
                superClassMixinLists.add(l);
            }
            
            if(superClassMixinLists.size() > 0) {
                try {
                    // C3 Linearization
                    List mergedList = Linearizator.instance().mixFromLeftToRight(
                		(List[])superClassMixinLists.toArray(new List[superClassMixinLists.size()])
					);
                    
                    // append
                    for(Iterator it=mergedList.iterator(); it.hasNext(); ) {
                        mixinListToAppend.add(it.next());
                    }
                }
                catch (MixerException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (m < outerMixinList.size()-1) {
            createMixinList(mixinListToAppend, outerMixinList, m+1, qualifiedName);
        }
    }

    public void run() {
    	for (Iterator it = g.getTopClassRoot().iterator(); it.hasNext();) {
    		((CaesarTypeNode)it.next()).accept(this);
		}
    	
    	visited.clear();
	}
}
