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
 * $Id: CjInterfaceDeclaration.java,v 1.18 2005-10-11 14:59:55 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CCompilationUnit;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

// FJKEEP 
public class CjInterfaceDeclaration extends JInterfaceDeclaration {

	public CjInterfaceDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment) {
		super(
			where,
			modifiers,
			ident,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}

	protected void checkModifiers(CContext context) throws PositionedError {
		int modifiers = getModifiers();

		check(
			context,
			CModifier.isSubsetOf(
				modifiers,
				getAllowedModifiers()),
			KjcMessages.NOT_INTERFACE_MODIFIERS,
			CModifier.toString(
				CModifier.notElementsOf(
					modifiers,
					getAllowedModifiers())));



		// JLS 9.1.1 : The access modifiers protected and private pertain only
		// to member interfaces within a directly enclosing class declaration.
		check(
			context,
			(isNested()
				&& getOwner().getCClass().isClass()
				&& !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PROTECTED | ACC_PRIVATE),
			KjcMessages.INVALID_INTERFACE_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_PRIVATE)));

		// JLS 9.1.1 : The access modifier static pertains only to member interfaces.
		check(
			context,
			isNested() || !CModifier.contains(modifiers, ACC_STATIC),
			KjcMessages.INVALID_INTERFACE_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_STATIC)));

		// JLS 8.5.2 : Member interfaces are always implicitly static.
		if (isNested()) {
			setModifiers(modifiers | ACC_STATIC);
		}

		// JLS 9.5 : A member type declaration in an interface is implicitly
		// static and public.
		if (isNested() && getOwner().getCClass().isInterface()) {
			setModifiers(modifiers | ACC_STATIC | ACC_PUBLIC);
		}
	}
	
	protected CSourceClass createSourceClass(CClass owner, CCompilationUnit cunit, String prefix) {
        return new CCjSourceClass(
        		owner, 
        		getTokenReference(), 
        		modifiers, 
        		ident, 
        		prefix + ident, 
        		isDeprecated(), 
        		false, 
        		false,
        		cunit,
        		this);
    }

	/**
	 * Walter
	 * Returns the modifiers that are allowed in this definition.
	 */
	protected int getAllowedModifiers()	{
		return 	ACC_PUBLIC 
			| ACC_PROTECTED
			| ACC_PRIVATE
			| ACC_ABSTRACT
			| ACC_STATIC
			| ACC_STRICT
			| ACC_INTERFACE
			//Jurgen's
			| ACC_PRIVILEGED 
			| ACC_CROSSCUTTING 
			| ACC_DEPLOYED;
	}		
}
