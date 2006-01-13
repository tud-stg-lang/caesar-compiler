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
 * $Id: AspectOnThreadDeployer.java,v 1.4 2006-01-13 12:06:06 gasiunas Exp $
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
	public AspectContainerIfc createContainer(AspectRegistryIfc reg) {
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
