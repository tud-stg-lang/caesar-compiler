/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.Set;

/**
 * @author Vaidas Gasiunas
 *
 * Strategy class implementing thread based aspect deployment
 * 
 */
public class AspectOnThreadDeployer extends BasicAspectDeployer {
	
	/**
	 * Get the specific container identifier
	 * 
	 * @return	Container identifier
	 */
	public int getContId() {
		return AspectContainerIfc.THREAD_MAPPER;
	}
	
	/**
	 * Create specific container object
	 * 
	 * @return 	New container object
	 */
	public AspectContainerIfc createContainer() {
		return new AspectThreadMapper();
	}
	
	/**
	 * Deploy object on the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be deployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void deployOnContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		
		AspectThreadMapper threadMapper = (AspectThreadMapper)cont;
		
		/* deploy the object */
		threadMapper.deployObject(aspectObj, Thread.currentThread());
		
		/* include the registry to the set of thread registries */
		/* (important for inheriting deployed objects by child threads)*/
		Set set = (Set)AspectRegistryIfc.threadLocalRegistries.get();
        set.add(reg);	
	}
	
	/**
	 * Undeploy object from the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be undeployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void undeployFromContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		
		AspectThreadMapper threadMapper = (AspectThreadMapper)cont;
		
		threadMapper.undeployObject(aspectObj, Thread.currentThread());
		
		if (threadMapper.$getInstances() == null) {
			/* remove the registry to the set of thread registries */
			/* (important for inheriting deployed objects by child threads)*/
			Set set = (Set)AspectRegistryIfc.threadLocalRegistries.get();
	        set.remove(reg);
	    }
	}	
}
