package org.caesarj.compiler.ast;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JVirtualClassDeclaration extends JCaesarClassDeclaration {

	public JVirtualClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		JPointcutDeclaration[] pointcuts,
		JAdviceDeclaration[] advices,
		CaesarDeclare[] declares) {
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			wrappee,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			pointcuts,
			advices,
			declares);
	}

}
