/*
 * Created on 08.12.2003
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
	private CaesarAdviceKind(String name, int key, int precedence, boolean isAfter, boolean isCflow)
	{
		kind = new AdviceKind(name,key,precedence,isAfter,isCflow);
	}
	
	/*
	 * Returns the encapsulated object of type AdviceKind
	 */	  
	public	AdviceKind wrappee()
	{
		return kind;
	}
// Constant Advicekinds
	public static final CaesarAdviceKind Before         = new CaesarAdviceKind("before", 1, 0, false, false);
	public static final CaesarAdviceKind After          = new CaesarAdviceKind("after", 2, 0, true, false);
	public static final CaesarAdviceKind AfterThrowing  = new CaesarAdviceKind("afterThrowing", 3, 0, true, false);
	public static final CaesarAdviceKind AfterReturning = new CaesarAdviceKind("afterReturning", 4, 0, true, false);
	public static final CaesarAdviceKind Around         = new CaesarAdviceKind("around", 5, 0, false, false);
}
