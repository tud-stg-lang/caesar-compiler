package org.caesarj.compiler.typesys.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.typesys.util.JavaQualifiedName;
import org.caesarj.compiler.typesys.visitor.ICaesarTypeVisitor;
import org.caesarj.util.InconsistencyException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeNode {
	
	private List enclosingFor      = new LinkedList(); // of OuterInnerRelation
	private List enclosedBy        = new LinkedList(); // of OuterInnerRelation
	private List inheritsFrom  	   = new LinkedList(); // of SuperSubRelation
	private List inheritedBy   	   = new LinkedList(); // of SuperSubRelation
	private List furtherbindingFor = new LinkedList(); // of FurtherboundFurtherbindingRelation
	private List furtherboundBy    = new LinkedList(); // of FurtherboundFurtherbindingRelation
	
	private Kind kind;
	
	private JavaQualifiedName qualifiedName;
	
	private List mixinList = new LinkedList();
	
	private CaesarTypeGraph g;
	
	public static class Kind {
		private int i;
		private String desc;
		private Kind(int i, String desc) {this.i=i; this.desc=desc;}
		public boolean equals(Object other) {return ((Kind)other).i == i;}
		public String toString() {return desc;}
	}
	
	public static Kind DECLARED = new Kind(1, "DECLARED");
	public static Kind IMPLICIT = new Kind(2, "IMPLICIT");
		

	public CaesarTypeNode(CaesarTypeGraph g, Kind kind, JavaQualifiedName qn) {
		this.kind = kind;
		this.qualifiedName = qn;
		this.g = g;
	}
	
	public void accept(ICaesarTypeVisitor visitor) {
		visitor.visitCaesarTypeNode(this);
	}

	public Iterator declaredInners() {
		return new ImplicitRelationFilter(enclosingFor.iterator(), true);
	}

	public Iterator declaredParents() {
		return new ImplicitRelationFilter(inheritsFrom.iterator(), true);
	}

	public JavaQualifiedName getQualifiedName() {
		return qualifiedName;
	}

	public List getMixinList() {
		return mixinList;
	}

	public CaesarTypeNode[] getMixinListAsArray() {
		return (CaesarTypeNode[])mixinList.toArray(new CaesarTypeNode[mixinList.size()]);
	}

	public Iterator implicitInners() {
		return new ImplicitRelationFilter(enclosingFor.iterator(), false);
	}

	public Iterator implicitParents() {
		return new ImplicitRelationFilter(inheritsFrom.iterator(), false);
	}

	public Iterator inners() {
		return enclosingFor.iterator();
	}

	public boolean isFurtherbinding() {
		return furtherbindingFor.size() > 0;
	}

	public boolean isImplicitType() {
		return kind.equals(IMPLICIT);
	}

	public boolean isDeclaredType() {
		return kind.equals(DECLARED);
	}

	public Iterator parents() {
		return inheritsFrom.iterator();
	}
	
	public Kind getKind() {
		return kind;
	}

	
	public void addEnclosedBy(Relation relation) {
		addToList(relation, enclosedBy);
	}

	public void addEnclosingFor(Relation relation) {
		addToList(relation, enclosingFor);
	}

	public void addFurtherbindingFor(Relation relation) {
		addToList(relation, furtherbindingFor);
	}

	public void addFurtherboundBy(Relation relation) {
		addToList(relation, furtherboundBy);
	}

	public void addInheritedBy(Relation relation) {
		addToList(relation, inheritedBy);
	}

	public void addInheritsFrom(Relation relation) {
		addToList(relation, inheritsFrom);
	}

	private void addToList(Relation relation, List list) {
		if(!list.contains(relation))
			list.add(relation);
	}

	public CaesarTypeNode getOuter() {
		if(enclosedBy.size() == 0) {
			return null;
		}
		else if(enclosedBy.size() == 1) {
			return ((OuterInnerRelation)enclosedBy.get(0)).getOuterNode();
		}
		else {
			throw new InconsistencyException("multiple outers not supported yet");
		}
	}
	
	public CaesarTypeNode lookupInner(String ident) {
		for (Iterator it = inners(); it.hasNext();) {
			CaesarTypeNode inner = ((OuterInnerRelation)it.next()).getInnerNode();
			if(inner.getQualifiedName().getIdent().equals(ident)) {
				return inner;
			}
		}
		
		return null;
	}
	
	public CaesarTypeNode lookupDeclaredInner(String ident) {
		for (Iterator it = declaredInners(); it.hasNext();) {
			CaesarTypeNode inner = ((OuterInnerRelation)it.next()).getInnerNode();
			if(inner.getQualifiedName().getIdent().equals(ident))
				return inner;
		}
		
		return null;
	}
	
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(qualifiedName.getClassName());
        
        if(isFurtherbinding())
        	res.append(" [f]");

        res.append("\n\tkind:    "+kind);        
        
        if(inheritsFrom.size() > 0) {
            res.append("\n\textends: ");
            for(Iterator it = inheritsFrom.iterator(); it.hasNext();) {
            	SuperSubRelation rel = (SuperSubRelation)it.next();
                res.append(rel.getSuperNode().getQualifiedName().getClassName());
                if(rel.isImplicit())
                	res.append("[i]");
                
                if(it.hasNext())
                    res.append(", ");
            }            
        }

        if(enclosedBy.size() > 0) {
            res.append("\n\touter:   ");
            for(Iterator it = enclosedBy.iterator(); it.hasNext();) {
                res.append(((OuterInnerRelation)it.next()).getOuterNode().getQualifiedName().getClassName());
                if(it.hasNext())
                    res.append(", ");
            }            
        }
        
        res.append("\n\tmixins:  [");
        for (Iterator it = mixinList.iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            res.append(item.getQualifiedName().getClassName());
            if(it.hasNext())
                res.append(", ");
        }
        res.append(']');

        return res.toString();
    }
}
