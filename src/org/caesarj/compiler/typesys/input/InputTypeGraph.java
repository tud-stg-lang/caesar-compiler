package org.caesarj.compiler.typesys.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * Graph of type declarations, 
 *     inferred from parsed AST, 
 *     used as input for Caesar type graph generation.
 
 * @author vaidas 
 */
public class InputTypeGraph {

	/* map: qualified name to type node */
	protected Map<JavaQualifiedName, InputTypeNode> nameToNode 
		= new HashMap<JavaQualifiedName, InputTypeNode>();
	/* list of all top level cclasses */
	protected List<InputTypeNode> topLevelTypes	= new ArrayList<InputTypeNode>();

	/**
	 *  Get type node for qualified name 
	 */
	public InputTypeNode getNodeByName(JavaQualifiedName qualName) {
		return nameToNode.get(qualName);
	}
	
	/**
	 *	Get all top level cclasses
	 */
	public List<InputTypeNode> topLevelTypes() {
		return topLevelTypes;
	}
	
	/**
	 * Add all cclass declarations of the compilation unit to the graph 
	 */
	public void addCompilationUnit(JCompilationUnit cu) {
		for (JTypeDeclaration td: cu.getInners()) {
			if (td instanceof CjMixinInterfaceDeclaration) {
				InputTypeNode node = addTypeDeclaration((CjMixinInterfaceDeclaration)td, null);
				topLevelTypes.add(node);
			}
		}
	}
	
	/**
	 * Add type declaration to the graph, recurse to its inners
	 * 
	 * @param typeDecl		The declaration to be added
	 * @param outer			The enclosing type node of the declaration to be added
	 * @return				Added declaration
	 */
	public InputTypeNode addTypeDeclaration(CjMixinInterfaceDeclaration typeDecl, InputTypeNode outer) {
		InputTypeNode node = new InputTypeNode(typeDecl, outer);
		nameToNode.put(new JavaQualifiedName(typeDecl.getSourceClass().getQualifiedName()), node);
		
		for (JTypeDeclaration td: typeDecl.getInners()) {
			if (td instanceof CjMixinInterfaceDeclaration) {
				addTypeDeclaration((CjMixinInterfaceDeclaration)td, node);
			}
		}
		return node;
	}
}
