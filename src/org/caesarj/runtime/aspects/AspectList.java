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
 * $Id: AspectList.java,v 1.4 2005-03-22 08:42:20 aracic Exp $
 */

package org.caesarj.runtime.aspects;

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
	public List $getInstances() {
		List res = new LinkedList(aspList);
		return res;
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
