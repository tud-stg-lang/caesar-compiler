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
 * $Id: DumpTypesVisitor.java,v 1.4 2005-01-21 18:16:25 aracic Exp $
 */

package org.caesarj.compiler.typesys.visitor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.caesarj.compiler.Log;
import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class DumpTypesVisitor implements ICaesarTypeVisitor {
	
	private final CaesarTypeGraph g;
	private final TreeSet s;
	
	public DumpTypesVisitor(CaesarTypeGraph g) {
		this.g = g;
		
		s = new TreeSet(
			new Comparator(){
				public int compare(Object o1, Object o2) {
					return 
						((CaesarTypeNode)o1).getQualifiedName().toString().compareTo(
							((CaesarTypeNode)o2).getQualifiedName().toString()
						);
				}
			}
		);
	}
	
	public void visitCaesarTypeNode(CaesarTypeNode n) {
		s.add(n);
		
		for (Iterator it = n.inners(); it.hasNext();)
			((OuterInnerRelation)it.next()).getInnerNode().accept(this);
	}
	
	public void run() {
    	for (Iterator it = g.getTopClassRoot().iterator(); it.hasNext();) {
    		((CaesarTypeNode)it.next()).accept(this);
		}

    	StringBuffer sb = new StringBuffer();
    	for (Iterator it = s.iterator(); it.hasNext();) {
			sb.append(it.next()+"\n\n");			
		}
    	
    	Log.verbose("========== Caesar Type Graph ==========\n"+sb.toString()+"\n\n");
    	
    	s.clear();
	}
}
