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
