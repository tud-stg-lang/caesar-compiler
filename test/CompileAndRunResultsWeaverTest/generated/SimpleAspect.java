// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SimpleAspect.java

package generated;

import java.io.PrintStream;
import java.util.Set;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            SimpleAspect_Ifc, SimpleAspect_MultiInstances, SimpleAspect_MultiThreads, SimpleAspect_AspectRegistry

public class SimpleAspect
    implements SimpleAspect_Ifc
{

    public synchronized void ajc$before$generated_SimpleAspect$9(int i)
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("Before-Advice execution in " + toString() + " in " + Thread.currentThread().toString());
    }

    public synchronized void ajc$after$generated_SimpleAspect$11(int i)
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("After-Advice Execution in " + toString() + " in " + Thread.currentThread().toString());
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new SimpleAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new SimpleAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(SimpleAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return SimpleAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    public SimpleAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
