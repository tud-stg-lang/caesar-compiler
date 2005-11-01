package org.caesarj.compiler.typesys.join;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.typesys.CaesarTypeSystemException;
import org.caesarj.compiler.typesys.graphsorter.GraphSorter;
import org.caesarj.compiler.typesys.input.InputTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class JoinedTypeNode {
	protected JavaQualifiedName qualifiedName;
	protected JoinedTypeGraph graph;
	protected JoinedTypeNode outer;
	
	/* computed on demand */
	protected boolean bInputNode = false;
	protected InputTypeNode inputNode = null;
	protected List<JoinedTypeNode> allMixins = null;
	protected List<JoinedTypeNode> implMixins = null;
	protected List<JoinedTypeNode> ownMixins = null;
	protected List<JoinedTypeNode> declParents = null;
	protected List<JoinedTypeNode> directParents = null;
	protected List<JoinedTypeNode> allParents = null;
	protected List<String> declInnerNames = null;
	protected List<JoinedTypeNode> declInners = null;
	protected List<String> allInnerNames = null;
	protected List<JoinedTypeNode> allInners = null;
	
	public JoinedTypeNode(JavaQualifiedName qualifiedName, JoinedTypeGraph graphResolver) {
		this.qualifiedName = qualifiedName;
		this.graph = graphResolver;
		if (!qualifiedName.getOuterPrefix().equals("")) {
			this.outer = graphResolver.getNodeByName(
					new JavaQualifiedName(qualifiedName.getOuterQualifiedName()));
		}
	}
	
	public JavaQualifiedName getQualifiedName() {
		return qualifiedName;
	}
	
	public String getIdent() {
		return qualifiedName.getIdent();
	}
	
	public JoinedTypeNode getOuter() {
		return outer;
	}
	
	protected JoinedTypeNode lookupClassInCtx(String name) {
		if (outer == null) {
			JavaQualifiedName resolvedName = getInputNode().resolveType(name);
			if (graph.getInputNode(resolvedName) == null) {
				return null;
			}
			return graph.getNodeByName(resolvedName);
		}
		else {
			JoinedTypeNode inner = outer.findInner(name);
			if (inner != null) 
				return inner;
			return outer.lookupClassInCtx(name);
		}
	}
	
	public JoinedTypeNode findInner(String name) {
		for (String inner : getAllInnerNames()) {
			if (inner.equals(name)) {
				return createInnerForName(inner);
			}
		}
		return null;
	}
	
	public List<String> getAllInnerNames() {
		if (allInnerNames == null) {
			allInnerNames = new ArrayList<String>();
			for (JoinedTypeNode mixin : getAllMixins()) {
				for (String inner : mixin.getDeclInnerNames()) {
					if (!allInnerNames.contains(inner)) {
						allInnerNames.add(inner);
					}
				}
			}
		}
		return allInnerNames;		
	}
	
	public List<JoinedTypeNode> getAllInners() {
		if (allInners == null) {
			allInners = new ArrayList<JoinedTypeNode>();
			for (String name: getAllInnerNames()) {
				allInners.add(createInnerForName(name));
			}
		}
		return allInners;
	}
	
	public List<JoinedTypeNode> getDeclInners() {
		if (declInners == null) {
			declInners = new ArrayList<JoinedTypeNode>();
			for (String name: getDeclInnerNames()) {
				declInners.add(createInnerForName(name));
			}
		}
		return declInners;
	}
	
	protected JoinedTypeNode createInnerForName(String name) {
		return graph.getNodeByName(new JavaQualifiedName(getQualifiedName() + "$" + name));
	}
	
	public InputTypeNode getInputNode() {
		if (!bInputNode) {
			inputNode = graph.getInputNode(qualifiedName);
			bInputNode = true;
		}
		return inputNode;
	}
	
	public TokenReference getTokenRef() {
		if (isDeclared()) {
			return getInputNode().getTokenRef();
		}
		else if (getOuter() != null) {
			return getOuter().getTokenRef();
		}
		else {
			return TokenReference.NO_REF;
		}
	}
	
	public boolean isDeclared() {
		return (getInputNode() != null);
	}
	
	public List<JoinedTypeNode> getImplMixins() {
		if (implMixins == null) {
			implMixins = new ArrayList<JoinedTypeNode>();
			for (JoinedTypeNode mixin : getAllMixins()) {
				if (mixin.isDeclared()) {
					implMixins.add(mixin);
				}
			}			
		}
		return implMixins;
	}
	
	public List<JoinedTypeNode> getAllMixins() {
		if (allMixins == null) {
			allMixins = new ArrayList<JoinedTypeNode>();
			allMixins.addAll(getOwnMixins());
			for (JoinedTypeNode parent : getAllParents()) {
				List<JoinedTypeNode> parentMixins = parent.getOwnMixins();
				allMixins.addAll(parentMixins);
			}			
		}
		return allMixins;
	}
	
	public List<JoinedTypeNode> getOwnMixins() {
		if (ownMixins == null) {
			ownMixins = new ArrayList<JoinedTypeNode>();
			if (outer == null) {
				ownMixins.add(this);
			}
			else {
				String ident = qualifiedName.getIdent();
				for (JoinedTypeNode mixin : outer.getAllMixins()) {
					JoinedTypeNode inner = mixin.findInner(ident);
					if (inner != null) {
						ownMixins.add(inner);
					}
				}
			}
		}
		return ownMixins;
	}
	
	public List<JoinedTypeNode> getAllParents() {
		if (allParents == null) {
			try {
				TypeNodeParentSorter sorter = new TypeNodeParentSorter(this);
				allParents = sorter.getSortedTypeNodes();
				/* remove self */
				allParents.remove(0);
			}
			catch (GraphSorter.CycleFoundException e) {
				graph.getCompiler().reportTrouble(
						new PositionedError(getTokenRef(), KjcMessages.CLASS_CIRCULARITY,
				                getQualifiedName().toString()));
				throw new CaesarTypeSystemException();
			}
		}
		return allParents;
	}
	
	public List<JoinedTypeNode> getOuterChain() {
		List<JoinedTypeNode> lst = new ArrayList<JoinedTypeNode>();
		JoinedTypeNode n = this;
		while (n != null) {
			lst.add(0, n);
			n = n.getOuter();
		}
		return lst;
	}
	
	/**
	 * returns the type of this Node in context of the node n.
	 * B extends A ->
	 * A.A in context of B is B.A
	 */
	public JoinedTypeNode getTypeInContextOf(JoinedTypeNode n) {
	    // top level class, no redefinition possible
	    if (getOuter() == null || n == null)
	        return this;
	    
	    List<JoinedTypeNode> l1 = getOuterChain();
	    List<JoinedTypeNode> l2 = n.getOuterChain();
	    
	    JoinedTypeNode thisOuterChain[] = l1.toArray(new JoinedTypeNode[l1.size()]);
	    JoinedTypeNode contextOuterChain[] = l2.toArray(new JoinedTypeNode[l2.size()]);
	    
	    // check subtype relations

	    // s = max(thisOuterChain.length-1, contextOuterChain.length)
	    int s = thisOuterChain.length - 1; 
	    if (s > contextOuterChain.length)
	        s = contextOuterChain.length;
	    
	    for (int j = s-1; j >= 0; j--) {
	    	JoinedTypeNode t = contextOuterChain[j];
	    	for (int i = j+1; i < thisOuterChain.length; i++) {
		    	t = t.findInner(thisOuterChain[i].getQualifiedName().getIdent());
		    	if (t == null) {
		    		break;
		        }
		    }
	    	if (t != null && t.isSubtypeOf(this)) {
	    		return t;
	    	}
	    }
	    return null;
    }
	
	public boolean isSubtypeOf(JoinedTypeNode n) {
	    if (this == n) {
	        return true;
        }
	    
	    for (JoinedTypeNode mixin : getAllMixins()) {
            if (mixin == n)
                return true;
        }
	    
	    return false;
    }
	
	public List<JoinedTypeNode> getDirectParents() {
		if (directParents == null) {
			directParents = new ArrayList<JoinedTypeNode>();
			for (JoinedTypeNode mixin : getOwnMixins()) {
				for (JoinedTypeNode parent : mixin.getDeclParents()) {
					JoinedTypeNode resParent = parent;
					if (outer != null) {
						resParent = parent.getTypeInContextOf(outer);
						if (resParent == null) {
							graph.getCompiler().reportTrouble(
									new PositionedError(getTokenRef(), CaesarMessages.CANNOT_INHERIT_FROM_CCLASS,
											parent.getQualifiedName()));
							throw new CaesarTypeSystemException();							
						}
					}
					if (!directParents.contains(resParent)) {
						directParents.add(resParent);
					}
				}
			}
		}
		return directParents;
	}
	
	public List<JoinedTypeNode> getDeclParents() {
		if (declParents == null) {
			declParents = new ArrayList<JoinedTypeNode>();
			if (isDeclared()) {
				for (String parentIdent : getInputNode().getDeclaredParents()) {
					JoinedTypeNode parent = lookupClassInCtx(parentIdent);
					if (parent == null) {
						graph.getCompiler().reportTrouble(
								new PositionedError(getTokenRef(), CaesarMessages.CCLASS_UNKNOWN,
										parentIdent));
						throw new CaesarTypeSystemException();						
					}
					if (outer != null) {
						parent = graph.getNodeByName(
								new JavaQualifiedName(qualifiedName.getPrefix() + parentIdent));						
					}
					declParents.add(parent);
				}
			}
		}
		return declParents;	
	}
	
	public List<String> getDeclInnerNames() {
		if (declInnerNames == null) {
			declInnerNames = new ArrayList<String>();
			if (isDeclared()) {
				declInnerNames.addAll(getInputNode().getDeclaredInners());
			}
		}
		return declInnerNames;	
	}
	
	public String toString() {
		return getQualifiedName().toString();
	}
}
