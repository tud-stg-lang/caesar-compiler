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
 * $Id: CompositeAspectContainer.java,v 1.4 2005-03-22 08:42:20 aracic Exp $
 */

package org.caesarj.runtime.aspects;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author User
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompositeAspectContainer implements AspectContainerIfc {
	
	List containers = new LinkedList();
	
	/**
	 * Get list of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */
	public List $getInstances() {
		LinkedList res = new LinkedList();
		for (Iterator it = containers.iterator(); it.hasNext();) {
			Collection l = ((AspectContainerIfc)it.next()).$getInstances();
			for (Iterator it2 = l.iterator(); it2.hasNext();) {
				res.add(it2.next());
			}
		}
		return res;
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
