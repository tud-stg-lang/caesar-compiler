package org.caesarj.compiler.util;

import java.util.ArrayList;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjCleanMethodDeclaration;
import org.caesarj.compiler.ast.FjConstructorDeclaration;
import org.caesarj.compiler.ast.FjNameExpression;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

public class MethodTransformationFjVisitor extends FjVisitor {

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
		Object objOwner = owner.get();
		if( objOwner instanceof FjClassDeclaration )
		{
			FjClassDeclaration owner = (FjClassDeclaration) objOwner;
			superClass = self.getSuperClass().getQualifiedName();
			
			//It will generated the structure of the wrapper only if 
			//it is a binding or extends a binding class.
			FjCleanClassDeclaration cleanOwner = null;
			String mapName = null;			
			if (owner instanceof FjCleanClassDeclaration
				&& (CModifier.contains(self.getModifiers(), 
						Constants.CCI_BINDING)
					|| self.getSuperCollaborationInterface(
						self.getCClass(), Constants.CCI_BINDING) != null))
			{
				cleanOwner = (FjCleanClassDeclaration) owner;
				mapName = CciConstants.toWrapperMapName(
					FjConstants.toIfcName(ident));
			}
		

			FjConstructorDeclaration[] constructors = self.getConstructors();
			boolean constructorFound = false;
			for( int i = 0; i < constructors.length; i++ ) 
			{
				//Creates the wrapper creator methods only for 
				//constructors without parameters.
				if (cleanOwner != null && 
					constructors[i].getParameters().length > 0)
				{
					constructorFound = true;

					//creates the method
					FjCleanMethodDeclaration wrapperInitializationMethod = 
						constructors[i].createWrapperInstantiationMethod(
							mapName);
					
					//Appends the method created to the owner and 
					//its interface and proxy
					cleanOwner.append(wrapperInitializationMethod);
					cleanOwner.getCleanInterface().addMethod(
						wrapperInitializationMethod);
					cleanOwner.getCleanInterfaceImplementation().addMethod(
						wrapperInitializationMethod);

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
