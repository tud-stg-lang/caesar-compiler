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
 * $Id: CaesarAdviceKind.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.AdviceKind;

/**
 * @author Karl Klose
 *	This class is a wrapper for AspectJ advicekinds
 */
public class CaesarAdviceKind {
// Attributes	
	private AdviceKind	kind;
// Construction
	private CaesarAdviceKind( AdviceKind kind )
	{
		this.kind = kind;
	}
	/*
	 * Returns the encapsulated object of type AdviceKind
	 */	  
	public	AdviceKind wrappee()
	{
		return kind;
	}
// Constant Advicekinds
   public static final CaesarAdviceKind Before 		   = new CaesarAdviceKind(AdviceKind.Before);
   public static final CaesarAdviceKind After          = new CaesarAdviceKind(AdviceKind.After);
   public static final CaesarAdviceKind AfterThrowing  = new CaesarAdviceKind(AdviceKind.AfterThrowing);
   public static final CaesarAdviceKind AfterReturning = new CaesarAdviceKind(AdviceKind.AfterReturning);
   public static final CaesarAdviceKind Around         = new CaesarAdviceKind(AdviceKind.Around);

}
