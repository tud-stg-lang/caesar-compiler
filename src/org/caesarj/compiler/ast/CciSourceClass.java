package org.caesarj.compiler.ast;

import java.util.Hashtable;

import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * This class represents a source class that can have either implementations 
 * or bindings. It just adds these data to the Fj implementation.
 * 
 * @author Walter Augusto Werner
 */
public class CciSourceClass 
	extends FjSourceClass
	implements CciClass
{
	/**
	 * The CIs that the class binds.
	 */
	private CReferenceType[] bindings;
	/**
	 * The CIs that the class implements.
	 */
	private CReferenceType[] implementations;
	
	/**
	 * 
	 * @param owner
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param qualifiedName
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param decl
	 */
	public CciSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JTypeDeclaration decl)
	{
		super(
			owner,
			where,
			modifiers,
			ident,
			qualifiedName,
			typeVariables,
			deprecated,
			synthetic,
			decl);
		
	}



	/**
	 * Overriden for set the bindings and implementations.
	 * 
	 * @see at.dms.kjc.CClass#close(at.dms.kjc.CReferenceType[], java.util.Hashtable, at.dms.kjc.CMethod[])
	 */
	public void close(
		CReferenceType[] interfaces,
		Hashtable fields,
		CMethod[] methods)
	{
		if (decl instanceof CciClassDeclaration)
		{
			bindings = ((CciClassDeclaration)decl).getBindings();
			implementations = ((CciClassDeclaration)decl).getImplementations();
		}
		super.close(interfaces, fields, methods);
	}

	/**
	 * @return the CIs that the class binds.
	 */
	public CReferenceType[] getBindings()
	{
		return decl instanceof CciClassDeclaration
			? ((CciClassDeclaration)decl).getBindings()
			: null;
	}

	/**
	 * @return the CIs that the class implements.
	 */
	public CReferenceType[] getImplementations()
	{
		return decl instanceof CciClassDeclaration
			? ((CciClassDeclaration)decl).getImplementations()
			: null;
	}
}
