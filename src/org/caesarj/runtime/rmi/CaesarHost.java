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
 * $Id: CaesarHost.java,v 1.3 2005-03-31 10:43:20 gasiunas Exp $
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
			getRemoteDepl().$deployOn(aspIfc.$getAspectRegistry(), aspIfc);
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
			getRemoteDepl().$undeployFrom(aspIfc.$getAspectRegistry(), aspIfc);
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
