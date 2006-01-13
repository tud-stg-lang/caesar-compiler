package org.caesarj.runtime.aspects;

abstract public class AbstractAspectRegistry implements AspectRegistryIfc {
	public AspectContainerIfc $aspectContainer;
	public Object $cj$joinpoint$this;
	
	public AspectContainerIfc $getAspectContainer()
    {
        return $aspectContainer;
    }

    public void $setAspectContainer(AspectContainerIfc cont)
    {
        $aspectContainer = cont;
    }
}
