package org.caesarj.compiler.util;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjConstructorDeclaration;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;

public class MethodTransformationFjVisitor extends FjVisitor {

	
	public void visitFjCleanClassDeclaration(
		FjCleanClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		super.visitFjCleanClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls);
			

		CReferenceType selfType = self.getCleanInterface().getCClass().getAbstractType();


		// replace self's clean methods by a
		// self-context-enabled implementation.
		self.addSelfContextToCleanMethods(selfType);


		// extend the contructor-args
		// by a supertype-parameter.
		self.addSuperTypeParameterToConstructors();
	}
	
	public void visitFjVirtualClassDeclaration(
		FjVirtualClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {
		
		// add the factory methods to the containing class
		// we need the unmodified constructors signatures:
		// self.addSuperTypeParameterToConstructors() called later!
		Object objOwner = owner.get();
		if( objOwner instanceof FjClassDeclaration ) {
			FjClassDeclaration owner = (FjClassDeclaration) objOwner;
			superClass = self.getSuperClass().getQualifiedName();

			FjConstructorDeclaration[] constructors = self.getConstructors();
			for( int i = 0; i < constructors.length; i++ ) {
				owner.append(
					constructors[ i ].getFactoryMethod(
						self, 
						superClass,
						owner.isClean()));
			}
		}
		
		super.visitFjVirtualClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls);
	}
}
