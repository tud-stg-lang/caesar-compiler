package org.caesarj.runtime.aspects;

/**
 * Interface implemented by all aspect classes.
 * 
 * @author Ivica Aracic
 */
public interface AspectIfc {
	
	public void $deploySelf(AspectDeployerIfc depl);
	
	public void $undeploySelf(AspectDeployerIfc depl);
}
