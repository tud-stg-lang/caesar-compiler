/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * @author Vaidas Gasiunas
 *
 * Aspect container, which contains aspects deployed on individual threads
 */
public class AspectThreadMapper implements AspectContainerIfc {
	
	private WeakHashMap $threadObjects = new WeakHashMap();
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return 			Iterator of aspect objects
	 */ 
	public Iterator $getInstances() {
		
		Thread thread = Thread.currentThread();
		List lst = (List)$threadObjects.get(thread);
		if (lst == null)
			return null;
		else
			return lst.iterator();
	}
	
	/**
	 * Get container type
	 * 
	 * @return  Constant denoting container type
	 */
	public int $getContainerType() {
		return THREAD_MAPPER;
	}
	
	/**
	 * Check if there are no deployed objects
	 * 
	 * @return			If collection is empty
	 */
	public boolean isEmpty() {
		return $threadObjects.isEmpty();
	}
	
	/**
	 * Deploy the object on the thread. 
	 * Assumes correct usage for the sake of efficiency.
	 * 
	 * @param obj		Aspect object
	 * @param thread	Deployment thread
	 */
	public void deployObject(Object obj, Thread thread) {
		
		/* include the object to the list of deployed objects of the thread */
		List lst = (List)$threadObjects.get(thread);
		
		if (lst == null)
		{
			lst = new LinkedList();
			$threadObjects.put(thread, lst);
		}
		
		lst.add(obj);
	}
	
	/**
	 * Undeploy the object from the thread 
	 * 
	 * @param obj		Aspect object
	 * @param thread	Deployment thread
	 */
	public void undeployObject(Object obj, Thread thread) {
		
		/* remove the object from the list of deployed objects of the thread */
		List lst = (List)$threadObjects.get(thread);
		
		if (lst != null) {
			lst.remove(obj);
			
			if (lst.isEmpty())	{
				$threadObjects.remove(thread);
			}
		}
	}
	
	/**
	 * Copy deployed objects from one thread to another
	 * 
	 * @param srcThread		Source thread
	 * @param destThread	Destination thread
	 */
	public void copyObjects(Thread srcThread, Thread destThread) {
		
		List lst = (List)$threadObjects.get(srcThread);
		
		if (lst != null) {
			List lstNew = new LinkedList(lst);
			$threadObjects.put(destThread, lstNew);
		}
	}
}
