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
 * $Id: CaesarTypeNode.java,v 1.16 2005-03-29 09:47:27 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.typesys.CaesarTypeSystemException;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.visitor.ICaesarTypeVisitor;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.TokenReference;

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
	private JavaQualifiedName qualifiedImplName;
	
	// null for implicit types
	private CjMixinInterfaceDeclaration typeDecl = null; 
	
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
		this.qualifiedImplName = qn.convertToImplName();
		this.g = g;
	}
	
	public void accept(ICaesarTypeVisitor visitor) {
		visitor.visitCaesarTypeNode(this);
	}

	public JavaQualifiedName getQualifiedName() {
		return qualifiedName;
	}
	
	public JavaQualifiedName getQualifiedImplName() {
		return qualifiedImplName;
	}
	
	public CjMixinInterfaceDeclaration getTypeDecl() {
		return typeDecl;
	} 
	
	public TokenReference getTokenRef() {
		if (getTypeDecl() != null) {
			return getTypeDecl().getTokenReference();
		}
		else if (getOuter() != null) {
			return getOuter().getTokenRef();
		}
		else {
			return null;
		}
	}

	public List getMixinList() {
		return mixinList;
	}

	public Iterator declaredInners() {
		return new ImplicitRelationFilter(enclosingFor.iterator(), true);
	}

	public Iterator declaredParents() {
		return new ImplicitRelationFilter(inheritsFrom.iterator(), true);
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
	
	public boolean isAbstract() {
		/* determine if the class is abstract */
		if (typeDecl != null) {
			return (typeDecl.getCorrespondingClassDeclaration().getModifiers() & ClassfileConstants2.ACC_ABSTRACT) != 0;
		}
		else {
			boolean isAbstr = true;
			for (Iterator it = incrementFor(); it.hasNext();) {
				FurtherboundFurtherbindingRelation rel = (FurtherboundFurtherbindingRelation)it.next();
				if (!rel.getFurtherboundNode().isAbstract()) {
					isAbstr = false;
				}				
			}
			return isAbstr;
		}
	}
	
	public boolean canBeInstantiated() {
		if (isAbstract()) {
			return false;
		}
		if (isTopLevelClass()) {
			return true;
		}
		else {
			return getOuter().canBeInstantiated();
		}
	}

	public Iterator parents() {
		return inheritsFrom.iterator();
	}
	
	public Iterator incrementFor() {
	    return furtherbindingFor.iterator();
	}
	
	public Kind getKind() {
		return kind;
	}

	
	public void setTypeDecl(CjMixinInterfaceDeclaration decl) {
		typeDecl = decl;
	} 
	
	public void addEnclosedBy(BidirectionalRelation relation) {	    
		addToList(relation, enclosedBy);
		if(enclosedBy.size() > 1)
		    throw new InconsistencyException("multiple outers not supported yet");
	}

	public void addEnclosingFor(BidirectionalRelation relation) {
		addToList(relation, enclosingFor);
	}

	public void addFurtherbindingFor(BidirectionalRelation relation) {
		addToList(relation, furtherbindingFor);
	}

	public void addFurtherboundBy(BidirectionalRelation relation) {
		addToList(relation, furtherboundBy);
	}

	public void addInheritedBy(BidirectionalRelation relation) {
		addToList(relation, inheritedBy);
	}

	public void addInheritsFrom(BidirectionalRelation relation) {
		addToList(relation, inheritsFrom);
	}

	private void addToList(BidirectionalRelation relation, List list) {
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
	
	public CaesarTypeNode getTopmostNode() throws CaesarTypeSystemException{
        if(!isFurtherbinding()) {
            return this;
        }
        else {
            LinkedList topMostList = new LinkedList();

            for(Iterator it=furtherbindingFor.iterator(); it.hasNext();) {
                CaesarTypeNode item = ((FurtherboundFurtherbindingRelation)it.next()).getFurtherboundNode();
                topMostList.add(item.getTopmostNode());
            }
                        
            // check that they are all same
            if(topMostList.size() > 1) {
                boolean first = true;
                CaesarTypeNode ref = null;
                for (Iterator it = topMostList.iterator(); it.hasNext();) {
                    CaesarTypeNode item = (CaesarTypeNode) it.next();
                    
                    if(first) {
                        // get ref
                        first = false;
                        ref = item;
                    }
                    else {
                        // compare with ref
                        if(!ref.equals(item))
                            throw new CaesarTypeSystemException();
                    }
                }                               
            }
            
            return (CaesarTypeNode)topMostList.get(0);
        }	    
	}
	
	/**
	 * returns the type of this Node in context of the node n.
	 * B extends A ->
	 * A.A in context of B is B.A
	 */
	public CaesarTypeNode getTypeInContextOf(CaesarTypeNode n) {
	    
	    // top level class, no redefinition possible
	    if(enclosedBy.size() == 0 || n == null)
	        return this;
	    
	    List l1 = new LinkedList();
	    List l2 = new LinkedList();
	    
	    // generated lists
	    genOuterList(l1);
	    n.genOuterList(l2);
	    
	    CaesarTypeNode thisOuterChain[] = 
	        (CaesarTypeNode[])l1.toArray(new CaesarTypeNode[l1.size()]);
	    CaesarTypeNode contextOuterChain[] = 
	        (CaesarTypeNode[])l2.toArray(new CaesarTypeNode[l2.size()]);
	    
	    // check subtype relations

	    // s = max(thisOuterChain.length-1, contextOuterChain.length)
	    int s = thisOuterChain.length - 2; 
	    if(s > contextOuterChain.length - 1)
	        s = contextOuterChain.length - 1;
	    
	    for(int j=s; j>=0; j--) {
	        CaesarTypeNode t = contextOuterChain[j];
	        	        
	        for(int i=j+1; i<thisOuterChain.length; i++) {
	            t = t.lookupInner(thisOuterChain[i].getQualifiedName().getIdent());
	            if(t == null)
	                break;
	        }

	        if(t != null) {
	            if(t.isSubtypeOf(this))
	                return t;
            }
        }
	    
	    return null;
    }
	
	private void genOuterList(List l) {
	    l.add(0, this);
	    if(enclosedBy.size()>0) {	        
	        getOuter().genOuterList(l);
        }
    }
	
	public boolean isSubtypeOf(CaesarTypeNode n) {
	    if(this.qualifiedName.equals(n.getQualifiedName())) {
	        return true;
        }
	    
	    for (Iterator it = parents(); it.hasNext();) {
            SuperSubRelation rel = (SuperSubRelation) it.next();
            if(rel.getSuperNode().isSubtypeOf(n))
                return true;
        }
	    
	    return false;
	    
	    
	    /*
	    // would this here work?
	    // more efficient method?
	    boolean res = false;
	    if(getMixinList().size() <= n.getMixinList().size()) {
	        res = true;
	        for (Iterator it = getMixinList().iterator(); it.hasNext() && res;) {                
                if(!n.getMixinList().contains(it.next()))
                    res = false;
            }
	    }
	    return res;
	    */
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

    public boolean isIncrementFor(CaesarTypeNode furtherbound) {
        if(this.equals(furtherbound))
            return true;
        
        boolean res = false;
        
        for (Iterator it = incrementFor(); it.hasNext();) {
            FurtherboundFurtherbindingRelation rel = (FurtherboundFurtherbindingRelation)it.next();
            res = res || rel.getFurtherboundNode().isIncrementFor(furtherbound);
        }
        
        return res;
    }

    public boolean inheritsFromCaesarObject() {
        return inheritsFrom.size() == 0;
    }
    
    public boolean isTopLevelClass() {
        return enclosedBy.size() == 0;
    }
    
    /**
     *  Crosscutting information 
     */
    
    /* the class has pointcuts and advice different from its direct super */
	private boolean uniqueCrosscutting = false;
	
	/* aspect registry must be generated for the class */
	private boolean needsAspectRegistry = false;
    
    public boolean declaredCrosscutting() {
		if (typeDecl != null) {
			return typeDecl.getCorrespondingClassDeclaration().isCrosscutting();
		}
		else {
			return false;
		}
	}
	
	public boolean isCrosscutting() {
		if (declaredCrosscutting()) {
			return true;
		}
		for (Iterator it = getMixinList().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            if (item.declaredCrosscutting()) {
            	return true;
            }
        }
		return false;
	}
	
	public boolean isUniqueCrosscutting() {
		return uniqueCrosscutting;
	}
	
	public void setUniqueCrosscutting() {
		uniqueCrosscutting = true;
	}
	
	public boolean needsAspectRegistry() {
		return needsAspectRegistry;
	}
	
	public void setNeedsAspectRegistry() {
		needsAspectRegistry = true;
	}
}
