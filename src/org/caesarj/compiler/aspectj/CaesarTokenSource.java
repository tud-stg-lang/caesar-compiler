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
