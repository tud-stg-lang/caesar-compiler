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
