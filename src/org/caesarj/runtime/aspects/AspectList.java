/*
 * Created on 13.09.2004
 */
package org.caesarj.runtime.aspects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vaidas Gasiunas
 *
 * Aspect container based on linked list
 */
public class AspectList implements AspectContainerIfc 
{
	List aspList = new LinkedList();
	int  _type;
	
	public AspectList(int type) {
		_type = type;
	}
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */
	public Iterator $getInstances() {
		return aspList.iterator();
	}
	
	/**
	 * Get container type
	 * 
	 * @return  Constant denoting container type
	 */
	public int $getContainerType() {
		return _type;
	}	
	
	/**
	 * Get implementation list
	 * 
	 * @return	implementation list
	 */
	public List getList() {
		return aspList;
	}
	
	/**
	 * Is container empty
	 * 
	 * @return  Is container empty
	 */
	public boolean isEmpty() {
		return aspList.isEmpty();
	}
}
