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
            
            for (Iterator it = t.declaredParents(); it.hasNext();) {
                CaesarTypeNode superType = ((SuperSubRelation)it.next()).getSuperNode();
                LinkedList l = new LinkedList();
                createMixinList(l, outerMixinList, 0, superType.getQualifiedName());
                superClassMixinLists.add(l);
            }
            
            if(t.isFurtherbinding()) {
            	LinkedList s = new LinkedList();
                createMixinList(s, outerMixinList, m+1, qualifiedName);
                superClassMixinLists.add(s);
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
