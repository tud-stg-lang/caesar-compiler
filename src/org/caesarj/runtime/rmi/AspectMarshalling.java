/*
 * Created on 22.09.2004
 */
package org.caesarj.runtime.rmi;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.caesarj.runtime.aspects.AspectContainerIfc;
import org.caesarj.runtime.aspects.AspectIfc;
import org.caesarj.runtime.aspects.AspectOnThreadDeployer;
import org.caesarj.runtime.aspects.AspectRegistryIfc;
import org.caesarj.runtime.aspects.AspectThreadMapper;
import org.caesarj.runtime.aspects.CompositeAspectContainer;

/**
 * @author Vaidas Gasiunas
 *
 * Marshal aspects deployed on the current thread and deploy them on the remote execution thread
 */
public class AspectMarshalling implements Serializable
{
	private static AspectOnThreadDeployer _depl = new AspectOnThreadDeployer();
	
	/**
	 * Marshal aspects deployed on the current thread
	 * 
	 * @param out			Output stream for marshalling
	 * @throws IOException
	 */
	static public void marshalAspects(ObjectOutput out) throws IOException 
	{
		/* iterate through all registries, which have deployed objects on the thread */
		Set activeRegistries = (Set) AspectRegistryIfc.threadLocalRegistries.get();
		Iterator iterator = activeRegistries.iterator();
				
		while (iterator.hasNext()) 
		{
			AspectRegistryIfc currentRegistry = (AspectRegistryIfc) iterator.next();
			
			AspectContainerIfc cont = currentRegistry.$getAspectContainer();
			AspectThreadMapper threadMapper = findThreadMapper(currentRegistry);
			
			if (threadMapper != null) 
			{
				Iterator it = threadMapper.$getInstances();
				
				/* marshal only if there are deployed instances */
				if (it != null && it.hasNext()) 
				{
					/* marshal registry name */
					out.writeObject(currentRegistry.getClass().getName());
					
					/* marshal all instances deployed in the registry for the current thread */
					List lst = new LinkedList();				
					while (it.hasNext()) {
						Object aspObj = it.next();
						CaesarHost.export(aspObj);
						lst.add(aspObj);
					}
					out.writeObject(lst);
				}
			}
		}
		
		/* write null as the end marker */
		out.writeObject(null); 
	}
    
	/**
	 * Unmarshal aspects and deploy them on the current thread
	 * 
	 * @param out			Output stream for marshalling
	 * @throws IOException
	 */
	static public void unmarshalAspects(ObjectInput in) throws IOException, ClassNotFoundException {
		
		try {
			while (true) {
				Object obj = in.readObject();
				
				/* null is the end marker */
				if (obj == null)
					return;
				
				/* unmarshal registry name and retrieve its local singleton instance */ 
				String regName = (String)obj;
				Class cls = Class.forName(regName);
				Method mth = cls.getMethod("aspectOf", new Class[0]);
				AspectRegistryIfc reg = (AspectRegistryIfc)mth.invoke(null, null);
				
				/* clear the set of deployed aspects on the current thread */
				AspectThreadMapper threadMapper = findThreadMapper(reg);
				if (threadMapper != null)
				{
					threadMapper.undeployAllFromThread(Thread.currentThread());
				}
				
				/* unmarshal aspect object and deploy them in the registry for the current thread */
				List lst = (List)in.readObject();
				
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					AspectIfc aspObj = (AspectIfc)it.next();
					_depl.$deployOn(reg, aspObj);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Find thread based aspect deployment container of the given registry
	 * 
	 * @param reg		Aspect registry
	 * @return			Thread mapper container (null if not found)
	 */
	static private AspectThreadMapper findThreadMapper(AspectRegistryIfc reg)
	{
		AspectContainerIfc cont = reg.$getAspectContainer();
		AspectThreadMapper threadMapper = null;
		
		if (cont != null) {
			if (cont.$getContainerType() == AspectContainerIfc.THREAD_MAPPER) {
				threadMapper = (AspectThreadMapper)cont;
			}
			else if (cont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
				CompositeAspectContainer composite = (CompositeAspectContainer)cont;
				threadMapper = (AspectThreadMapper)composite.findContainer(AspectContainerIfc.THREAD_MAPPER);
			}
		}
		
		return threadMapper;
	}
}
