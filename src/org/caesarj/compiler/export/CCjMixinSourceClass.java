/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CCjMixinSourceClass.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
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
