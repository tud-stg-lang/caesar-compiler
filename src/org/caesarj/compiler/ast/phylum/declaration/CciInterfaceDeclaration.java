package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.visitor.KjcVisitor;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * AST element for interface declaration.
 * 
 * @author Walter Augusto Werner
 */
public class CciInterfaceDeclaration 
	extends FjInterfaceDeclaration
{

	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciInterfaceDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
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
			modifiers,
			ident,
			typeVariables,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}
	/**
	 * @return true if it is a collaboration interface, false otherwise.
	 */
	public boolean isCollaborationInterface()
	{
		return CModifier.contains(modifiers, CCI_COLLABORATION);
	}
	
	/**
	 * Returns the modifiers that are allowed for interfaces.
	 */
	protected int getAllowedModifiers()
	{
		return 	super.getAllowedModifiers()	
			| CCI_COLLABORATION;
	}
	
	/**
	 * Sets its own interfaces and the source class' interfaces.
	 * @param interfaces
	 */
	public void setInterfaces(CReferenceType[] interfaces)
	{
		this.interfaces = interfaces;
		sourceClass.setInterfaces(interfaces);
	}
	/**
	 * Creates the class representation of collaboration interfaces.
	 * @return
	 * @throws PositionedError
	 */
	public FjCleanClassDeclaration createCleanClassRepresentation()
		throws PositionedError
	{
		CClassNameType superClass = null;
		//Sets the super class if there is one.
		if (interfaces.length == 1)
			superClass = new CClassNameType(interfaces[0].getQualifiedName());
		else if (interfaces.length > 0)
		{
			throw new PositionedError(
					getTokenReference(), 
					CaesarMessages.CI_MULTIPLE_SUPER_TYPE,
					ident);
		}		
		
		return new FjCleanClassDeclaration(
			getTokenReference(),
			modifiers & ~(ACC_INTERFACE | ACC_ABSTRACT) 
				| FJC_CLEAN | CCI_COLLABORATION,
			ident,
			typeVariables,
			superClass,
			null,
			null,
			null,
			CReferenceType.EMPTY,
			fields,
			createEmptyMethods(),
			transformInnerInterfaces(),
			body,
			null,
			null);
	}
	
	
	/**
	 * Creates the empty classes for all interfaces it finds. It has to be
	 * done for the interfaces that are defined inside Collaboration Intefaces.
	 * @param ownerName
	 * @return 
	 */
	public JTypeDeclaration[] transformInnerInterfaces() 
		throws PositionedError
	{
		ArrayList innerProxies = new ArrayList(inners.length);
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i] instanceof CciInterfaceDeclaration)
				inners[i] =
					((CciInterfaceDeclaration)inners[i])
						.createEmptyVirtualDeclaration(this);
		}

		return inners;
		
	}
	
	/**
	 * Does it has super interfaces?
	 * @return
	 */
	public boolean hasSuper()
	{
		return interfaces != null && interfaces.length > 0;
	}
	
	/**
	 * Creates an empty virtual class declaration using the protocol
	 * of the interface. 
	 * 
	 * @param owner
	 * @param hasSuper
	 * @return
	 */
	public FjVirtualClassDeclaration createEmptyVirtualDeclaration(
		JTypeDeclaration owner) 
		throws PositionedError
	{
		CClassNameType superClass = null;
		if (interfaces.length == 1)
		{
			superClass = new CClassNameType(interfaces[0].getQualifiedName());
		}
		else if (interfaces.length > 0)
		{
			throw new PositionedError(
					getTokenReference(), 
					CaesarMessages.CI_MULTIPLE_SUPER_TYPE,
					ident);
		}
		
		
		int newModifiers = (~ACC_ABSTRACT & ~ACC_INTERFACE & modifiers) 
			| CCI_COLLABORATION;
		FjVirtualClassDeclaration returnClass;
		if (CModifier.contains(modifiers, FJC_OVERRIDE))
			returnClass =
				new FjOverrideClassDeclaration(
					getTokenReference(), 
					newModifiers,
					ident,
					typeVariables,
					superClass,
					null, 
					null,
					null,
					CReferenceType.EMPTY,
					new JFieldDeclaration[0],
					createEmptyMethods(),
					transformInnerInterfaces(),
					new JPhylum[0],
					null,
					null);
		else
			returnClass =
				new FjVirtualClassDeclaration(
					getTokenReference(), 
					newModifiers,
					ident,
					typeVariables,
					superClass,
					null,
					null,
					null,
					CReferenceType.EMPTY,
					new JFieldDeclaration[0],
					createEmptyMethods(),
					transformInnerInterfaces(),
					new JPhylum[0],
					null,
					null);
					
		return returnClass;
	}
	
	/**
	 * Creates empty implementation of the interface methods.
	 * @return
	 */
	protected JMethodDeclaration[] createEmptyMethods()
	{
		ArrayList returnMethods = new ArrayList(methods.length);
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjCleanMethodDeclaration)
				returnMethods.add(
					((FjCleanMethodDeclaration)methods[i]).createEmptyMethod());
		}
		return (JMethodDeclaration[])returnMethods.toArray(
			new JMethodDeclaration[returnMethods.size()]);
	}

	/* (non-Javadoc)
	 * @see org.caesarj.compiler.ast.JInterfaceDeclaration#accept(org.caesarj.compiler.ast.KjcVisitor)
	 */
	public void accept(KjcVisitor p) {
		p.visitCciInterfaceDeclaration(this,
					 getCClass().getModifiers(),
					 sourceClass.getIdent(),
					 interfaces,
					 body,
					 methods);
 	}

}
