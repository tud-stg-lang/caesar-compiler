package org.caesarj.compiler.family;


import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.FjCleanClassIfcImplDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.FjCleanClassInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.FjCleanMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.FjConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.FjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.visitor.DeclarationVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;

public class MethodTransformationFjVisitor extends DeclarationVisitor {

	/**
	 * The environment for generate the interfaces of the classes.
	 */
	protected KjcEnvironment environment;

	public MethodTransformationFjVisitor(KjcEnvironment environment)
	{
		this.environment = environment;
	}
	
	
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
		if (owner.isClassDeclaration())
		{
			JClassDeclaration owner = this.owner.getClassDeclaration();
			superClass = self.getSuperClass().getQualifiedName();
			
			//It will generated the structure of the wrapper only if 
			//it is a binding or extends a binding class.
			FjCleanClassDeclaration cleanOwner = null;
			String mapName = null;			
			if (owner instanceof FjCleanClassDeclaration
				&& (CModifier.contains(self.getModifiers(), 
						CaesarConstants.CCI_BINDING)
					|| self.getSuperCollaborationInterface(
						self.getCClass(), CaesarConstants.CCI_BINDING) != null))
			{
				cleanOwner = (FjCleanClassDeclaration) owner;
				mapName = CciConstants.toWrapperMapName(
					FjConstants.toIfcName(ident));
			}
		

			FjConstructorDeclaration[] constructors = self.getConstructors();
			boolean constructorFound = false;
			for( int i = 0; i < constructors.length; i++ ) 
			{
				//Creates the wrapper creator/destructor methods only for 
				//constructors without parameters.
				if (cleanOwner != null && 
					constructors[i].getParameters().length > 0)
				{
					constructorFound = true;

					//creates the methods
					FjCleanMethodDeclaration wrapperCreatorMethod = 
						constructors[i].createWrapperCreatorMethod(
							mapName);
					FjCleanMethodDeclaration wrapperDestructorMethod = 
						constructors[i].createWrapperDestructorMethod(
							mapName);							
					
					//Appends the methods created to the owner and 
					//its interface and proxy
					FjCleanClassInterfaceDeclaration cleanOwnerInterface =
						cleanOwner.getCleanInterface();
					FjCleanClassIfcImplDeclaration cleanOwnerIfcImpl =
						cleanOwner.getCleanInterfaceImplementation();

					cleanOwner.append(wrapperCreatorMethod);
					cleanOwner.append(wrapperDestructorMethod);
					cleanOwnerInterface.addMethod(wrapperCreatorMethod);
					cleanOwnerInterface.addMethod(wrapperDestructorMethod);
					cleanOwnerIfcImpl.addMethod(wrapperCreatorMethod);
					cleanOwnerIfcImpl.addMethod(wrapperDestructorMethod);

				}
				owner.append(
					constructors[ i ].getFactoryMethod(
						self, 
						superClass,
						owner.isClean()));
			}
			//Now the fields and their initialization are added
			if (constructorFound)
			{
				JFieldDeclaration map = self.createWrapperMap(mapName);	
				cleanOwner.addField(map);
				cleanOwner.insertWrapperMappingsInitialization(map);
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
