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
 * $Id: CjProceedDeclaration.java,v 1.6 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.export.CCjProceed;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Type comment.
 * 
 * @author Jürgen Hallpap
 */
public class CjProceedDeclaration extends CjMethodDeclaration {

	/** The name of the enclosing advice-method.*/
	private String adviceName;

	/**
	 * Constructor for CaesarProceedMethodDeclaration.
	 * @param where
	 * @param returnType
	 * @param ident
	 * @param parameters
	 * @param adviceName
	 */
	public CjProceedDeclaration(
		TokenReference where,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		String adviceName) {
		super(
			where,
			ACC_STATIC,
			returnType,
			ident,
			parameters,
			new CReferenceType[0],
			new JBlock(where, JBlock.EMPTY, new JavaStyleComment[0]),
			null,
			new JavaStyleComment[0]);

		this.adviceName = adviceName;
	}

	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {
		
		try {

			CType[] parameterTypes = new CType[parameters.length];
			CBinaryTypeContext typeContext =
				new CBinaryTypeContext(
					context.getClassReader(),
					context.getTypeFactory(),
					context,
					(modifiers & ACC_STATIC) == 0);

			returnType = returnType.checkType(typeContext);
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterTypes[i] = parameters[i].checkInterface(typeContext);
			}

			for (int i = 0; i < exceptions.length; i++) {
				exceptions[i] =
					(CReferenceType) exceptions[i].checkType(typeContext);
			}

			setInterface(
				new CCjProceed(
					context.getCClass(),
					ident,
					returnType,
					parameterTypes,
					adviceName));

			return (CSourceMethod) getMethod();
		} catch (UnpositionedError cue) {
			throw cue.addPosition(getTokenReference());
		}

	}

	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkBody1(CClassContext)
	 */
	public void checkBody1(CClassContext context) throws PositionedError {
		//do nothing, the body is not important, it will be generated during code generation		
	}

}
