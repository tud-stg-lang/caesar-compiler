/*
 * Created on 17.12.2003
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.TypeX;

/**
 * @author Karl Klose
 * Encapsulates the AspectJ-NameMangler
 */
public class CaesarNameMangler {
	private CaesarNameMangler()
	{
		throw new RuntimeException("static");
	}
	/*
	 * Create the name for the given advice
	 * @param	name	The name of the Type
	 * @param	kind	The advice kind
	 */
	public static String adviceName(
		String name, CaesarAdviceKind kind, int position) 
	{
		return NameMangler.adviceName(TypeX.forName(name), kind.wrappee(), position);
	}
}
