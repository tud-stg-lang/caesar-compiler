/*
 * Created on 08.12.2003
 */
package org.caesarj.compiler.aspectj;

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
// Counstruction
	public CaesarPatternParser( String input, CaesarSourceContext context )
	{
		patternParser = new PatternParser(
			CaesarTokenSource.createTokenSource(input,context)
			);
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
		try{
			ret = new CaesarPointcut( patternParser.parsePointcut() );
		}
		catch(ParserException ex)
		{
			throw new CaesarParserException(ex);
		}
		return ret;
	}
}
