/*
 * Created on 16.09.2004
 */
package org.caesarj.runtime.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Vaidas Gasiunas
 *
 * Handles requests of aspect deployment on remote registries
 */
public interface AspectRegistryServerIfc extends Remote {
	
	/**
	 * Deploys remote aspect reference on given registry 
	 * 
	 * @param aspObj			Remote reference to aspect object
	 * @param regClassName		Fully qualified registry class name
	 * @throws RemoteException
	 */
	public void deployRemote(Object aspObj, String regClassName) throws RemoteException;
	
	/**
	 * Undeploys remote aspect reference from given registry 
	 * 
	 * @param aspObj			Remote reference to aspect object
	 * @param regClassName		Fully qualified registry class name
	 * @throws RemoteException
	 */
	public void undeployRemote(Object aspObj, String regClassName) throws RemoteException;
}
