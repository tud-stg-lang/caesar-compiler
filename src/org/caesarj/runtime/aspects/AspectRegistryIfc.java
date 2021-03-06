/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: AspectRegistryIfc.java,v 1.3 2005-03-31 11:58:18 gasiunas Exp $
 */

package org.caesarj.runtime.aspects;

import java.util.HashSet;

/**
 * @author Vaidas Gasiunas
 *
 * Interface of aspect registry singletons
 */
public interface AspectRegistryIfc {
	
	public AspectContainerIfc $getAspectContainer();
	
	public void $setAspectContainer(AspectContainerIfc cont);
	
	public void $setSingleAspect(Object aspObj);
	
	public static ThreadLocal threadLocalRegistries = new ThreadLocal() {

		protected synchronized Object initialValue() {
			return new HashSet();
		}

	};
}
