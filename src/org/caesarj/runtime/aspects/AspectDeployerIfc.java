/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

/**
 * @author Vaidas Gasiunas
 *
 * Interface for aspect deployment strategies
 * 
 */
public interface AspectDeployerIfc {
	
	/**
	 * Deploy object on given registry
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $deployOn(AspectRegistryIfc reg, Object aspectObj);
	
	/**
	 * Undeploy object from the given registry
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $undeployFrom(AspectRegistryIfc reg, Object aspectObj);
}
