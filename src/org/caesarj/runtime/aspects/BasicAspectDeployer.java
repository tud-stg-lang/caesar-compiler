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
 * $Id: BasicAspectDeployer.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.runtime.aspects;

import java.util.HashMap;


/**
 * @author Vaidas Gasiunas
 *
 * Abstract parent for basic aspect container implementation 
 */
abstract public class BasicAspectDeployer implements AspectDeployerIfc {
	
	/**
	 * Generate unique type container type identifiers for lookup in composite container
	 */
	static HashMap _deployerIds = new HashMap();
	static int _nextId = AspectContainerIfc.FIRST_BASIC_CONTAINER;
	
	static public int getDeployerId(AspectDeployerIfc depl) {
		Object regId = _deployerIds.get(depl.getClass().getName());
		if (regId != null)	{
			return ((Integer)regId).intValue();
		}
		else {
			int id = _nextId++;
			_deployerIds.put(depl.getClass().getName(), new Integer(id));
			return id;
		}
	}
	
	protected int _contId = 0;
	
	/**
	 * Construct deployer
	 */
	public BasicAspectDeployer() {
		_contId = getDeployerId(this);
	}
	
	/**
	 * Create specific container object
	 * 
	 * @return 	New container object
	 */
	abstract public AspectContainerIfc createContainer();
	
	/**
	 * Deploy object on the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be deployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	abstract public void deployOnContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg);
	
	/**
	 * Undeploy object from the container
	 * 
	 * @param cont			Aspect container
	 * @param aspectObj		Object to be undeployed
	 * @param reg			Aspect registry (for read-only usage)
	 */
	abstract public void undeployFromContainer(AspectContainerIfc cont, Object aspectObj, AspectRegistryIfc reg);
	
	/**
	 * Get the specific container identifier
	 * 
	 * @return	Container identifier
	 */
	public int getContId() {
		return _contId;
	}	
	
	/**
	 * Deploy object on given registry
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $deployOn(AspectRegistryIfc reg, Object aspectObj) {

		AspectContainerIfc curCont = reg.$getAspectContainer();
		AspectContainerIfc myCont = null;
		
		/* setup appropriate aspect container in the registry */
		if (curCont == null) {
			myCont = createContainer();
			reg.$setAspectContainer(myCont);
		}
		else if (curCont.$getContainerType() == getContId()) {
			myCont = curCont;
		}
		else if (curCont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
			CompositeAspectContainer composite = (CompositeAspectContainer)curCont;
			myCont = composite.findContainer(getContId());
			if (myCont == null) {
				myCont = createContainer();
				composite.getList().add(myCont);				
			}
		}
		else {
			CompositeAspectContainer composite = new CompositeAspectContainer();
			myCont = createContainer();
			composite.getList().add(curCont);
			composite.getList().add(myCont);
			reg.$setAspectContainer(composite);			
		}
		
		deployOnContainer(myCont, aspectObj, reg);
	}
	
	/**
	 * Undeploy object from the given registry
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $undeployFrom(AspectRegistryIfc reg, Object aspectObj) {

		AspectContainerIfc curCont = reg.$getAspectContainer();
		AspectContainerIfc myCont = null;
		
		if (curCont == null) {
			return; // ignore
		}
		else if (curCont.$getContainerType() == getContId()) {
			myCont = curCont;
			undeployFromContainer(myCont, aspectObj, reg);
			if (myCont.isEmpty()) {
				reg.$setAspectContainer(null);
			}
		}
		else if (curCont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
			CompositeAspectContainer composite = (CompositeAspectContainer)curCont;
			myCont = composite.findContainer(getContId());
			
			if (myCont != null) {
				undeployFromContainer(myCont, aspectObj, reg);
				
				if (myCont.isEmpty()) {
					composite.getList().remove(myCont);
					
					if (composite.getList().size() < 2)	{
						reg.$setAspectContainer((AspectContainerIfc)composite.getList().get(0));
					}
				}
			}
		}
	}
}
