/*
 * Created on 16.09.2004
 */
package org.caesarj.runtime.rmi;

import java.lang.reflect.Method;

import org.caesarj.runtime.aspects.AspectLocalDeployer;
import org.caesarj.runtime.aspects.AspectRegistryIfc;

/**
 * @author Vaidas Gasiunas
 *
 * Handles requests of aspect deployment on remote registries
 */
public class AspectRegistryServer implements AspectRegistryServerIfc {
	
	private AspectLocalDeployer _depl = new AspectLocalDeployer();
	
	/**
	 * Deploys remote aspect reference on given registry 
	 */
	public void deployRemote(Object aspObj, String regClassName) {
		try {
			System.out.println("Deploying object on registry " + regClassName);
			
			Class cls = Class.forName(regClassName);
			Method mth = cls.getMethod("aspectOf", new Class[0]);
			AspectRegistryIfc reg = (AspectRegistryIfc)mth.invoke(null, null);
			
			_depl.$deployOn(reg, aspObj);
		}
		catch (Exception e) {
			throw new CaesarRemoteException("Could not resolve aspect registry " 
							+ regClassName + " : " + e.getMessage());
		}			
	}
	
	/**
	 * Undeploys remote aspect reference from given registry 
	 */
	public void undeployRemote(Object aspObj, String regClassName) {
		try {
			System.out.println("Undeploying object from registry " + regClassName);
			
			Class cls = Class.forName(regClassName);
			Method mth = cls.getMethod("aspectOf", new Class[0]);
			AspectRegistryIfc reg = (AspectRegistryIfc)mth.invoke(null, null);
			
			_depl.$undeployFrom(reg, aspObj);
		}
		catch (Exception e) {
			throw new CaesarRemoteException("Could not resolve aspect registry " 
					+ regClassName + " : " + e.getMessage());
		}
	}
}
