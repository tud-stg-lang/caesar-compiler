// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AutomaticDeploymentTest.java

package generated;

import java.util.Set;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

// Referenced classes of package generated:
//            AutomaticDeploymentTestSuperAspect_Ifc, AutomaticDeploymentTestSuperAspect_MultiInstances, AutomaticDeploymentTestSuperAspect_MultiThreads, AutomaticDeploymentTestSuperAspect_AspectRegistry

class AutomaticDeploymentTestSuperAspect
    implements AutomaticDeploymentTestSuperAspect_Ifc
{

    public synchronized Deployable _deploy(Deployable deployable)
    {
        Object obj;
        if(_deploymentThread == deployable._getDeploymentThread())
        {
            obj = new AutomaticDeploymentTestSuperAspect_MultiInstances();
            ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
        } else
        {
            obj = new AutomaticDeploymentTestSuperAspect_MultiThreads();
        }
        ((Deployable) (obj))._deploy(this);
        ((Deployable) (obj))._deploy(deployable);
        return ((Deployable) (obj));
    }

    public synchronized Deployable _undeploy()
    {
        Set set = (Set)AspectRegistry.threadLocalRegistries.get();
        set.remove(AutomaticDeploymentTestSuperAspect_AspectRegistry.ajc$perSingletonInstance);
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
        return AutomaticDeploymentTestSuperAspect_AspectRegistry.ajc$perSingletonInstance;
    }

    public Deployable _getThreadLocalDeployedInstances()
    {
        return this;
    }

    AutomaticDeploymentTestSuperAspect()
    {
    }

    private Thread _deploymentThread; /* synthetic field */
}
