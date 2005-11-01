package org.caesarj.compiler.typesys.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

public class InputTypeGraph {

	protected Map<JavaQualifiedName, InputTypeNode> nameToNode 
		= new HashMap<JavaQualifiedName, InputTypeNode>();
	protected List<InputTypeNode> topLevelTypes	= new ArrayList<InputTypeNode>();

	protected InputTypeGraph inputGraph;

	public InputTypeNode getNodeByName(JavaQualifiedName qualName) {
		return nameToNode.get(qualName);
	}
	
	public List<InputTypeNode> topLevelTypes() {
		return topLevelTypes;
	}
	
	public void addCompilationUnit(JCompilationUnit cu) {
		for (JTypeDeclaration td: cu.getInners()) {
			if (td instanceof CjMixinInterfaceDeclaration) {
				InputTypeNode node = addTypeDeclaration((CjMixinInterfaceDeclaration)td, null);
				topLevelTypes.add(node);
			}
		}
	}
	
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
