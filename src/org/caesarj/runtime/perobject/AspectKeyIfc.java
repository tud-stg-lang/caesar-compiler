/*
 * Created on 29.09.2004
 */
package org.caesarj.runtime.perobject;

/**
 * @author Vaidas Gasiunas
 * 
 * Determines the key object.
 * Used for activation of aspects, deployed on object.
 * 
 */
public interface AspectKeyIfc {
	
	public Object getCurrentKey();
}
