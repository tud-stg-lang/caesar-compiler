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
 * $Id: CompositeAspectContainer.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.runtime.aspects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author User
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompositeAspectContainer implements AspectContainerIfc {
	
	List containers = new LinkedList();
	
	/**
	 * @author Vaidas Gasiunas
	 *
	 * Composite iterator, for retrieving all deployed instances
	 */
	public class CompositeIter implements Iterator
	{
		private Iterator outerIter = null;
		private Iterator innerIter = null;
		
		public CompositeIter() {
			
			outerIter = containers.iterator();
			
			/* find first non-empty inner iterator */
			while (outerIter.hasNext())	{
				innerIter = ((AspectContainerIfc)outerIter.next()).$getInstances();
				if (innerIter != null && innerIter.hasNext()) {
					break;
				}
			}
		}		
		
		/**
	     * Returns <tt>true</tt> if the iteration has more elements. 
	     *
	     * @return <tt>true</tt> if the iterator has more elements.
	     */
	    public boolean hasNext() {
	    	
	    	if (innerIter.hasNext()) {
	    		return true;
	    	}	    			    	
	    	else {
	    		/* shift outer iterator */
	    		while (outerIter.hasNext())	{
					innerIter = ((AspectContainerIfc)outerIter.next()).$getInstances();
					if (innerIter != null && innerIter.hasNext()) {
						return true;
					}
				}
	    		return false;
	    	}	    		
	    }

	    /**
	     * Returns the next element in the iteration.
	     *
	     * @return the next element in the iteration.
	     * @exception NoSuchElementException iteration has no more elements.
	     */
	    public Object next() {
	    	
	    	if (innerIter.hasNext()) {
	    		return innerIter.next();
	    	}
	    	
	    	/* shift outer iterator */
	    	while (outerIter.hasNext())	{
				innerIter = ((AspectContainerIfc)outerIter.next()).$getInstances();
				if (innerIter != null && innerIter.hasNext()) {
					return innerIter.next();
				}
			}
	    	
    		throw new NoSuchElementException();	    	
	    }

	    /**
	     *	Removing is not supported
	     *
	     *  @exception UnsupportedOperationException 
	     */
	    public void remove() {
	    	throw new UnsupportedOperationException();
	    }	    
	}
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */
	public Iterator $getInstances() {
		return new CompositeIter();
	}
	
	/**
	 * Get container type
	 * 
	 * @return  Constant denoting container type
	 */
	public int $getContainerType() {
		return COMPOSITE_CONTAINER;
	}
	
	/**
	 * Find container by type
	 * 
	 * @param type		Container type constant (from AspectContainerIfc)
	 * @return			Found container object or null
	 */
	public AspectContainerIfc findContainer(int type) {
		
		Iterator it = containers.iterator();
		
		while (it.hasNext()) {			
			AspectContainerIfc cont =  (AspectContainerIfc)it.next();
			if (cont.$getContainerType() == type) {
				return cont;
			}
		}
			
		return null;
	}
	
	/**
	 * Get implementation list
	 * 
	 * @return
	 */
	public List getList() {
		return containers;
	}
	
	/**
	 * Is container empty
	 * 
	 * @return  Is container empty
	 */
	public boolean isEmpty() {
		return containers.isEmpty();
	}
}
