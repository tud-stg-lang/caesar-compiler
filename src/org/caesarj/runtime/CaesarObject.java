package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectDeployerIfc;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarObject implements CaesarObjectIfc {
	
    protected final Object $outer;
    
    public CaesarObject(Object outer) {
        $outer = outer;
	}
    
    public Object outer() {
        return $outer;
    }
    
    public boolean familyEquals(CaesarObject other) {
        return this.$outer == other.$outer;
    }
    
	public void $deploySelf(AspectDeployerIfc depl) {
		// do nothing
	}
	
	public void $undeploySelf(AspectDeployerIfc depl) {
		// do nothing
    }
	
	// ... more to come ...
}
