/*
 * Created on 16.09.2004
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
