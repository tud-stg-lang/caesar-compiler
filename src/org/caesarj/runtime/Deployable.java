package org.caesarj.runtime;

/**
 * The caesar source classes (and the associated deployment support classes) 
 * with aspect behaviour need to implement this interface.
 * It is added to the class´ interfaces automatically, means the caesar developer
 * has not to care about it.
 * 
 * @author Jürgen Hallpap
 */
public interface Deployable {
	
	public Deployable $deploy(Deployable aspectToDeploy);
	
	public Deployable $undeploy();
	
	public Thread $getDeploymentThread();
	
	public void $setDeploymentThread(Thread deploymentThread);
	
	public Deployable $getThreadLocalDeployedInstances();

}
