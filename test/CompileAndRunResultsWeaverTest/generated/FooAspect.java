// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   KlausTest.java

package generated;

import java.io.PrintStream;
import java.util.Set;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            FooAspect_Ifc, FooAspect_MultiInstances, FooAspect_MultiThreads, FooAspect_AspectRegistry

class FooAspect
    implements FooAspect_Ifc
{

    public synchronized void ajc$before$generated_FooAspect$1d()
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("Before bar ");
    }

    public synchronized void ajc$after$generated_FooAspect$21()
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("After bar ");
    }

    public FooAspect(String s1)
    {
        s = s1;
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new FooAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new FooAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(FooAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return FooAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    private String s;
    private Thread _deploymentThread; /* synthetic field */
}
