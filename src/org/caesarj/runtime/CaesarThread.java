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
 * $Id: CaesarThread.java,v 1.4 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.runtime;

import java.util.Iterator;
import java.util.Set;

import org.caesarj.runtime.aspects.AspectContainerIfc;
import org.caesarj.runtime.aspects.AspectRegistryIfc;
import org.caesarj.runtime.aspects.AspectThreadMapper;
import org.caesarj.runtime.aspects.CompositeAspectContainer;

/**
 * CaesarThread extends java.lang.Thread in order to make new threads
 * inherit the aspectual behaviour of the creating Thread instance.
 * Means that all (at creation time) in the creating thread deployed instances,
 * will be deployed for this CaesarThread instance, too.
 */
public class CaesarThread extends Thread {

	public CaesarThread() {
		super();
		deployParentThreadInstances();
	}

	public CaesarThread(Runnable target) {
		super(target);
		deployParentThreadInstances();
	}

	public CaesarThread(ThreadGroup group, Runnable target) {
		super(group, target);
		deployParentThreadInstances();
	}

	public CaesarThread(String name) {
		super(name);
		deployParentThreadInstances();
	}

	public CaesarThread(ThreadGroup group, String name) {
		super(group, name);
		deployParentThreadInstances();
	}

	public CaesarThread(Runnable target, String name) {
		super(target, name);
		deployParentThreadInstances();
	}

	public CaesarThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		deployParentThreadInstances();
	}

	public CaesarThread(
		ThreadGroup group,
		Runnable target,
		String name,
		long stackSize) {
		super(group, target, name, stackSize);
		deployParentThreadInstances();
	}

	/**
	 * Deploys all Instances of of the parent thread in the
	 * new CaesarThread.
	 */
	protected void deployParentThreadInstances() {

		/* iterate through all registries, which have deployed objects on the thread */
		Set activeRegistries = (Set) AspectRegistryIfc.threadLocalRegistries.get();
		Iterator iterator = activeRegistries.iterator();
		while (iterator.hasNext()) {
			AspectRegistryIfc currentRegistry = (AspectRegistryIfc) iterator.next();
			
			AspectContainerIfc cont = currentRegistry.$getAspectContainer();
			AspectThreadMapper threadMapper = null;
			
			/* find thread mapper */
			if (cont != null) {
				if (cont.$getContainerType() == AspectContainerIfc.THREAD_MAPPER) {
					threadMapper = (AspectThreadMapper)cont;
				}
				else if (cont.$getContainerType() == AspectContainerIfc.COMPOSITE_CONTAINER) {
					CompositeAspectContainer composite = (CompositeAspectContainer)cont;
					threadMapper = (AspectThreadMapper)composite.findContainer(AspectContainerIfc.THREAD_MAPPER);
				}
			}
			
			if (threadMapper != null) {
				/* copy deployed instances of parent thread */
				threadMapper.copyObjects(Thread.currentThread(), this);				
			}
		}
	}

}
