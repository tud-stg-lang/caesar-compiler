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
 * $Id: AspectRegistryServer.java,v 1.3 2005-09-14 08:29:52 gasiunas Exp $
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
			AspectRegistryIfc reg = (AspectRegistryIfc)mth.invoke(null, (Object[])null);
			
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
			AspectRegistryIfc reg = (AspectRegistryIfc)mth.invoke(null, (Object[])null);
			
			_depl.$undeployFrom(reg, aspObj);
		}
		catch (Exception e) {
			throw new CaesarRemoteException("Could not resolve aspect registry " 
					+ regClassName + " : " + e.getMessage());
		}
	}
}
