package org.caesarj.compiler.util;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.ast.CciWeaveletClassDeclaration;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjCompilationUnit;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

/**
 * This class makes the transformations in the collaboration interfaces and
 * binding and providing classes. It transforms every nested collaboration 
 * interface, as well as the nested interfaces of the providing classes. The
 * nested collaboration interfaces are transformed int overriden classes if
 * the collaboration interface extends another one, or virtual otherwise. The
 * providing nested are turned always into overriden classes since it must 
 * override the nested collaboration interfaces. 
 * It must run before the <code>ClassTransformationFjVisitor</code>.
 * @author Walter Augusto Werner
 */
public class CollaborationInterfaceTransformation 
	extends FjVisitor
{
	

	/**
	 * The environment for generate the interfaces of the classes.
	 */
	protected KjcEnvironment environment;
	
	/**
	 * A "family" of collaboration interfaces must be transformed only by the 
	 * most outer owner.
	 */
	private FjCleanClassDeclaration lastCollaborationInterface;

	/**
	 * A "family" of providing classes must be transformed only by the 
	 * most outer owner.
	 */
	private FjCleanClassDeclaration lastProvidingClass;
	
	/**
	 * A "family" of binding classes must be transformed only by the 
	 * most outer owner.
	 */
	private FjCleanClassDeclaration lastBindingClass;
	
	public CollaborationInterfaceTransformation(KjcEnvironment environment)
	{
		this.environment = environment;
	}
	/**
	 * It will generate the interface of everything that is created...
	 */
	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations)
	{

		String packagePrefix = null;
		if (packageName != null)
			packagePrefix = packageName.getName();
		if (packagePrefix == null || packagePrefix.trim().equals(""))
			packagePrefix = "";
		else
			packagePrefix = packagePrefix + "/";

		super.visitCompilationUnit(
			self,
			packageName,
			importedPackages,
			importedClasses,
			typeDeclarations);

		JTypeDeclaration[] inners = ((FjCompilationUnit) self).getInners();
		for (int i = 0; i < inners.length; i++)
		{
			inners[i].generateInterface(
				environment.getClassReader(),
				null,
				packagePrefix);
		}
	}
	
	/**
	 * Also sets the owner of the classes.
	 */	
	public void visitFjClassDeclaration(
		FjClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls)
	{

		// classes need to know their
		// owners in order to be able to access
		// fields when inheriting
		Object myOwner = owner.get();
		if (myOwner instanceof FjClassDeclaration)
			self.setOwnerDeclaration(myOwner);

		super.visitFjClassDeclaration(
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
	 * Makes all tranformations needed here.
	 *
	 */
	public void visitFjCleanClassDeclaration(
		FjCleanClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls)
	{
		
		if (CModifier.contains(modifiers, Constants.CCI_COLLABORATION)
				&& lastCollaborationInterface == null)
		{
			//Transforms inner interfaces in virtual or override types.
			self.transformInnerInterfaces();
			//Create the empty bodies of the methods
			self.createEmptyMethodBodies();

			//Sets the super class if there is one.
			if (interfaces.length > 0)
			{
				superClass = interfaces[0].getQualifiedName();
				self.setSuperClass(new CClassNameType(superClass));
			
				//if it has more than one it will generate an error.
				if (interfaces.length <= 1)
					self.setInterfaces(CReferenceType.EMPTY);
			}
			//My inners cannot pass by here...
			lastCollaborationInterface = self;
		}
		else if (self.getProviding() != null && lastProvidingClass == null)
		{
			self.setModifiers(self.getModifiers() | Constants.CCI_PROVIDING);
			modifiers = self.getModifiers();
			//Transforms my inner classes into overriden classes.
			self.transformInnerProvidingClasses();
			
			//My super class is my providing class
			superClass = self.getProviding().getQualifiedName();
			self.setSuperClass(new CClassNameType(superClass));
			// My inners cannot pass by here..
			lastProvidingClass = self;
		}
		else if (self.getBinding() != null && lastBindingClass == null)
		{
			self.setModifiers(self.getModifiers() | Constants.CCI_BINDING);
			modifiers = self.getModifiers();
			
			//Sets the right super type of the inner classes, 
			//and sets it as a binding.
			superClass = self.getBindingTypeName();
			self.transformInnerBindingClasses(superClass);
			self.addProvidingAcessor();
			//self.addWrapperRecyclingStructure();
			// My super class is the binding class.
			self.setSuperClass(new CClassNameType(
				superClass));
			
			lastBindingClass = self;
		}

		// Our inner declarations may have further
		// inners, so descend here too.
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
		
		//Ok, I am the last, so set it null..
		if (self == lastCollaborationInterface)
			lastCollaborationInterface = null;
		else if (self == lastProvidingClass)
			lastProvidingClass = null;	
		else if (self == lastBindingClass)
			lastBindingClass = null;			
	}
	/**
	 * 
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
		superClass = self.getBindingTypeName();
		self.setSuperClass(new CClassNameType(superClass));
		self.addAccessors();
		// TODO Auto-generated method stub
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

}

