package org.caesarj.runtime;

import java.util.Iterator;
import java.util.Set;

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

		Set activeRegistries = (Set) AspectRegistry.threadLocalRegistries.get();
		Iterator iterator = activeRegistries.iterator();
		while (iterator.hasNext()) {
			AspectRegistry currentRegistry = (AspectRegistry) iterator.next();
			Deployable parentThreadInstances =
				currentRegistry.$getThreadLocalDeployedInstances();

			currentRegistry.$deploy(parentThreadInstances, this);
		}

	}

}
