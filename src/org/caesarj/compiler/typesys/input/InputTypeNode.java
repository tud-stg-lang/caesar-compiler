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

public class InputTypeNode {
	
	protected CjMixinInterfaceDeclaration typeDecl;
	protected InputTypeNode outer;
	protected String[] parents = null;
	protected List<String> inners = null;
	
	public InputTypeNode(CjMixinInterfaceDeclaration typeDecl, InputTypeNode outer) {
		this.typeDecl = typeDecl;
		this.outer = outer;
	}
	
	public JavaQualifiedName getQualifiedName() {
		return new JavaQualifiedName(typeDecl.getSourceClass().getQualifiedName());
	}
	
	public CjMixinInterfaceDeclaration getTypeDecl() {
		return typeDecl;
	}
	
	public TokenReference getTokenRef() {
		return getTypeDecl().getTokenReference();
	}
	
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
