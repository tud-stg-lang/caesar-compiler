package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectIfc;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public interface CaesarObjectIfc extends AspectIfc {
    Object outer();
    boolean familyEquals(CaesarObject other);
}
