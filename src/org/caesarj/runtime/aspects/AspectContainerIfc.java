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
 * $Id: AspectContainerIfc.java,v 1.6 2005-03-31 11:58:18 gasiunas Exp $
 */

package org.caesarj.runtime.aspects;


/**
 * @author Vaidas Gasiunas
 *
 * Interface for containers of deployed aspect objects
 * 
 */
public interface AspectContainerIfc {
	
	/**
	 * Aspect container type identifiers
	 */
	public final static int COMPOSITE_CONTAINER = 0;
	
	public final static int THREAD_MAPPER = 1;
	
	public final static int FIRST_BASIC_CONTAINER = 100;	
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */ 
	Object[] $getInstances();
	
	/**
	 * Get container type
	 * 
	 * @return  Constant denoting container type
	 */
	int $getContainerType();
	
	/**
	 * Is container empty
	 * 
	 * @return  Is container empty
	 */
	boolean isEmpty();	
	
	/**
	 * If there is fixed single instance, return it. 
	 * Return null otherwise
	 * 
	 * @return  Single deployed instance
	 */
	Object getSingleInstance();	
}
