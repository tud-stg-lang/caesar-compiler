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
 * $Id: CCjPrivilegedField.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.export;

import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CVoidType;

/**
 * A privileged field encapsulates a non-visible field and 
 * provides the corresponding access-methods.
 * 
 * @author Jürgen Hallpap
 */
public class CCjPrivilegedField extends CSourceField {

	/** the encapsulated non-visible baseField.*/
	private CField baseField;

	/** the reader access-method.*/
	private CMethod reader;

	/** the writer access-method.*/
	private CMethod writer;

	/** the resolved-member for attribute creation*/
	private CaesarMember resolvedMember;

	/** the declaring type*/
	private String declaringSig;

	/**
	 * Constructor for PrivilegedField.
	 * @param baseField
	 * @param aspect
	 */
	public CCjPrivilegedField(
		CField baseField,
		CCjSourceClass aspect) {
		super(
			baseField.getOwner(),
			baseField.getModifiers(),
			baseField.getIdent(),
			baseField.getType(),
			baseField.isDeprecated(),
			baseField.isSynthetic());

		this.baseField = baseField;

		this.declaringSig = CaesarBcelWorld.getInstance().resolve(owner).getSignature();

		String aspectType = CaesarBcelWorld.getInstance().resolve(aspect).getSignature();

		CaesarMember field =
			CaesarMember.Member(
				CaesarMember.FIELD,
				declaringSig,
				getModifiers(),
				getIdent(),
				getType().getSignature());
			
		CaesarMember readerMember =
			CaesarMember.privilegedAccessMethodForFieldGet(aspectType,field);

		CType[] readerParameterTypes = { getOwnerType()};


		reader =
			new CSourceMethod(
				owner,
				readerMember.getModifiers(),
				readerMember.getName(),
				getType(),
				readerParameterTypes,
				CReferenceType.EMPTY,
				false,
				true,
				null);

		
		CaesarMember writerMember = 
			CaesarMember.privilegedAccessMethodForFieldSet(aspectType,field);
/*			new CaesarMember(
			AjcMemberMaker.privilegedAccessMethodForFieldSet(
				TypeX.forSignature(aspectType), field.wrappee()));
*/
		CType[] writerParameterTypes = { getOwnerType(), getType()};
		writer =
			new CSourceMethod(
				owner,
				writerMember.getModifiers(),
				writerMember.getName(),
				new CVoidType(),
				writerParameterTypes,
				CReferenceType.EMPTY,
				false,
				true,
				null);

	}

	/**
	 * Gets the appropriate accessor.
	 * 
	 * @param isReadAccess
	 * @return CMethod
	 */
	public CMethod getAccessMethod(boolean isReadAccess) {
		if (isReadAccess)
			return reader;
		else
			return writer;
	}

	/**
	 * Gets the resolvedMember.
	 * 
	 * @return ResolvedMember
	 */
	public CaesarMember getResolvedMember() {
		if (resolvedMember == null) {
			resolvedMember = CaesarMember.ResolvedMember(
					CaesarMember.FIELD,
					declaringSig,
					getModifiers(),
					getIdent(),
					getType().getSignature()
				);
		}

		return resolvedMember;
	}

}
