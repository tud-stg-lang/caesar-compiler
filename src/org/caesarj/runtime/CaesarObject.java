package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectDeployerIfc;
import org.caesarj.runtime.aspects.AspectIfc;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarObject implements AspectIfc {
	public CaesarObject(Object outer) {
	}
	
	public void $deploySelf(AspectDeployerIfc depl) {
		// do nothing
	}
	
	public void $undeploySelf(AspectDeployerIfc depl) {
		// do nothing
    }
	
	// ... more to come ...
}
