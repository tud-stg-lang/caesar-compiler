package org.caesarj.compiler.ast;


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
	 * @return the CI that the class binds.
	 */
	public CReferenceType getBinding()
	{
		return decl instanceof CciClassDeclaration
			? ((CciClassDeclaration)decl).getBinding()
			: null;
	}

	/**
	 * @return the CI that the class implements.
	 */
	public CReferenceType getImplementation()
	{
		return decl instanceof CciClassDeclaration
			? ((CciClassDeclaration)decl).getImplementation()
			: null;
	}
	
	/**
	 * Sets the methods of this source class.
	 * @param methods
	 */
	public void setMethods(CMethod[] methods)
	{
		this.methods = methods;
	}

	public boolean isWeaveletClass()
	{
		return false;
	}

}
