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
 * $Id: AspectRemoteDeployer.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.runtime.rmi;

import java.rmi.RemoteException;

import org.caesarj.runtime.aspects.AspectDeployerIfc;
import org.caesarj.runtime.aspects.AspectRegistryIfc;

/**
 * @author Vaidas Gasiunas
 *
 * Remote aspect deployment strategy implementation
 */
public class AspectRemoteDeployer implements AspectDeployerIfc {
	
	AspectRegistryServerIfc _regSrv = null;
	
	public AspectRemoteDeployer(AspectRegistryServerIfc regSrv) {
		_regSrv = regSrv;
	}
	
	/**
	 * Deploy the object on given registry 
	 *  
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $deployOn(AspectRegistryIfc reg, Object aspObj) {
		try {
			_regSrv.deployRemote(aspObj, reg.getClass().getName());
		}
		catch (RemoteException e) {
			throw new CaesarRemoteException("Failed to deploy aspect on remote registry: " + e.getMessage());
		}				
	}

	/**
	 * Undeploy the object from the given registry
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $undeployFrom(AspectRegistryIfc reg, Object aspObj) {
		try {
			_regSrv.undeployRemote(aspObj, reg.getClass().getName());
		}
		catch (RemoteException e) {
			throw new CaesarRemoteException("Failed to undeploy aspect from remote registry: " + e.getMessage());
		}
	}

}
