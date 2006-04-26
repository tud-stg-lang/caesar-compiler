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
 * $Id: CaesarFormalBinding.java,v 1.4 2006-04-26 16:55:25 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.patterns.FormalBinding;

/**
 * @author Karl Klose
 * 	Wraps the AspectJ-FormalBinding
 */
public class CaesarFormalBinding {
// Attributes	
	private FormalBinding	binding;
// Access
	public FormalBinding	wrappee()
	{ 
		return binding;
	}
// Construction
   public CaesarFormalBinding(ResolvedTypeX type, String name, int index, int start, int end, String fileName) 
   {
 		binding = new FormalBinding(
			type,
			name,
			index,
			start,
			end,
			fileName);	   
   }

	public CaesarFormalBinding( FormalBinding binding )
	{
		this.binding = binding;
	}

// interface
	public String getName() {
		return binding.getName();
	}
	/**
	 * Returns an array containing the wrapped objects 
	 * @param declares	An array of CaesarFormalBindings
	 * @return	An array of the wrappees 
	 */
	public static FormalBinding[] wrappees(CaesarFormalBinding[] bindings) {
		FormalBinding[] ret = new FormalBinding[bindings.length];
		for(int i=0;i<bindings.length;i++)
			ret[i] = bindings[i].wrappee();
		return ret;
	}
}
