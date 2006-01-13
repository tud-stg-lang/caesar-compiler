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
 * $Id: DeploySupport.java,v 1.7 2006-01-13 12:06:07 gasiunas Exp $
 */

package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectLocalDeployer;
import org.caesarj.runtime.aspects.AspectIfc;
import org.caesarj.runtime.aspects.AspectOnThreadDeployer;
import org.caesarj.runtime.perobject.AspectPerThisDeployer;

/**
 * Support class for deploy statements
 * 
 * @author Ivica Aracic
 */
public class DeploySupport {
	
	private static AspectOnThreadDeployer threadDeployer = new AspectOnThreadDeployer();
	private static AspectLocalDeployer localDeployer = new AspectLocalDeployer();
	private static AspectPerThisDeployer perThisDeployer = new AspectPerThisDeployer();
    
    public static AspectIfc checkIfDeployable(Object obj) {
        if(obj instanceof AspectIfc)
            return (AspectIfc)obj;
        else
            return null;
    }
    
    /**
     * Deploy aspect on the current thread (used for deploy block)
     * 
     * @param a		Aspect object
     */
    public static void deployBlock(AspectIfc a) {
        if(a != null) {
        	threadDeployer.$deployOn(a.$getAspectRegistry(), a);
        }
    }

    /**
     * Undeploy aspect from the current thread (used for deploy block)
     * 
     * @param a		Aspect object
     */
    public static void undeployBlock(AspectIfc a) {
        if(a != null) {
        	threadDeployer.$undeployFrom(a.$getAspectRegistry(), a);
        }
    }
    
    /**
     * Deploy aspect on the local process
     * 
     * @param a		Aspect object
     */
    public static void deployLocal(AspectIfc a) {
    	if(a != null) {
    		localDeployer.$deployOn(a.$getAspectRegistry(), a);
        }
    }
    
    /**
     * Undeploy aspect from the local process
     * 
     * @param a		Aspect object
     */
    public static void undeployLocal(AspectIfc a) {
    	if(a != null) {
    		localDeployer.$undeployFrom(a.$getAspectRegistry(), a);            
        }
    }
    
    /**
     * Deploy aspect on the local process
     * 
     * @param a		Aspect object
     */
    public static void deployOnObject(PerThisDeployable a, Object o) {
    	if(a != null) {
    		perThisDeployer.deployOnObject(a, o);
        }
    }
    
    /**
     * Undeploy aspect from the local process
     * 
     * @param a		Aspect object
     */
    public static void undeployFromObject(PerThisDeployable a, Object o) {
    	if(a != null) {
    		perThisDeployer.undeployFromObject(a, o);            
        }
    }
}
