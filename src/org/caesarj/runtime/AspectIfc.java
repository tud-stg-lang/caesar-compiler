package org.caesarj.runtime;

/**
 * Interface implemented by all aspect classes.
 * 
 * @author Ivica Aracic
 */
public interface AspectIfc 
{
	public void $deploySelf(Thread thread);
	
	public void $undeploySelf();
}
