/*
 * Created on 06.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.TokenReference;

/**
 * @author Vaidas Gasiunas
 *
 * Mixin source class. Keeps reference to origin class and uses it for resolution of pointcuts.
 */
public class CCjMixinSourceClass extends CCjSourceClass 
{
	// the class from which the mixin was generated
	protected CCjSourceClass _originClass;
	
	public CCjMixinSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JTypeDeclaration decl,
		CCjSourceClass originClass)
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
			decl,
			null);
		
		_originClass = originClass;
	}
	
	/**
	 * Returns the resolvedPointcuts.
	 * @return List (of ResolvedPointcut)
	 */
	public List getResolvedPointcuts()
	{
		List ret = new ArrayList();
		for (Iterator it = _originClass.resolvedPointcuts.iterator(); it.hasNext();)
		{
			CaesarMember	resolvedPointcutDef = ((CaesarMember)it.next()); 		
			ret.add( resolvedPointcutDef.wrappee() );
		}

		if (getSuperClass() != null
			&& CModifier.contains(
				getSuperClass().getModifiers(),
				ACC_CROSSCUTTING))
		{

			ret.addAll(
				((CCjSourceClass) getSuperClass()).getResolvedPointcuts());
		}

		return ret;
	}
}
