/*
 * Created on 08.12.2003
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.PatternParser;
import org.caesarj.compiler.aspectj.CaesarDeclare;

/**
 * @author Karl Klose
 *
 *	This class encapsulates the AspectJ-Patternparser.
 */
public class CaesarPatternParser {
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
		return new CaesarDeclare(patternParser.parseDeclare());
	}

	public CaesarPointcut parsePointcut() {
			return new CaesarPointcut( patternParser.parsePointcut() );
	}
}
