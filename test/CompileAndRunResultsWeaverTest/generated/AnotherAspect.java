// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnotherAspect.java

package generated;

import java.io.PrintStream;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            AnotherAspect_Ifc, AnotherAspect_MultiInstances, AnotherAspect_MultiThreads, AnotherAspect_AspectRegistry

public class AnotherAspect
    implements AnotherAspect_Ifc
{

    public synchronized void ajc$before$generated_AnotherAspect$7(JoinPoint joinpoint)
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("AnotherAspect Before: " + joinpoint.toString());
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new AnotherAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new AnotherAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(AnotherAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return AnotherAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    public AnotherAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
