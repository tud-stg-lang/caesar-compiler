package org.caesarj.compiler.util;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.ast.CciWeaveletClassDeclaration;
import org.caesarj.compiler.ast.CciWeaveletReferenceType;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjConstructorDeclaration;
import org.caesarj.compiler.ast.FjTypeSystem;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

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

		// replace self's clean methods by a
		// self-context-enabled implementation.
		self.addSelfContextToCleanMethods(
			self.getCleanInterface().getCClass().getAbstractType() );


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
		if( owner.get() instanceof FjClassDeclaration ) {
			superClass = self.getSuperClass().getQualifiedName();

			FjConstructorDeclaration[] constructors = self.getConstructors();
			for( int i = 0; i < constructors.length; i++ ) {
				((FjClassDeclaration) owner.get()).
					append( constructors[ i ].getFactoryMethod( self, superClass ) );
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
	/* Walter
	 * @see org.caesarj.compiler.util.FjVisitor#visitCciWeaveletClassDeclaration(org.caesarj.compiler.ast.CciWeaveletClassDeclaration, int, java.lang.String, org.caesarj.kjc.CTypeVariable[], java.lang.String, org.caesarj.kjc.CReferenceType[], org.caesarj.kjc.JPhylum[], org.caesarj.kjc.JMethodDeclaration[], org.caesarj.kjc.JTypeDeclaration[])
	 */
	public void visitCciWeaveletClassDeclaration(
		CciWeaveletClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls)
	{

		CciWeaveletReferenceType superCollaborationInterface = 
			self.getSuperCollaborationInterface();
		CReferenceType bindingType = superCollaborationInterface
			.getBindingType();
		CReferenceType implementationType = superCollaborationInterface
			.getImplementationType();
		
		addWeaveletFactoryMethods(self, bindingType, 
			CciConstants.BINDING_FIELD_NAME);

		addWeaveletFactoryMethods(self, implementationType, 
			CciConstants.IMPLEMENTATION_FIELD_NAME);



		super.visitCciWeaveletClassDeclaration(
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
	protected void addWeaveletFactoryMethods(
		CciWeaveletClassDeclaration self, 
		CReferenceType ciType, 
		String fieldName)
	{
		if (CModifier.contains(ciType.getCClass().getModifiers(), 
			Constants.FJC_VIRTUAL))
		{
			if (owner.get() instanceof CciWeaveletClassDeclaration)
			{
				CciWeaveletClassDeclaration weavelet = 
					(CciWeaveletClassDeclaration)owner.get();
				
				weavelet.addFactoryMethods(self, ciType, 
					fieldName);
			}
		}		
	}


}
