/*
 * Created on 16.09.2004
 */
package org.caesarj.runtime.rmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.ServerRef;

import org.caesarj.runtime.aspects.AspectIfc;

/**
 * @author Vaidas Gasiunas
 *
 * Caesar host abstraction. Provides operations to publish 
 * and resolve remote objects, to deploy aspects
 */
public class CaesarHost {
	
	private String _url;
	
	private AspectRemoteDeployer _remDepl = null;
	
	/**
	 * Create Caesar host object
	 * 
	 * @param url	Host url
	 */
	public CaesarHost(String url) {
		_url = url;
		if (!_url.endsWith("/")) {
			_url += "/";
		}
		
		if (!_url.startsWith("rmi://")) {
			_url = "rmi://" + _url;
		}
	}
	
	/**
	 * Predefined local host object
	 */
	public static CaesarHost LOCAL_HOST = new CaesarHost("rmi://localhost/"); 
	
	/**
	 * Export object for remoting, if not exported yet
	 * 
	 * @param obj	Local object reference
	 */
	public static void export(Object obj) {
		try {
			exportObject((Remote)obj);
		}
		catch (RemoteException e) {
			// Ignore exception
		}
	}
	
	/**
	 * Publish object for remote access by name
	 * 
	 * @param name	Remote object name
	 * @param obj	Local object reference
	 */
	public void publish(String name, Object obj) {
		try {
			exportObject((Remote)obj);
			Naming.rebind(_url + name, (Remote)obj);
		}
		catch (Exception e) {
			throw new CaesarRemoteException("Failed to publish remote object :" + e.getMessage());
		}
	}
	
	/**
	 * Resolve remote object by name
	 * 
	 * @param name	Remote object name
	 * @return		Reference to resolved remote object
	 */
	public Object resolve(String name) {
		try {
			return Naming.lookup(_url + name);
		}
		catch (Exception e) {
			throw new CaesarRemoteException("Failed to resolve remote object :" + e.getMessage());
		}
	}
	
	/**
	 * Activate aspect deployment on the host (call from server site)
	 */
	public void activateAspectDeployment() {
		AspectRegistryServer srv = new AspectRegistryServer();
		publish("CjAspectRegistryServer", srv);
	}
	
	/**
	 * Deploy aspect object on the host
	 * 
	 * @param aspObj	Aspect object
	 */
	public void deployAspect(Object aspObj) {
		/* deploy only remote non-null aspect */
		if (aspObj != null && aspObj instanceof AspectIfc && aspObj instanceof Remote) {
			AspectIfc aspIfc = (AspectIfc)aspObj;
			export(aspIfc);
			aspIfc.$deploySelf(getRemoteDepl());
		}
	}
	
	/**
	 * Undeploy aspect object form the host
	 * 
	 * @param aspObj	Aspect object
	 */
	public void undeployAspect(Object aspObj) {
		/* undeploy only remote non-null aspect */
		if (aspObj != null && aspObj instanceof AspectIfc && aspObj instanceof Remote) {
			AspectIfc aspIfc = (AspectIfc)aspObj;
			aspIfc.$undeploySelf(getRemoteDepl());
		}
	}
	
	/**
	 * Create remote deployment strategy for the host
	 * 
	 * @return	Remote deployment strategy object
	 */
	private AspectRemoteDeployer getRemoteDepl() {
		if (_remDepl == null) {
			AspectRegistryServerIfc srv = (AspectRegistryServerIfc)resolve("CjAspectRegistryServer");
			_remDepl = new AspectRemoteDeployer(srv);
		}
		return _remDepl;
	}
	
	/**
	 * Export object using custom remote reference
	 * 
	 * @param obj	- object to be exported
	 * @return		- object stub
	 * @throws RemoteException
	 */
	private static Remote exportObject(Remote obj) throws RemoteException
	{
		ServerRef serverRef;
		try {
			serverRef = new CjUnicastServerRef(0);		
		}
		catch (Exception e) {
		    throw new ExportException(
		    		"Exception creating instance of CjUnicastServerRef class", e);
		}
		
		return serverRef.exportObject(obj, null);
	}
}
