package org.caesarj.compiler.ast;

import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;

public class FjOverrideClassDeclaration
	extends FjVirtualClassDeclaration
	implements FjOverrideable
{

	public FjOverrideClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType binding,
		CReferenceType providing,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment)
	{
		super(
			where,
			modifiers | FJC_OVERRIDE,
			ident,
			typeVariables,
			superClass,
			binding,
			providing,
			wrappee,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
			
		// Walter: the "bindings" was inserted as parameter.			
	}
	protected FjCleanClassInterfaceDeclaration newInterfaceDeclaration(
		TokenReference where,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{
		return new FjOverridingCleanClassInterfaceDeclaration(
			where,
			ident,
			modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET),			
			interfaces,
			methods,
			getOwnerDeclaration(),
			this);
	}
	protected FjCleanClassIfcImplDeclaration newIfcImplDeclaration(
		TokenReference where,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{
		return new FjOverridingCleanClassIfcImplDeclaration(
			where,
			ident,
			(modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET)) | ACC_PUBLIC,			
			interfaces,
			methods,
			getOwnerDeclaration(),
			this);
	}

	public String toSuperClass(String name)
	{
		return FjConstants.toProxyName(name);
	}

	public static void setOverridingSuperClass(
		FjOverrideable instance,
		Compiler compiler)
	{
		CClass owner = instance.getCClass().getOwner().getCClass();
		CClass ownerSuper = owner;
		CReferenceType superClass = null;
		CClass innerSuper = null;

		FjTypeSystem fjts = new FjTypeSystem();
		while (superClass == null
			&& !fjts.superClassOf(ownerSuper).getQualifiedName().equals(
				"java/lang/Object")
			&& !fjts.superClassOf(ownerSuper).getQualifiedName().equals(
				FjConstants.CHILD_TYPE_NAME)
			&& !fjts.superClassOf(ownerSuper).getQualifiedName().equals(
				FjConstants.CHILD_IMPL_TYPE_NAME))
		{

			// the nested class we are looking for
			// is positioned in either the clean
			// ifc or the actual superclass (iff
			// the superclass is not clean)
			if (FjConstants
				.isIfcImplName(
					fjts.superClassOf(ownerSuper).getQualifiedName()))
				ownerSuper =
					fjts
						.superClassOf(ownerSuper)
						.getInterfaces()[0]
						.getCClass();
			else
				ownerSuper = fjts.superClassOf(ownerSuper);

			CReferenceType[] inners = ownerSuper.getInnerClasses();
			for (int i = 0; innerSuper == null && i < inners.length; i++)
			{
				if (inners[i]
					.getIdent()
					.equals(
						FjConstants.toIfcName(
							instance.getCClass().getIdent())))
					innerSuper = inners[i].getCClass();
			}
			if (innerSuper != null)
			{
				superClass =
					new CClassNameType(
						instance.toSuperClass(innerSuper.getQualifiedName()));
			}
		}
		
		if (superClass == null)
		{
			// If it has a providing class, it generates other message
			if (instance.getProviding() != null)
			{
				compiler.reportTrouble(
					new UnpositionedError(
						CaesarMessages.PROVIDING_DEFINES_OTHER_NESTED, 
							FjConstants.removeFamilyJ(
								instance.getCClass().getQualifiedName()))
							.addPosition(instance.getTokenReference()));
			}
			else
			{
				compiler.reportTrouble(
					new UnpositionedError(
						CaesarMessages.NO_MATCHING_OVERRIDDEN_CLASS).addPosition(
						instance.getTokenReference()));
			}
		}
		else
			instance.setSuperClass(superClass);
	}

	public Vector inherritConstructorsFromBaseClass(Hashtable markedVirtualClasses)
		throws PositionedError
	{
		Vector messages = new Vector();

		// in this case there is nothing to inherrit			
		if (getSuperClass()
			.getQualifiedName()
			.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
			return messages;

		Constructors constructorsToSupport = null;

		try
		{
			// is the superclass being compiled, too and ready to be used (<=> marked)?
			String superClassKey =
				FjConstants.toFullQualifiedBaseName(
					getSuperClass().getQualifiedName(),
					self);
			FjCleanClassDeclaration classDecl =
				(FjCleanClassDeclaration) markedVirtualClasses.get(
					superClassKey);
			if (classDecl != null)
				constructorsToSupport = new UncheckedConstructors(classDecl);

			// or is the superclass available binary
			if (constructorsToSupport == null)
			{
				CReferenceType superBaseType =
					new CClassNameType(
						FjConstants.toFullQualifiedBaseName(
							getSuperClass().getIdent(),
							self));
				superBaseType = (CReferenceType) superBaseType.checkType(self);
				constructorsToSupport =
					new CheckedConstructors(superBaseType.getCClass());
			}
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition(getTokenReference());
		}

		return assertConstructorsAreAvailable(constructorsToSupport);
	}
}
