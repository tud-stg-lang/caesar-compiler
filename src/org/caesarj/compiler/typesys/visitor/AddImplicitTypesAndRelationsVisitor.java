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
		if(visited.contains(this)) return;
        
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
