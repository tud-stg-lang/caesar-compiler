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
 * $Id: CaesarTokenSource.java,v 1.2 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.patterns.BasicToken;
import org.aspectj.weaver.patterns.BasicTokenSource;
import org.aspectj.weaver.patterns.IToken;

/**
 * @author hallpap
 *
 */
public class CaesarTokenSource extends BasicTokenSource {

	/**
	 * Constructor for CaesarTokenSource.
	 * @param tokens
	 * @param sourceContext
	 */
	public CaesarTokenSource(IToken[] tokens, ISourceContext sourceContext) {
		super(tokens, sourceContext);
	}

	public static CaesarTokenSource createTokenSource(
		String input,
		ISourceContext sourceContext) {
			
		char[] chars = input.toCharArray();

		int i = 0;
		List tokens = new ArrayList();

		while (i < chars.length) {
			char ch = chars[i++];
			switch (ch) {
				case ' ' :
				case '\t' :
				case '\n' :
				case '\r' :
					continue;
				case '*' :
				case '.' :
				case '(' :
				case ')' :
				case '+' :
				case '[' :
				case ']' :
				case ',' :
				case '!' :
				case ':' :
					tokens.add(
						BasicToken.makeOperator(
							new Character(ch).toString(),
							i - 1,
							i - 1));
					continue;
				case '&' :
				case '|' :
					if (i == chars.length) {
						throw new BCException("bad " + ch);
					}
					char nextChar = chars[i++];
					if (nextChar == ch) {
						tokens.add(
							BasicToken.makeOperator(
								new StringBuffer()
									.append(ch)
									.append(ch)
									.toString(),
								i - 2,
								i - 1));
					} else {
						throw new RuntimeException("bad " + ch);
					}
					continue;

				case '\"' :
					int start0 = i - 1;
					while (i < chars.length && !(chars[i] == '\"'))
						i++;
					i += 1;
					tokens.add(
						BasicToken.makeLiteral(
							new String(chars, start0 + 1, i - start0 - 2),
							"string",
							start0,
							i - 1));
				default :
					int start = i - 1;
					while (i < chars.length
						&& Character.isJavaIdentifierPart(chars[i])) {
						i++;
					}
					tokens.add(
						BasicToken.makeIdentifier(
							new String(chars, start, i - start),
							start,
							i - 1));

			}
		}

		return new CaesarTokenSource(
			(IToken[]) tokens.toArray(new IToken[tokens.size()]),
			sourceContext);
	}
}
