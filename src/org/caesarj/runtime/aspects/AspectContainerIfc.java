/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.Iterator;

/**
 * @author Vaidas Gasiunas
 *
 * Interface for containers of deployed aspect objects
 * 
 */
public interface AspectContainerIfc {
	
	/**
	 * Aspect container type identifiers
	 */
	public final static int COMPOSITE_CONTAINER = 0;
	
	public final static int THREAD_MAPPER = 1;
	
	public final static int LOCAL_CONTAINER = 2;
		
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */ 
	Iterator $getInstances();
	
	/**
	 * Get container type
	 * 
	 * @return  Constant denoting container type
	 */
	int $getContainerType();
}
