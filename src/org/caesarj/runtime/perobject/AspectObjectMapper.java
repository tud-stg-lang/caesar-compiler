/*
 * Created on 29.09.2004
 */
package org.caesarj.runtime.perobject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.caesarj.runtime.aspects.AspectContainerIfc;

/**
 * @author Vaidas Gasiunas
 *
 * Maintaints aspects deployed on objects
 */
public class AspectObjectMapper implements AspectContainerIfc {
	
	protected int _id;
	
	protected AspectKeyIfc _keyDetect;
	
	public AspectObjectMapper(int id, AspectKeyIfc keyDetect)
	{
		_id = id;
		_keyDetect = keyDetect;
	}
	
	private WeakHashMap _objectAspects = new WeakHashMap();
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return 			Iterator of aspect objects
	 */ 
	public Iterator $getInstances() {		
		Object key = _keyDetect.getCurrentKey();
		List lst = (List)_objectAspects.get(key);
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
		return _id;
	}
	
	/**
	 * Check if there are no deployed objects
	 * 
	 * @return			If collection is empty
	 */
	public boolean isEmpty() {
		return _objectAspects.isEmpty();
	}
	
	/**
	 * Deploy the object on the key object. 
	 * Assumes correct usage for the sake of efficiency.
	 * 
	 * @param obj		Aspect object
	 * @param key		Key object
	 */
	public void deployObject(Object obj, Object key) {
		
		/* include the object to the list of deployed objects of the thread */
		List lst = (List)_objectAspects.get(key);
		
		if (lst == null)
		{
			lst = new LinkedList();
			_objectAspects.put(key, lst);
		}
		
		lst.add(obj);
	}
	
	/**
	 * Undeploy the object from the key object 
	 * 
	 * @param obj		Aspect object
	 * @param key		Key object
	 */
	public void undeployObject(Object obj, Object key) {
		
		/* remove the object from the list of deployed objects of the thread */
		List lst = (List)_objectAspects.get(key);
		
		if (lst != null) {
			lst.remove(obj);
			
			if (lst.isEmpty())	{
				_objectAspects.remove(key);
			}
		}
	}
}
