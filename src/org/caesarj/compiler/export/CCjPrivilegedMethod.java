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
 * $Id: CCjPrivilegedMethod.java,v 1.4 2005-05-31 09:02:26 meffert Exp $
 */

package org.caesarj.compiler.export;

import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.TokenReference;

/**
 * Type comment.
 * 
 * @author Jürgen Hallpap
 */
public class CCjPrivilegedMethod extends CSourceMethod {

	private CaesarMember resolvedMember;

	private CMethod baseMethod;

	/**
	 * Constructor for PrivilegedMethod.
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param body
	 * @param families
	 */
	public CCjPrivilegedMethod(CMethod baseMethod) {
		super(
			baseMethod.getOwner(),
			baseMethod.getModifiers(),
			baseMethod.getIdent(),
			baseMethod.getReturnType(),
			new JFormalParameter[0],	// TODO [mef] : Method parameters for debugging
			baseMethod.getParameters(),
			CReferenceType.EMPTY,
			false,
			true,
			new JBlock(
				TokenReference.NO_REF,
				JBlock.EMPTY,
				new JavaStyleComment[0])
			);


		this.baseMethod = baseMethod;
	}

	public CaesarMember getResolvedMember() {
		if (resolvedMember == null) {
			String	declaringSig = 
				CaesarBcelWorld.getInstance().resolve(getOwner()).getSignature();
			resolvedMember =
				CaesarMember.ResolvedMember(
					CaesarMember.METHOD,
					declaringSig,
					getModifiers(),
					baseMethod.getIdent(),
					getSignature());
		}

		return resolvedMember;
	}

}
