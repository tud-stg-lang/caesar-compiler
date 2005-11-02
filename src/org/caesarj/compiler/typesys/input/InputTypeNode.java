package org.caesarj.compiler.typesys.input;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Node of the InputTypeGraph, 
 * 		serves as a wrapper for an AST type declaration
 * 		used as input for Caesar type graph generation
 *  
 * @author vaidas
 *
 */
public class InputTypeNode {
	
	/* type declaration */
	protected CjMixinInterfaceDeclaration typeDecl;
	
	/* reference to outer node (can be null) */
	protected InputTypeNode outer;
	
	/* the names of all declared parents of the node (computed on-demand) */
	protected String[] parents = null;
	
	/* the names of all declared inners of the node (computed on-demand) */
	protected List<String> inners = null;
	
	/**
	 * Constructs input type node
	 * 
	 * @param typeDecl	Type declaration to be wrapped
	 * @param outer		Outer type node (can be null)
	 */
	public InputTypeNode(CjMixinInterfaceDeclaration typeDecl, InputTypeNode outer) {
		this.typeDecl = typeDecl;
		this.outer = outer;
	}
	
	/**
	 *	Get qualified name of the node 
	 */
	public JavaQualifiedName getQualifiedName() {
		return new JavaQualifiedName(typeDecl.getSourceClass().getQualifiedName());
	}
	
	/**
	 *	Get type declaration 
	 */
	public CjMixinInterfaceDeclaration getTypeDecl() {
		return typeDecl;
	}
	
	/**
	 *	Get token reference for the type declaration 
	 */
	public TokenReference getTokenRef() {
		return getTypeDecl().getTokenReference();
	}
	
	/**
	 * Resolve class name in the context of the type declaration
	 * 
	 * @param name		class to be resolved
	 * @return			qualified name of the resolved class (null if not found)
	 */
	public JavaQualifiedName resolveType(String name) {
		CContext context = typeDecl.getContext();
		try {
			CClass clazz = context.lookupClass(typeDecl.getSourceClass(), name);
			return new JavaQualifiedName(clazz.getQualifiedName());
		}
		catch (UnpositionedError e) {
			return null; /* not found */
		}			
	}
	
	/**
	 *	Get the names of the declared parents 
	 */
	public String[] getDeclaredParents() {
		if (parents == null) {
			CReferenceType[] extTypes = typeDecl.getExtendedTypes();
			parents = new String[extTypes.length];
			for (int i1 = 0; i1 < extTypes.length; i1++) {
				parents[i1] = extTypes[i1].toString();
			}
		}
		return parents;
	}
	
	/**
	 *	Get the names of the declared inners 
	 */
	public List<String> getDeclaredInners() {
		if (inners == null) {
			JTypeDeclaration[] innerDecls = typeDecl.getInners();
			inners = new ArrayList<String>();
			for (JTypeDeclaration tdecl : innerDecls) {
				if (tdecl instanceof CjMixinInterfaceDeclaration) {
					inners.add(tdecl.getIdent());
				}
			}
		}
		return inners;
	}
}
