// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NestedAspectTest.java

package generated;

import java.io.PrintStream;
import java.util.*;
import junit.framework.TestCase;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

public class NestedAspectTest extends TestCase
{
    public class NestedAspect
        implements NestedAspect_Ifc
    {

        public synchronized void ajc$before$generated_NestedAspectTest$NestedAspect$19()
        {
            if(_getDeploymentThread() == Thread.currentThread())
                System.out.println("NestedAspect: Before xyz");
        }

        public synchronized Deployable _deploy(Deployable deployable)
        {
            Object obj;
            if(_deploymentThread == deployable._getDeploymentThread())
            {
                obj = new NestedAspect_MultiInstances();
                ((Deployable) (obj))._setDeploymentThread(_deploymentThread);
            } else
            {
                obj = new NestedAspect_MultiThreads();
            }
            ((Deployable) (obj))._deploy(this);
            ((Deployable) (obj))._deploy(deployable);
            return ((Deployable) (obj));
        }

        public synchronized Deployable _undeploy()
        {
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.remove(NestedAspect_AspectRegistry.ajc.perSingletonInstance);
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
            return NestedAspect_AspectRegistry.ajc.perSingletonInstance;
        }

        public Deployable _getThreadLocalDeployedInstances()
        {
            return this;
        }

        private Thread _deploymentThread; /* synthetic field */

        public NestedAspect()
        {
        }
    }

    static interface NestedAspect_Ifc
        extends Deployable
    {

        public abstract void ajc$before$generated_NestedAspectTest$NestedAspect$19();
    }

    class NestedAspect_MultiInstances
        implements NestedAspect_Ifc
    {

        public synchronized Deployable _deploy(Deployable deployable)
        {
            if(_deploymentThread == deployable._getDeploymentThread())
            {
                _deployedInstances.push(deployable);
                return this;
            } else
            {
                NestedAspect_MultiThreads nestedaspect_multithreads = new NestedAspect_MultiThreads();
                nestedaspect_multithreads._deploy(this);
                nestedaspect_multithreads._deploy(deployable);
                return nestedaspect_multithreads;
            }
        }

        public synchronized Deployable _undeploy()
        {
            _deployedInstances.pop();
            if(_deployedInstances.size() < 2)
                return (Deployable)_deployedInstances.pop();
            else
                return this;
        }

        public synchronized void _setDeploymentThread(Thread thread)
        {
            _deploymentThread = thread;
        }

        public synchronized Thread _getDeploymentThread()
        {
            return _deploymentThread;
        }

        protected synchronized Stack _getDeployedInstances()
        {
            return _deployedInstances;
        }

        public Deployable _getThreadLocalDeployedInstances()
        {
            return this;
        }

        public synchronized void ajc$before$generated_NestedAspectTest$NestedAspect$19()
        {
            if(_getDeploymentThread() == Thread.currentThread())
            {
                NestedAspect_Ifc nestedaspect_ifc;
                for(Iterator iterator = _getDeployedInstances().iterator(); iterator.hasNext(); nestedaspect_ifc._mth19())
                    nestedaspect_ifc = (NestedAspect_Ifc)iterator.next();

            }
        }

        private void Block$()
        {
            _deployedInstances = new Stack();
        }

        private Stack _deployedInstances; /* synthetic field */
        private Thread _deploymentThread; /* synthetic field */

        NestedAspect_MultiInstances()
        {
            Block$();
        }
    }

