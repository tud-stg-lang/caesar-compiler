package org.caesarj.runtime.perobject;

import org.caesarj.runtime.PerThisDeployable;
import org.caesarj.runtime.aspects.AbstractAspectRegistry;
import org.caesarj.runtime.aspects.AspectContainerIfc;
import org.caesarj.runtime.aspects.AspectRegistryIfc;
import org.caesarj.runtime.aspects.BasicAspectDeployer;

public class AspectPerThisDeployer extends BasicAspectDeployer {

	protected Object _deployKey = null;
	
	/**
	 * Sets current key for deployment
	 * 
	 * @param key	Object, used as key for deployment
	 */
	protected void setDeployKey(Object key)
	{
		_deployKey = key;
	}
	
	/**
	 * Deploy aspect on object
	 * 
	 * @param aspObj	aspect object
	 * @param key		deployment key object
	 */
	public synchronized void deployOnObject(PerThisDeployable aspObj, Object key)
	{
		setDeployKey(key);
		$deployOn(aspObj.$getAspectRegistry(), aspObj);		
	}
	
	/**
	 * Undeploy aspect from object
	 * 
	 * @param aspObj	aspect object
	 * @param key		deployment key object
	 */
	public synchronized void undeployFromObject(PerThisDeployable aspObj, Object key)
	{
		setDeployKey(key);
		$undeployFrom(aspObj.$getAspectRegistry(), aspObj);		
	}
	
	/**
	 * Create specific container object
	 * 
	 * @return 	New container object
	 */
	public AspectContainerIfc createContainer(AspectRegistryIfc reg) {
		return new AspectThisObjectMapper(getContId(), (AbstractAspectRegistry)reg);
	}
	
	/**
	 * Deploy object on the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be deployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void deployOnContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		AspectThisObjectMapper objectMapper = (AspectThisObjectMapper)cont;
		objectMapper.deployObject(aspectObj, _deployKey);
	}
	
	/**
	 * Undeploy object from the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be undeployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void undeployFromContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		AspectThisObjectMapper objectMapper = (AspectThisObjectMapper)cont;
		objectMapper.undeployObject(aspectObj, _deployKey);
	}

}
