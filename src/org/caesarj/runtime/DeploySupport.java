package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectLocalDeployer;
import org.caesarj.runtime.aspects.AspectIfc;
import org.caesarj.runtime.aspects.AspectOnThreadDeployer;

/**
 * Support class for deploy statements
 * 
 * @author Ivica Aracic
 */
public class DeploySupport {
    
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
            a.$deploySelf(new AspectOnThreadDeployer());
        }
    }

    /**
     * Undeploy aspect from the current thread (used for deploy block)
     * 
     * @param a		Aspect object
     */
    public static void undeployBlock(AspectIfc a) {
        if(a != null) {
            a.$undeploySelf(new AspectOnThreadDeployer());
        }
    }
    
    /**
     * Deploy aspect on the local process
     * 
     * @param a		Aspect object
     */
    public static void deployLocal(AspectIfc a) {
    	if(a != null) {
            a.$deploySelf(new AspectLocalDeployer());
        }
    }
    
    /**
     * Undeploy aspect from the local process
     * 
     * @param a		Aspect object
     */
    public static void undeployLocal(AspectIfc a) {
    	if(a != null) {
            a.$undeploySelf(new AspectLocalDeployer());
        }
    }
}
