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
//            FooAspect, FooAspect2_Ifc, FooAspect2_MultiInstances, FooAspect2_MultiThreads, 
//            FooAspect2_AspectRegistry

class FooAspect2 extends FooAspect
    implements FooAspect2_Ifc
{

    public synchronized void ajc$after$generated_FooAspect2$2e()
    {
        if(_getDeploymentThread() == Thread.currentThread())
            System.out.println("After Foo.goo.goo");
    }

    public FooAspect2(String s)
    {
        super(s);
    }

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new FooAspect2_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new FooAspect2_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(FooAspect2_AspectRegistry.ajc$perSingletonInstance);
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
        return FooAspect2_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    private Thread _deploymentThread; /* synthetic field */
}
