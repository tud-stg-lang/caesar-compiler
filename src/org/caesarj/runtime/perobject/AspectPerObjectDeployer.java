/*
 * Created on 29.09.2004
 */
package org.caesarj.runtime.perobject;

import org.caesarj.runtime.aspects.AspectContainerIfc;
import org.caesarj.runtime.aspects.AspectIfc;
import org.caesarj.runtime.aspects.AspectRegistryIfc;
import org.caesarj.runtime.aspects.BasicAspectDeployer;

/**
 * @author Vaidas Gasiunas
 *
 * Abstract implementation of object based deployment
 */
abstract public class AspectPerObjectDeployer extends BasicAspectDeployer {
	
	protected AspectKeyIfc _keyDetect = null;
	
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
	 * Initialize the AspectDeployer with specific key detection algorithm
	 * 
	 * @param keyDetect		Key detection algorithm
	 */
	public void init(AspectKeyIfc keyDetect)
	{
		_keyDetect = keyDetect;
	}
	
	/**
	 * Deploy aspect on object
	 * 
	 * @param aspObj	aspect object
	 * @param key		deployment key object
	 */
	public synchronized void deployOnObject(AspectIfc aspObj, Object key)
	{
		setDeployKey(key);
		aspObj.$deploySelf(this);
	}
	
	/**
	 * Undeploy aspect from object
	 * 
	 * @param aspObj	aspect object
	 * @param key		deployment key object
	 */
	public synchronized void undeployFromObject(AspectIfc aspObj, Object key)
	{
		setDeployKey(key);
		aspObj.$undeploySelf(this);
	}
	
	/**
	 * Create specific container object
	 * 
	 * @return 	New container object
	 */
	public AspectContainerIfc createContainer() {
		return new AspectObjectMapper(getContId(), _keyDetect);
	}
	
	/**
	 * Deploy object on the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be deployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	public void deployOnContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg) {
		AspectObjectMapper objectMapper = (AspectObjectMapper)cont;
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
		AspectObjectMapper objectMapper = (AspectObjectMapper)cont;
		objectMapper.undeployObject(aspectObj, _deployKey);
	}
}
