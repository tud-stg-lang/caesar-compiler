/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.HashSet;

/**
 * @author Vaidas Gasiunas
 *
 * Interface of aspect registry singletons
 */
public interface AspectRegistryIfc {
	
	public AspectContainerIfc $getAspectContainer();
	
	public void $setAspectContainer(AspectContainerIfc cont);
	
	public static ThreadLocal threadLocalRegistries = new ThreadLocal() {

		protected synchronized Object initialValue() {
			return new HashSet();
		}

	};
}
