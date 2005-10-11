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
 * $Id: CCjIfcSourceClass.java,v 1.6 2005-10-11 14:59:55 gasiunas Exp $
 */

package org.caesarj.compiler.export;

import java.util.ArrayList;

import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * @author Vaidas Gasiunas
 *
 * Mixin source class. Keeps reference to origin class and uses it for resolution of pointcuts.
 */
public class CCjIfcSourceClass extends CCjSourceClass 
{
	// the class from which the mixin was generated
	protected CjMixinInterfaceDeclaration _mixinIfcDecl;
	
	public CCjIfcSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		boolean deprecated,
		boolean synthetic,
		CCompilationUnit cunit, 
		CjMixinInterfaceDeclaration decl)
	{
		super(
			owner,
			where,
			modifiers,
			ident,
			qualifiedName,
			deprecated,
			synthetic,
			false,
			cunit,
			decl,			
			null);
		_mixinIfcDecl = decl;
	}
	
	public CCjSourceClass getImplClass() {
		return _mixinIfcDecl.getCorrespondingClassDeclaration().getCjSourceClass();
	}

	public CCjSourceClass getRegistryClass() {
		CjClassDeclaration registry =  _mixinIfcDecl.getCorrespondingClassDeclaration().getRegistryClass();
		if (registry != null)
		    return registry.getCjSourceClass();
		return null;
	}
	
	/**
	 * Adds all applicable methods (JLS 15.12.2.1) to the specified container.
	 * @param	container	the container for the methods
	 * @param	ident		method invocation name
	 * @param	actuals		method invocation arguments
	 */
	public void collectApplicableMethods(
		CTypeContext context,
		ArrayList container,
		String ident,
		CType[] actuals)
	{
		super.collectApplicableMethods(context, container, ident, actuals);
		
		CMethod[] implMeth = getImplClass().getMethods();
		
		// include static methods of the corresponding implementation class
		for (int i = 0; i < implMeth.length; i++)
		{
			if (implMeth[i].isApplicableTo(context, ident, actuals) && implMeth[i].isStatic())
			{
				container.add(implMeth[i]);
			}
		}
	}
	
	/**
	 * Searches the class or interface to locate declarations of fields that are
	 * accessible.
	 * 
	 * @param	caller		the class of the caller
	 * @param     primary         the class of the primary expression (can be null)
	 * @param	ident		the simple name of the field
	 * @return	the field definition
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CField lookupField(CClass caller, CClass primary, String ident)
		throws UnpositionedError
	{
		CField field = getField(ident);

		if (field != null && field.isAccessible(primary, caller)) {
			return field;
		}
		field = lookupSuperField(caller, primary, ident);
		if (field != null && field.isAccessible(primary, caller)) {
			return field;
		}
		
		return getImplClass().lookupStaticClassField(caller, primary, ident);
	}
}