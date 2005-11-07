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
 * $Id: CaesarTypeNode.java,v 1.19 2005-11-07 09:26:50 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.join.JoinedTypeNode;
import org.caesarj.compiler.typesys.visitor.ICaesarTypeVisitor;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeNode {
	protected JoinedTypeNode joinedNode = null;
	
	protected JavaQualifiedName qualifiedName;
	protected JavaQualifiedName qualifiedImplName;
	
	protected List<CaesarTypeNode> implicitInners = null;
	protected List<CaesarTypeNode> implicitParents = null;
	protected List<CaesarTypeNode> furtherbounds = null;
	protected List<CaesarTypeNode> directFurtherbounds = null;
	
	protected CaesarTypeGraph g;
	
	public CaesarTypeNode(CaesarTypeGraph g, JavaQualifiedName qn) {
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
	
	public JoinedTypeNode getJoinedNode() {
		if (joinedNode == null) {
			joinedNode = g.getJoinedNode(qualifiedName);
		}
		return joinedNode;
	}
	
	public CjMixinInterfaceDeclaration getTypeDecl() {
		if (getJoinedNode().getInputNode() == null) 
			return null;
		else
			return getJoinedNode().getInputNode().getTypeDecl();
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
	
	public List<CaesarTypeNode> getMixinList() {
		return g.wrapList(getJoinedNode().getImplMixins());
	}
	
	public List<CaesarTypeNode> getOwnMixins() {
		return g.wrapList(getJoinedNode().getOwnMixins());
	}
	
	public List<CaesarTypeNode> declaredInners() {
		return g.wrapList(getJoinedNode().getDeclInners());
	}

	public List<CaesarTypeNode> declaredParents() {
		return g.wrapList(getJoinedNode().getDeclParents());
	}
	
	public List<CaesarTypeNode> inners() {
		return g.wrapList(getJoinedNode().getAllInners());
	}
	
	public List<CaesarTypeNode> parents() {
		return g.wrapList(getJoinedNode().getAllParents());
	}
	
	public List<CaesarTypeNode> furtherbounds() {
		if (furtherbounds == null) {
			furtherbounds = new ArrayList<CaesarTypeNode>(getOwnMixins().size()-1);
			for (CaesarTypeNode tn : getOwnMixins()) {
				if (tn != this) {
					furtherbounds.add(tn);
				}
			}			
		}
		return furtherbounds;
	}
	
	public List<CaesarTypeNode> directFurtherbounds() {
		if (directFurtherbounds == null) {
			directFurtherbounds = new ArrayList<CaesarTypeNode>();
			for (CaesarTypeNode fb1 : furtherbounds()) {
				boolean bAdd = true;
				for (CaesarTypeNode fb2 : furtherbounds()) {
					if (fb2.furtherbounds().contains(fb1)) {
						bAdd = false;
						break;
					}					
				}
				if (bAdd) {
					directFurtherbounds.add(fb1);
				}
			}
		}
		return directFurtherbounds;
	}

	public List<CaesarTypeNode> implicitInners() {
		if (implicitInners == null) {
			implicitInners = new ArrayList<CaesarTypeNode>();
			List<CaesarTypeNode> declLst = declaredInners();
			for (CaesarTypeNode inner : inners()) {
				if (!declLst.contains(inner)) {
					implicitInners.add(inner);
				}
			}
		}
		return implicitInners;
	}
	
	public List<CaesarTypeNode> implicitParents() {
		if (implicitParents == null) {
			implicitParents = new ArrayList<CaesarTypeNode>();
			List<CaesarTypeNode> declLst = declaredParents();
			for (CaesarTypeNode parent : parents()) {
				if (!declLst.contains(parent)) {
					implicitParents.add(parent);
				}
			}
		}
		return implicitParents;
	}
		
	public boolean isFurtherbinding() {
		return getJoinedNode().getOwnMixins().size() > 1;
	}

	public boolean isImplicitType() {
		return (getJoinedNode().getInputNode() == null);
	}

	public boolean isDeclaredType() {
		return (getJoinedNode().getInputNode() != null);
	}
	
	public boolean declaredConcrete() {
		if (!isImplicitType()) 
			return (getTypeDecl().getCorrespondingClassDeclaration().getModifiers() & ClassfileConstants2.ACC_ABSTRACT) == 0;
		else
			return false;
	}
	
	public boolean isAbstract() {
		if (!isImplicitType()) {
			return !declaredConcrete();
		}
		else {
			/* determine if the class is abstract */
			for (CaesarTypeNode n : getOwnMixins()) {
				if (n.declaredConcrete()) {
					return false;
				}
			}
			return true;
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

	public CaesarTypeNode getOuter() {
		return g.wrapJoinedNode(getJoinedNode().getOuter());		
	}
	
	public CaesarTypeNode lookupInner(String ident) {
		return g.wrapJoinedNode(getJoinedNode().findInner(ident));
	}
	
	public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(qualifiedName.getClassName());
        
        if (isFurtherbinding())
        	res.append(" [f]");
        
        if (isImplicitType())
        	res.append(" [i]");
        
        if (parents().size() > 0) {
            res.append("\n\textends: ");
            for(CaesarTypeNode parent : parents()) {
            	res.append(parent.getQualifiedName().getClassName());
            	if (!declaredParents().contains(parent))
                	res.append("[i]");
                res.append(", ");
            }            
        }

        if (getOuter() != null) {
            res.append("\n\touter:   ");
            res.append(getOuter().getQualifiedName().getClassName());                                     
        }
        
        res.append("\n\tmixins:  [");
        for (CaesarTypeNode mixin : getMixinList()) {
        	res.append(mixin.getQualifiedName().getClassName());
            res.append(", ");
        }
        res.append(']');

        return res.toString();
    }

    public boolean isIncrementFor(CaesarTypeNode furtherbound) {
        for (CaesarTypeNode fb : getOwnMixins()) {
        	if (fb == furtherbound) {
        		return true;
        	}
        }
        
        return false;
    }

    public boolean inheritsFromCaesarObject() {
        return getJoinedNode().getAllParents().size() == 0;
    }
    
    public boolean isTopLevelClass() {
        return getOuter() == null;
    }
    
    /**
     *  Crosscutting information 
     */
    
    /* the class has pointcuts and advice different from its direct super */
	private boolean uniqueCrosscutting = false;
	
	/* aspect registry must be generated for the class */
	private boolean needsAspectRegistry = false;
    
    public boolean declaredCrosscutting() {
		if (!isImplicitType()) {
			return getTypeDecl().getCorrespondingClassDeclaration().isCrosscutting();
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
