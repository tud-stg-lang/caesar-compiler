// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AutomaticDeploymentTest.java

package generated;

import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            AutomaticDeploymentTestAspect, AutomaticDeploymentTestSubAspect_Ifc, AutomaticDeploymentTestSubAspect_MultiInstances, AutomaticDeploymentTestSubAspect_MultiThreads, 
//            AutomaticDeploymentTestSubAspect_AspectRegistry

class AutomaticDeploymentTestSubAspect extends AutomaticDeploymentTestAspect
    implements AutomaticDeploymentTestSubAspect_Ifc
{

    public synchronized void ajc$after$generated_AutomaticDeploymentTestSubAspect$43(StringBuffer stringbuffer, JoinPoint joinpoint)
    {
        if(_getDeploymentThread() == Thread.currentThread())
            stringbuffer.append("after : " + joinpoint.toString());
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new AutomaticDeploymentTestSubAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new AutomaticDeploymentTestSubAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance);
        return null;
    }

    public synchronized Thread _getDeploymentThread()
    {
        return _deploymentThread;
    }

    public synchronized void _setDeploymentThread(Thread thread)
    {
        _deploymentThread = thread;
    }

    public synchronized AspectRegistry _getSingletonAspect()
    {
        return AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    AutomaticDeploymentTestSubAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
