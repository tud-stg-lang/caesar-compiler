/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: AspectThreadMapper.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
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
	
	/**
	 * Undeploys all objects from the given thread
	 * 
	 * @param thread	Deployment thread
	 */
	public void undeployAllFromThread(Thread thread) {
		$threadObjects.remove(thread);
	}
}
