/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.Set;

/**
 * @author Vaidas Gasiunas
 *
 * Strategy class implementing thread based aspect deployment
 * 
 */
public class AspectOnThreadDeployer implements AspectDeployerIfc {
	
	/**
	 * Deploy the object on given registry 
	 * Deploys the object on the current thread
	 *  
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $deployOn(AspectRegistryIfc reg, Object aspectObj) {
		
		AspectContainerIfc cont = reg.$getAspectContainer();
		AspectThreadMapper threadMapper = null;
		
		/* setup appropriate aspect container in the registry */
		if (cont == null) {		
			threadMapper = new AspectThreadMapper();
			reg.$setAspectContainer(threadMapper);
		}
		else if (cont.$getContainerType() == AspectContainerIfc.THREAD_MAPPER) {
			threadMapper = (AspectThreadMapper)cont;
		}
		else if (cont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
			CompositeAspectContainer composite = (CompositeAspectContainer)cont;
			threadMapper = (AspectThreadMapper)composite.findContainer(AspectContainerIfc.THREAD_MAPPER);
			if (threadMapper == null) {
				threadMapper = new AspectThreadMapper();
				composite.getList().add(threadMapper);				
			}
		}
		else {
			CompositeAspectContainer composite = new CompositeAspectContainer();
			threadMapper = new AspectThreadMapper();
			composite.getList().add(cont);
			composite.getList().add(threadMapper);
			reg.$setAspectContainer(composite);			
		}
		
		/* deploy the object */
		threadMapper.deployObject(aspectObj, Thread.currentThread());
		
		/* include the registry to the set of thread registries */
		/* (important for inheriting deployed objects by child threads)*/
		Set set = (Set)AspectRegistryIfc.threadLocalRegistries.get();
        set.add(reg);
	}

	/**
	 * Undeploy the object from the given registry
	 * Assumes that the object is deployed on the current thread
	 * 
	 * @param reg			Registry instance
	 * @param aspectObj		Aspect object
	 */
	public void $undeployFrom(AspectRegistryIfc reg, Object aspectObj) {
		
		AspectContainerIfc cont = reg.$getAspectContainer();
		AspectThreadMapper threadMapper = null;
		boolean undeployed = false;
		
		if (cont == null) {
			return; // ignore
		}
		else if (cont.$getContainerType() == AspectContainerIfc.THREAD_MAPPER) {
			threadMapper = (AspectThreadMapper)cont;
			threadMapper.undeployObject(aspectObj, Thread.currentThread());
			undeployed = true;
			
			if (threadMapper.isEmpty()) {
				reg.$setAspectContainer(null);
			}
		}
		else if (cont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
			CompositeAspectContainer composite = (CompositeAspectContainer)cont;
			threadMapper = (AspectThreadMapper)composite.findContainer(AspectContainerIfc.THREAD_MAPPER);
			
			if (threadMapper != null) {
				threadMapper.undeployObject(aspectObj, Thread.currentThread());
				undeployed = true;
				
				if (threadMapper.isEmpty())	{
					composite.getList().remove(threadMapper);
					
					if (composite.getList().size() < 2)	{
						reg.$setAspectContainer((AspectContainerIfc)composite.getList().get(0));
					}
				}
			}
		}
		
		if (undeployed && threadMapper.$getInstances() == null) {
			/* remove the registry to the set of thread registries */
			/* (important for inheriting deployed objects by child threads)*/
			Set set = (Set)AspectRegistryIfc.threadLocalRegistries.get();
	        set.remove(reg);
		}
	}
}
