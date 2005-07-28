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
 * $Id: CjProceedExpression.java,v 1.4 2005-07-28 11:44:36 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.CMethodNotFoundError;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * The proceed expression.
 * Usage is restricted to around advices.
 * 
 * @author Jürgen Hallpap
 */
public class CjProceedExpression
	extends JMethodCallExpression
	implements CaesarConstants {

	/**
	 * Constructor for ProceedExpression.
	 * @param where
	 * @param JExpression[]
	 * @param comments
	 */
	public CjProceedExpression(TokenReference where, JExpression[] args) {

		super(
			where,
			null,
			"PROCEED EXPRESSION",
			addAroundClosureArgument(args, where));

	}

	/**
	 * Appends an additional argument for the aroundClosure.
	 */
	private static JExpression[] addAroundClosureArgument(JExpression[] args, TokenReference where) {
		JExpression[] newArgs = new JExpression[args.length + 1];

		System.arraycopy(args, 0, newArgs, 0, args.length);

		newArgs[newArgs.length - 1] =
			new JNameExpression(
				where,
				AROUND_CLOSURE_PARAMETER);

		return newArgs;

	}

	/**
	 * Set the ident and prefix of the mehtod call, then analyse.
	 * 
	 * @param context
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError {

		prefix = new JThisExpression(getTokenReference());
		ident =
			(context.getMethodContext().getCMethod().getIdent()
				+ PROCEED_METHOD)
				.intern();
		return super.analyse(context);
	}

	/**
	 * Converts a potential error message.
	 */
	protected void findMethod(
		CExpressionContext context,
		CClass local,
		CType[] argTypes)
		throws PositionedError {

		try {

			super.findMethod(context, local, argTypes);
		} catch (CMethodNotFoundError e) {
			CType[] visibleArgTypes = new CType[argTypes.length - 1];
			System.arraycopy(
				argTypes,
				0,
				visibleArgTypes,
				0,
				argTypes.length - 1);
			throw new CMethodNotFoundError(
				getTokenReference(),
				this,
				PROCEED_METHOD,
				visibleArgTypes);
		}
	}

}
