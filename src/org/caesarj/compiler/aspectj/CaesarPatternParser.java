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
 * $Id: CaesarPatternParser.java,v 1.4 2005-03-10 12:32:09 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.IToken;
import org.aspectj.weaver.patterns.ITokenSource;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;

/**
 * @author Karl Klose
 *
 *	This class encapsulates the AspectJ-Patternparser.
 */
public class CaesarPatternParser {
	public class CaesarParserException extends RuntimeException
	{
		private ParserException e;
		public CaesarParserException( ParserException e)
		{
			this.e = e;
		}
		
		public String	getMessage()
		{
			return e.getMessage();
		}
	}
//Attribute
	private PatternParser	patternParser;
	private ITokenSource	tokenSource;
	
// Counstruction
	public CaesarPatternParser( String input, CaesarSourceContext context )
	{
		tokenSource = CaesarTokenSource.createTokenSource(input,context);
		patternParser = new PatternParser(tokenSource);			
	}
// Interface
	public CaesarDeclare parseDeclare() {
		CaesarDeclare ret;
		try{
			ret = new CaesarDeclare(patternParser.parseDeclare());
		}
		catch(ParserException ex)
		{
			throw new CaesarParserException(ex);
		}
		return ret;
	}

	public CaesarPointcut parsePointcut() {
		CaesarPointcut ret;
		try {
			ret = new CaesarPointcut(patternParser.parsePointcut());
		}
		catch(ParserException ex) {
			throw new CaesarParserException(ex);
		}
		if (tokenSource.peek() != IToken.EOF) {
			throw new CaesarParserException(new ParserException("symbols found after pointcut", tokenSource.peek()));
		}
		return ret;
	}
}
