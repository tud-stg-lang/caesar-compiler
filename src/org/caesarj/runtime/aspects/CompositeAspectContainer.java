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
 * $Id: CompositeAspectContainer.java,v 1.7 2005-06-03 08:24:47 klose Exp $
 */

package org.caesarj.runtime.aspects;


/**
 * @author User
 *
 * TODO [documentation]
 */
public class CompositeAspectContainer implements AspectContainerIfc {
	
	DynArray containers = new DynArray();
	
	/**
	 * Get DynArray of deployed aspect objects for which the advice has to be called
	 * 
	 * @return iterator of aspect objects
	 */
	public Object[] $getInstances() {
		int numCont = containers.size();
		Object instances[][] = new Object[numCont][];
		int totSize = 0;
		for (int i1 = 0; i1 < numCont; i1++) {
			instances[i1] = ((AspectContainerIfc)containers.get(i1)).$getInstances();
			if (instances[i1] != null) {
				totSize += instances[i1].length;
			}
		}
		if (totSize == 0) {
			return null;
		}
		Object[] res = new Object[totSize];
		int pos = 0;
		for (int i1 = 0; i1 < numCont; i1++) {
			if (instances[i1] != null) {
				System.arraycopy(instances[i1], 0, res, pos, instances[i1].length);
				pos += instances[i1].length;
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
		
		for (int i1 = 0; i1 < containers.size(); i1++) {
			AspectContainerIfc cont = (AspectContainerIfc)containers.get(i1);
			if (cont.$getContainerType() == type) {
				return cont;
			}			
		}
			
		return null;
	}
	
	/**
	 * Get implementation DynArray
	 * 
	 * @return
	 */
	public DynArray getList() {
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
	
	/**
	 * If there is fixed single instance, return it. 
	 * Return null otherwise
	 * 
	 * @return  Single deployed instance
	 */
	public Object getSingleInstance() {
		return null;
	}
}
