package org.caesarj.runtime;

import java.util.HashSet;

/**
 * The singleton aspect classes (the aspect registries) need to
 * implement this interface.
 * 
 * @author Jürgen Hallpap
 */
public interface AspectRegistry {

	public void _deploy(Deployable aspectToDeploy, Thread thread);

	public void _undeploy();

	public Deployable _getDeployedInstances();
	
	public Deployable _getThreadLocalDeployedInstances();
	
	

	public static ThreadLocal threadLocalRegistries = new ThreadLocal() {

		protected synchronized Object initialValue() {
			return new HashSet();
		}

	};

}
