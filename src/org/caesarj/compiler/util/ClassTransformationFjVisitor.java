package org.caesarj.compiler.util;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.ast.CciCollaborationInterfaceDeclaration;
import org.caesarj.compiler.ast.CciCollaborationInterfaceProxyDeclaration;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassIfcImplDeclaration;
import org.caesarj.compiler.ast.FjCleanClassInterfaceDeclaration;
import org.caesarj.compiler.ast.FjCompilationUnit;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

public class ClassTransformationFjVisitor extends FjVisitor {
	
	protected KjcEnvironment environment;

	public ClassTransformationFjVisitor( KjcEnvironment environment ) {		
		super();
		this.environment = environment;
	}

	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations) {

		String packagePrefix = null;
		if( packageName != null )
			packagePrefix = packageName.getName();
		if( packagePrefix == null
			|| packagePrefix.trim().equals( "" ) )
			packagePrefix = "";
		else
			packagePrefix = packagePrefix + "/";
		
		super.visitCompilationUnit( self, packageName, importedPackages, importedClasses, typeDeclarations );		
		
		JTypeDeclaration[] inners = ((FjCompilationUnit) self).getInners();
		for( int i = 0; i < inners.length; i++ ) {			
			inners[ i ].generateInterface(
				environment.getClassReader(),
				null,
				packagePrefix );
		}		
	}

	public void visitFjClassDeclaration(
		FjClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		// classes need to know their
		// owners in order to be able to access
		// fields when inheriting
		if( owner.get() instanceof FjClassDeclaration )
			self.setOwnerDeclaration( owner.get() );

		super.visitFjClassDeclaration( self, modifiers, ident, typeVariables, superClass, interfaces, body, methods, decls );	}

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

		self.setTypeFactory( environment.getTypeFactory() );		

		// Put self's clean interface into the
		// containing class or compilationunit
		FjCleanClassInterfaceDeclaration ifcDecl =
			self.createCleanInterface( owner.get() );
		FjCleanClassIfcImplDeclaration implDecl =
			self.createCleanInterfaceImplementation( owner.get() );

		owner.append( ifcDecl );
		owner.append( implDecl );

		// Let self implement its own clean interface and inherit
		// its superclass clean-interface-implementation
		CReferenceType ifcType = new CClassNameType(
			owner.getQualifiedName() + ifcDecl.getIdent() );
		self.addInterface( ifcType );
		self.setSuperClass();
		implDecl.setSuperClass( self.getSuperClass() );
		
		// Resets the class' name because the clean interface
		// gets the class' old name so that we do not have to
		// change any type declarations from class to clean interface.
		self.setIdent( FjConstants.baseName( self.getIdent() ) );

		// Our inner declarations may have further
		// inners, so descend here too.
		super.visitFjCleanClassDeclaration( self, modifiers, ident, typeVariables, superClass, interfaces, body, methods, decls );
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

		// virtual classes need to now in order
		// to perform the proper tranformations
		self.setOwnerDeclaration( owner.get() );

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
	
	/**
	 * This method adds the selfContext methods to Collaboration interfaces 
	 * and initializes the its inner classes as collaboration and virtual
	 * interface.
	 * @author Walter Augusto Werner
	 */	
	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods)
	{
		if (self instanceof CciCollaborationInterfaceDeclaration)
		{
			CciCollaborationInterfaceDeclaration collaborationInterface = 
				(CciCollaborationInterfaceDeclaration)self;
				
			collaborationInterface.addSelfContextMethods(
				environment.getTypeFactory());

			collaborationInterface.initInnersAsCollaboration();
			
			CciCollaborationInterfaceProxyDeclaration ciProxy =
				collaborationInterface.getProxyDeclaration(
					environment.getTypeFactory());

			owner.append(ciProxy);
			
		}

		super.visitInterfaceDeclaration(
			self,
			modifiers,
			ident,
			interfaces,
			body,
			methods);
	}
	
}
