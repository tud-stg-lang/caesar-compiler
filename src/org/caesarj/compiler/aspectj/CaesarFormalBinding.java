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
	public	FormalBinding	wrappee()
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
}
