// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SubSimpleAspect.java

package generated;

import java.io.PrintStream;
import java.util.Set;
import org.aspectj.runtime.internal.AroundClosure;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            SimpleAspect, SubSimpleAspect_Ifc, SubSimpleAspect_AspectRegistry, SubSimpleAspect_MultiInstances, 
//            SubSimpleAspect_MultiThreads

public class SubSimpleAspect extends SimpleAspect
    implements SubSimpleAspect_Ifc
{

    public synchronized int ajc$around$generated_SubSimpleAspect$9(int i, AroundClosure aroundclosure)
    {
        if(_getDeploymentThread() == Thread.currentThread())
        {
            System.out.println("SubSimpleAspect: Around " + i);
            return SubSimpleAspect_AspectRegistry.ajc$around$generated_SubSimpleAspect$9proceed(i, aroundclosure);
        } else
        {
            return SubSimpleAspect_AspectRegistry.ajc$around$generated_SubSimpleAspect$9proceed(i, aroundclosure);
        }
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new SubSimpleAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new SubSimpleAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    public SubSimpleAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
