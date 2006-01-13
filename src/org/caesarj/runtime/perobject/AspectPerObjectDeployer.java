/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: AspectPerObjectDeployer.java,v 1.5 2006-01-13 12:06:06 gasiunas Exp $
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
public class AspectPerObjectDeployer extends BasicAspectDeployer {
	
	protected AspectKeyIfc _keyDetect = null;
	
	protected Object _deployKey = null;
	
	/**
	 * Initialize the AspectDeployer with specific key detection algorithm
	 * 
	 * @param keyDetect		Key detection algorithm
	 */
	public AspectPerObjectDeployer(AspectKeyIfc keyDetect)
	{
		_keyDetect = keyDetect;
	}	
	
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
	public synchronized void deployOnObject(AspectIfc aspObj, Object key)
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
	public synchronized void undeployFromObject(AspectIfc aspObj, Object key)
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
