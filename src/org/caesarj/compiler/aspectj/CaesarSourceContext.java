package org.caesarj.compiler.aspectj;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.caesarj.util.TokenReference;

/**
 * @author hallpap
 *
 */
public class CaesarSourceContext implements ISourceContext {

	private TokenReference tokenReference;

	/**
	 * Constructor for CaesarSourceContext.
	 */
	public CaesarSourceContext(TokenReference tokenReference) {
		super();

		this.tokenReference = tokenReference;
	}

	/**
	 * @see org.aspectj.weaver.ISourceContext#makeSourceLocation(IHasPosition)
	 */
	public ISourceLocation makeSourceLocation(IHasPosition position) {
		return new SourceLocation(
			tokenReference.getPath(),
			tokenReference.getLine());
	}

	/**
	 * @see org.aspectj.weaver.ISourceContext#makeSourceLocation(int)
	 */
	public ISourceLocation makeSourceLocation(int line) {
		return new SourceLocation(
			tokenReference.getPath(),
			tokenReference.getLine());
	}

}