    class NestedAspect_MultiThreads
        implements NestedAspect_Ifc
    {

        public synchronized Deployable _deploy(Deployable deployable)
        {
            Deployable deployable1 = (Deployable)_perThreadDeployedInstances.get(deployable._getDeploymentThread());
            if(deployable1 != null)
            {
                deployable1 = deployable1._deploy(deployable);
                _perThreadDeployedInstances.put(deployable._getDeploymentThread(), deployable1);
            } else
            {
                _perThreadDeployedInstances.put(deployable._getDeploymentThread(), deployable);
            }
            return this;
        }

        public synchronized Deployable _undeploy()
        {
            Deployable deployable = (Deployable)_perThreadDeployedInstances.get(Thread.currentThread());
            if(deployable != null)
                deployable = deployable._undeploy();
            if(deployable == null)
            {
                _perThreadDeployedInstances.remove(Thread.currentThread());
                if(_perThreadDeployedInstances.size() < 2)
                    return (Deployable)_perThreadDeployedInstances.values().iterator().next();
            } else
            {
                _perThreadDeployedInstances.put(Thread.currentThread(), deployable);
            }
            return this;
        }

        public synchronized void _setDeploymentThread(Thread thread)
        {
            _deploymentThread = thread;
        }

        public synchronized Thread _getDeploymentThread()
        {
            return _deploymentThread;
        }

        protected synchronized Map _getDeployedInstances()
        {
            return _perThreadDeployedInstances;
        }

        public Deployable _getThreadLocalDeployedInstances()
        {
            return (Deployable)_getDeployedInstances().get(Thread.currentThread());
        }

        public synchronized void ajc$before$generated_NestedAspectTest$NestedAspect$19()
        {
            NestedAspect_Ifc nestedaspect_ifc = (NestedAspect_Ifc)_getDeployedInstances().get(Thread.currentThread());
            if(nestedaspect_ifc != null)
                nestedaspect_ifc._mth19();
        }

        private void Block$()
        {
            _perThreadDeployedInstances = new WeakHashMap();
        }

        private WeakHashMap _perThreadDeployedInstances; /* synthetic field */
        private Thread _deploymentThread; /* synthetic field */

        NestedAspect_MultiThreads()
        {
            Block$();
        }
    }

    public static class NestedAspect_AspectRegistry
        implements AspectRegistry
    {

        public synchronized void _deploy(Deployable arg0, Thread arg1)
        {
            if(arg0 == null)
                return;
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.add(this);
            arg0._setDeploymentThread(arg1);
            if(_deployedInstances == null)
            {
                _deployedInstances = (NestedAspect_Ifc)arg0;
                return;
            } else
            {
                _deployedInstances = (NestedAspect_Ifc)_deployedInstances._deploy(arg0);
                return;
            }
        }

        public synchronized void _undeploy()
        {
            _deployedInstances = (NestedAspect_Ifc)_deployedInstances._undeploy();
        }

        public synchronized Deployable _getDeployedInstances()
        {
            return _deployedInstances;
        }

        public Deployable _getThreadLocalDeployedInstances()
        {
            if(_getDeployedInstances() != null)
                return _getDeployedInstances()._getThreadLocalDeployedInstances();
            else
                return null;
        }

        private static void ajc$clinit()
        {
            ajc$perSingletonInstance = new NestedAspect_AspectRegistry();
            try
            {
                Class.forName("generated.NestedAspectTest$NestedAspect");
                return;
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                return;
            }
        }

        public void ajc$before$generated_NestedAspectTest$NestedAspect$19()
        {
            if(_getDeployedInstances() != null)
                ((NestedAspect_Ifc)_getDeployedInstances())._mth19();
        }

        private NestedAspect_Ifc _deployedInstances; /* synthetic field */
        public static final NestedAspect_AspectRegistry ajc$perSingletonInstance; /* synthetic field */

        private static 
        {
            ajc$clinit();
        }

        public NestedAspect_AspectRegistry()
        {
        }
    }


    public NestedAspectTest()
    {
        super("test");
    }

    public void test()
    {
        NestedAspect nestedaspect = new NestedAspect();
        NestedAspect nestedaspect1 = nestedaspect;
        try
        {
            if(nestedaspect1 != null)
                nestedaspect1._getSingletonAspect()._deploy(nestedaspect1, Thread.currentThread());
            NestedAspect_AspectRegistry.ajc.perSingletonInstance._mth19();
            xyz();
        }
        finally
        {
            if(nestedaspect1 != null)
                nestedaspect1._getSingletonAspect()._undeploy();
        }
    }

    public void xyz()
    {
        System.out.println("NestedAspectTest: m");
    }
}
