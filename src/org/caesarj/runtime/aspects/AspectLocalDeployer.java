/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

/**
 * @author Vaidas Gasiunas
 *
 * Aspect deployement strategy, which deploys aspect on entire local process 
 */
public class AspectLocalDeployer extends BasicAspectDeployer {
	
	/**
	 * Create specific container object
	 * 
	 * @return 	New container object
	 */
	public AspectContainerIfc createContainer() {
		return new AspectList(getContId());
	}
	
	/**
	 * Deploy object on the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be deployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void deployOnContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		((AspectList)cont).getList().add(aspectObj);	
	}
	
	/**
	 * Undeploy object from the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be undeployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void undeployFromContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		((AspectList)cont).getList().remove(aspectObj);
	}
}
