/*
 * Created on 17.12.2003
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.TypeX;
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
   public CaesarFormalBinding(String signature, String name, int index, int start, int end, String fileName) 
   {
 		binding = new FormalBinding(
			TypeX.forSignature(signature),
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
