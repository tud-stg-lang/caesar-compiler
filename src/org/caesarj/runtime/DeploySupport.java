package org.caesarj.runtime;

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
    
    public static void deployBlock(AspectIfc a) {
        if(a != null) {
            a.$getSingletonAspect().$deploy(a, Thread.currentThread());
        }
    }

    public static void undeployBlock(AspectIfc a) {
        if(a != null) {
            a.$getSingletonAspect().$undeploy();
        }
    }
}
