package org.caesarj.compiler.family;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.CciInterfaceDeclaration;
import org.caesarj.compiler.ast.CciWeaveletClassDeclaration;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.compiler.ast.FjVisitor;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JCompilationUnit;
import org.caesarj.compiler.ast.JInterfaceDeclaration;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.compiler.ast.JPackageName;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;

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
	 * The compiler to report errors.
	 */
	protected CompilerBase compiler;	
	
	/**
	 * A "family" of collaboration interfaces must be transformed only by the 
	 * most outer owner.
	 */
	private CciInterfaceDeclaration lastCollaborationInterface;

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
	
	public CollaborationInterfaceTransformation(KjcEnvironment environment, 
		CompilerBase compiler)
	{
		this.environment = environment;
		this.compiler = compiler;
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

		JTypeDeclaration[] inners = self.getInners();
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
		JClassDeclaration self,
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
		if (myOwner instanceof JClassDeclaration)
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
		if (self.getProviding() != null && lastProvidingClass == null)
		{
			self.setModifiers(self.getModifiers() | ClassfileConstants2.CCI_PROVIDING);
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
			self.setModifiers(
				self.getModifiers() 
				| ClassfileConstants2.CCI_BINDING
				| ClassfileConstants2.ACC_CROSSCUTTING);
			modifiers = self.getModifiers();
			
			//Sets the right super type of the inner classes, 
			//and sets it as a binding.
			superClass = self.getBindingTypeName();
			self.transformInnerBindingClasses(self);
			self.addProvidingAcessor();
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
		if (self == lastProvidingClass)
			lastProvidingClass = null;	
		else if (self == lastBindingClass)
			lastBindingClass = null;			
	}
	/**
	 * Sets super class and create acessors.
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

		if (! (owner.get() instanceof JClassDeclaration))
		{
			superClass = self.getBindingTypeName();
			self.setSuperClass(new CClassNameType(superClass));
			self.addAccessors();
			self.setModifiers(
				self.getModifiers() 
				| ClassfileConstants2.ACC_CROSSCUTTING);
			modifiers = self.getModifiers();
		}
		else
		{
			compiler.reportTrouble(
				new PositionedError(self.getTokenReference(), 
					CaesarMessages.WEAVELET_NESTED, ident));
		}
		
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


	/**
	 * Generates code for wrapper when it is explicitly declared.
	 */
	public void visitFjVirtualClassDeclaration(
		FjVirtualClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls)
	{
		if (self.getWrappee() != null)
		{
			try
			{
				TypeFactory typeFactory = environment.getTypeFactory();
				self.setTypeFactory(typeFactory);
				self.addInternalWrapperRecyclingStructure(typeFactory);
		
			}
			catch (PositionedError e)
			{
				compiler.reportTrouble(e);
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

	/**
	 * Transform the collaboration interfaces.
	 */
	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods)
	{
		if (CModifier.contains(modifiers, ClassfileConstants2.CCI_COLLABORATION)
			&& lastCollaborationInterface == null)
		{
			CciInterfaceDeclaration cciSelf = (CciInterfaceDeclaration) self;
			if (! (owner.get() instanceof JCompilationUnit))
			{
				compiler.reportTrouble(
					new PositionedError(
						self.getTokenReference(), 
						CaesarMessages.OWNER_IS_NOT_CI,
						ident));
			}
			else
			{
				try
				{
					FjCleanClassDeclaration classRepresentation 
						= cciSelf.createCleanClassRepresentation();
						
					((JCompilationUnit) owner.get()).replace(
						self, classRepresentation);
				}
				catch(PositionedError e)
				{
					compiler.reportTrouble(e);
				}
			}
			//My inners cannot pass by here...
			lastCollaborationInterface = cciSelf;
		}
		else if (self instanceof CciInterfaceDeclaration)
		{
			CciInterfaceDeclaration cciSelf = (CciInterfaceDeclaration)self;
			Object oldOwner = owner.get();
			owner.set(self);
			JTypeDeclaration[] inners = cciSelf.getInners();
			for (int i = 0; i < inners.length; i++)
			{
				if (inners[i] instanceof JClassDeclaration)
					((JClassDeclaration) inners[i]).setOwnerDeclaration(self);
				inners[i].accept(this);
			}
			owner.set(oldOwner);
		}
		//Ok, I am the last, so set it null..
		if (self == lastCollaborationInterface)
			lastCollaborationInterface = null;		
	}

}

