package org.caesarj.compiler.typesys.visitor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

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
		System.out.println("----------------------------------------");
		
    	for (Iterator it = g.getTopClassRoot().iterator(); it.hasNext();) {
    		((CaesarTypeNode)it.next()).accept(this);
		}

    	for (Iterator it = s.iterator(); it.hasNext();) {
			System.out.println("- "+it.next());
			System.out.println();
		}
    	
    	s.clear();
    	
    	System.out.println("----------------------------------------");
	}
}
