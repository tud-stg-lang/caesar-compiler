// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JoinPointReflectionTest.java

package generated;

import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.AroundClosure;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            ReflectionAspect_Ifc, ReflectionAspect_AspectRegistry, ReflectionAspect_MultiInstances, ReflectionAspect_MultiThreads, 
//            World

class ReflectionAspect
    implements ReflectionAspect_Ifc
{

    public synchronized void ajc$around$generated_ReflectionAspect$2f(World world, StringBuffer stringbuffer, AroundClosure aroundclosure, org.aspectj.lang.JoinPoint.StaticPart staticpart, JoinPoint joinpoint, org.aspectj.lang.JoinPoint.StaticPart staticpart1)
    {
        if(_getDeploymentThread() == Thread.currentThread())
        {
            stringbuffer.append("ReflectionAspect: Around: " + joinpoint.toString());
            stringbuffer.append("ReflectionAspect: Around: " + staticpart.toString());
            stringbuffer.append("ReflectionAspect: Around: " + staticpart1.toString());
            ReflectionAspect_AspectRegistry.ajc$around$generated_ReflectionAspect$2fproceed(world, stringbuffer, aroundclosure);
            return;
        } else
        {
            ReflectionAspect_AspectRegistry.ajc$around$generated_ReflectionAspect$2fproceed(world, stringbuffer, aroundclosure);
            return;
        }
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new ReflectionAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new ReflectionAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(ReflectionAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return ReflectionAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    ReflectionAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
