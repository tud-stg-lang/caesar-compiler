package org.caesarj.runtime;

/**
 * Interface implemented by all aspect classes.
 * 
 * @author Ivica Aracic
 */
public interface AspectIfc extends Deployable {
    public AspectRegistry $getSingletonAspect();
}
