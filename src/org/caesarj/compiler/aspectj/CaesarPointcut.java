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
 * $Id: CaesarPointcut.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.Pointcut;

/**
 * @author Karl Klose
 * This class is a wrapper for an AspectJ-Pointcut
 */
public class CaesarPointcut {
// Attributes	
	private Pointcut	pointcut;
// Construction
	public CaesarPointcut( Pointcut pointcut ) 
	{	
		this.pointcut = pointcut;
	}
// Accesors
	public Pointcut	wrappee()
	{
		return pointcut;
	}
// Functions
	static public CaesarPointcut	makeMatchesNothing()
	{
		return new CaesarPointcut(Pointcut.makeMatchesNothing(Pointcut.SYMBOLIC));
	}
	
	public CaesarPointcut resolve(CaesarScope scope) {
			pointcut = pointcut.resolve(scope);		
			return this;
		}	
/*
 * PerClause factory methods
 * 	Since perClauses are pointcuts, they are created and wrapped here. As a result,
 *  the compiler classes kneedn't know the PerClause-classes.
 */ 
	public static CaesarPointcut createPerSingleton()
	{
		return new CaesarPointcut( new PerSingleton() );
	}
}
