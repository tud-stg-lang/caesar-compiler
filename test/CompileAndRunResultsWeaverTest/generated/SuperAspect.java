// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SuperAspect.java

package generated;

import java.io.PrintStream;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            SuperAspect_Ifc, SuperAspect_MultiInstances, SuperAspect_MultiThreads, SuperAspect_AspectRegistry

public class SuperAspect
    implements SuperAspect_Ifc
{

    public synchronized void ajc$before$generated_SuperAspect$7(JoinPoint joinpoint)
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("SuperAspect Before: " + joinpoint.toString());
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new SuperAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new SuperAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(SuperAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return SuperAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    public SuperAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
