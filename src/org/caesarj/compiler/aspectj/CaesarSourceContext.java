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
 * $Id: CaesarSourceContext.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

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
