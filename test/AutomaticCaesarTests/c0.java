/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   CaesarTestCase_0.java

package generated;

import java.util.*;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.caesarj.runtime.AspectRegistry;
import org.caesarj.runtime.Deployable;

public class CaesarTestCase_0 extends TestCase
{
    class InnerAspect
        implements Ifc
    {

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect$24()
        {
            if($getDeploymentThread() == Thread.currentThread())
                result.append(":before foo");
        }

        public synchronized Deployable $deploy(Deployable deployable)
        {
            Object obj;
            if($deploymentThread == deployable.$getDeploymentThread())
            {
                obj = new MultiInstanceContainer();
                ((Deployable) (obj)).$setDeploymentThread($deploymentThread);
            } else
            {
                obj = new ThreadMapper();
            }
            ((Deployable) (obj)).$deploy(this);
            ((Deployable) (obj)).$deploy(deployable);
            return ((Deployable) (obj));
        }

        public synchronized Deployable $undeploy()
        {
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.remove(Registry.ajc.perSingletonInstance);
            return null;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized AspectRegistry $getSingletonAspect()
        {
            return Registry.ajc.perSingletonInstance;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return this;
        }

        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect()
        {
            this$0 = CaesarTestCase_0.this;
        }
    }

    public static interface InnerAspect.Ifc
        extends Deployable
    {

        public abstract void ajc$before$generated_CaesarTestCase_0$InnerAspect$24();
    }

    class InnerAspect.MultiInstanceContainer
        implements InnerAspect.Ifc
    {

        public synchronized Deployable $deploy(Deployable deployable)
        {
            if($deploymentThread == deployable.$getDeploymentThread())
            {
                $deployedInstances.push(deployable);
                return this;
            } else
            {
                InnerAspect.ThreadMapper threadmapper = new InnerAspect.ThreadMapper();
                threadmapper.$deploy(this);
                threadmapper.$deploy(deployable);
                return threadmapper;
            }
        }

        public synchronized Deployable $undeploy()
        {
            $deployedInstances.pop();
            if($deployedInstances.size() < 2)
                return (Deployable)$deployedInstances.pop();
            else
                return this;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        protected synchronized Stack $getDeployedInstances()
        {
            return $deployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return this;
        }

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect$24()
        {
            if($getDeploymentThread() == Thread.currentThread())
            {
                InnerAspect.Ifc ifc;
                for(Iterator iterator = $getDeployedInstances().iterator(); iterator.hasNext(); ifc._mth24())
                    ifc = (InnerAspect.Ifc)iterator.next();

            }
        }

        private void Block$()
        {
            $deployedInstances = new Stack();
        }

        private Stack $deployedInstances; /* synthetic field */
        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect.MultiInstanceContainer()
        {
            this$0 = CaesarTestCase_0.this;
            Block$();
        }
    }

    class InnerAspect.ThreadMapper
        implements InnerAspect.Ifc
    {

        public synchronized Deployable $deploy(Deployable deployable)
        {
            Deployable deployable1 = (Deployable)$perThreadDeployedInstances.get(deployable.$getDeploymentThread());
            if(deployable1 != null)
            {
                deployable1 = deployable1.$deploy(deployable);
                $perThreadDeployedInstances.put(deployable.$getDeploymentThread(), deployable1);
            } else
            {
                $perThreadDeployedInstances.put(deployable.$getDeploymentThread(), deployable);
            }
            return this;
        }

        public synchronized Deployable $undeploy()
        {
            Deployable deployable = (Deployable)$perThreadDeployedInstances.get(Thread.currentThread());
            if(deployable != null)
                deployable = deployable.$undeploy();
            if(deployable == null)
            {
                $perThreadDeployedInstances.remove(Thread.currentThread());
                if($perThreadDeployedInstances.size() < 2)
                    return (Deployable)$perThreadDeployedInstances.values().iterator().next();
            } else
            {
                $perThreadDeployedInstances.put(Thread.currentThread(), deployable);
            }
            return this;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        protected synchronized Map $getDeployedInstances()
        {
            return $perThreadDeployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return (Deployable)$getDeployedInstances().get(Thread.currentThread());
        }

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect$24()
        {
            InnerAspect.Ifc ifc = (InnerAspect.Ifc)$getDeployedInstances().get(Thread.currentThread());
            if(ifc != null)
                ifc._mth24();
        }

        private void Block$()
        {
            $perThreadDeployedInstances = new WeakHashMap();
        }

        private WeakHashMap $perThreadDeployedInstances; /* synthetic field */
        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect.ThreadMapper()
        {
            this$0 = CaesarTestCase_0.this;
            Block$();
        }
    }

    static class InnerAspect.Registry
        implements AspectRegistry
    {

        public synchronized void $deploy(Deployable arg0, Thread arg1)
        {
            if(arg0 == null)
                return;
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.add(this);
            arg0.$setDeploymentThread(arg1);
            if($deployedInstances == null)
            {
                $deployedInstances = (InnerAspect.Ifc)arg0;
                return;
            } else
            {
                $deployedInstances = (InnerAspect.Ifc)$deployedInstances.$deploy(arg0);
                return;
            }
        }

        public synchronized void $undeploy()
        {
            $deployedInstances = (InnerAspect.Ifc)$deployedInstances.$undeploy();
        }

        public synchronized Deployable $getDeployedInstances()
        {
            return $deployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            if($getDeployedInstances() != null)
                return $getDeployedInstances().$getThreadLocalDeployedInstances();
            else
                return null;
        }

        private static void ajc$clinit()
        {
            ajc$perSingletonInstance = new InnerAspect.Registry();
            try
            {
                Class.forName("generated.CaesarTestCase_0$InnerAspect");
                return;
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                return;
            }
        }

        public final void ajc$before$generated_CaesarTestCase_0$InnerAspect$24()
        {
            if($getDeployedInstances() != null)
                ((InnerAspect.Ifc)$getDeployedInstances())._mth24();
        }

        private InnerAspect.Ifc $deployedInstances; /* synthetic field */
        public static final InnerAspect.Registry ajc$perSingletonInstance; /* synthetic field */

        private static 
        {
            ajc$clinit();
        }

        InnerAspect.Registry()
        {
        }
    }

    class InnerAspect_Sub extends InnerAspect
        implements Ifc
    {

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect_Sub$29()
        {
            if($getDeploymentThread() == Thread.currentThread())
                result.append(":subbefore foo");
        }

        public synchronized Deployable $deploy(Deployable deployable)
        {
            Object obj;
            if($deploymentThread == deployable.$getDeploymentThread())
            {
                obj = new MultiInstanceContainer();
                ((Deployable) (obj)).$setDeploymentThread($deploymentThread);
            } else
            {
                obj = new ThreadMapper();
            }
            ((Deployable) (obj)).$deploy(this);
            ((Deployable) (obj)).$deploy(deployable);
            return ((Deployable) (obj));
        }

        public synchronized Deployable $undeploy()
        {
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.remove(Registry.ajc.perSingletonInstance);
            return null;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized AspectRegistry $getSingletonAspect()
        {
            return Registry.ajc.perSingletonInstance;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return this;
        }

        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect_Sub()
        {
            this$0 = CaesarTestCase_0.this;
        }
    }

    public static interface InnerAspect_Sub.Ifc
        extends Deployable
    {

        public abstract void ajc$before$generated_CaesarTestCase_0$InnerAspect_Sub$29();
    }

    class InnerAspect_Sub.MultiInstanceContainer extends InnerAspect.MultiInstanceContainer
        implements InnerAspect_Sub.Ifc
    {

        public synchronized Deployable $deploy(Deployable deployable)
        {
            if($deploymentThread == deployable.$getDeploymentThread())
            {
                $deployedInstances.push(deployable);
                return this;
            } else
            {
                InnerAspect_Sub.ThreadMapper threadmapper = new InnerAspect_Sub.ThreadMapper();
                threadmapper.$deploy(this);
                threadmapper.$deploy(deployable);
                return threadmapper;
            }
        }

        public synchronized Deployable $undeploy()
        {
            $deployedInstances.pop();
            if($deployedInstances.size() < 2)
                return (Deployable)$deployedInstances.pop();
            else
                return this;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        protected synchronized Stack $getDeployedInstances()
        {
            return $deployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return this;
        }

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect_Sub$29()
        {
            if($getDeploymentThread() == Thread.currentThread())
            {
                InnerAspect_Sub.Ifc ifc;
                for(Iterator iterator = $getDeployedInstances().iterator(); iterator.hasNext(); ifc._mth29())
                    ifc = (InnerAspect_Sub.Ifc)iterator.next();

            }
        }

        private void Block$()
        {
            $deployedInstances = new Stack();
        }

        private Stack $deployedInstances; /* synthetic field */
        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect_Sub.MultiInstanceContainer()
        {
            this$0 = CaesarTestCase_0.this;
            Block$();
        }
    }

    class InnerAspect_Sub.ThreadMapper extends InnerAspect.ThreadMapper
        implements InnerAspect_Sub.Ifc
    {

        public synchronized Deployable $deploy(Deployable deployable)
        {
            Deployable deployable1 = (Deployable)$perThreadDeployedInstances.get(deployable.$getDeploymentThread());
            if(deployable1 != null)
            {
                deployable1 = deployable1.$deploy(deployable);
                $perThreadDeployedInstances.put(deployable.$getDeploymentThread(), deployable1);
            } else
            {
                $perThreadDeployedInstances.put(deployable.$getDeploymentThread(), deployable);
            }
            return this;
        }

        public synchronized Deployable $undeploy()
        {
            Deployable deployable = (Deployable)$perThreadDeployedInstances.get(Thread.currentThread());
            if(deployable != null)
                deployable = deployable.$undeploy();
            if(deployable == null)
            {
                $perThreadDeployedInstances.remove(Thread.currentThread());
                if($perThreadDeployedInstances.size() < 2)
                    return (Deployable)$perThreadDeployedInstances.values().iterator().next();
            } else
            {
                $perThreadDeployedInstances.put(Thread.currentThread(), deployable);
            }
            return this;
        }

        public synchronized void $setDeploymentThread(Thread thread)
        {
            $deploymentThread = thread;
        }

        public synchronized Thread $getDeploymentThread()
        {
            return $deploymentThread;
        }

        protected synchronized Map $getDeployedInstances()
        {
            return $perThreadDeployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            return (Deployable)$getDeployedInstances().get(Thread.currentThread());
        }

        public synchronized void ajc$before$generated_CaesarTestCase_0$InnerAspect_Sub$29()
        {
            InnerAspect_Sub.Ifc ifc = (InnerAspect_Sub.Ifc)$getDeployedInstances().get(Thread.currentThread());
            if(ifc != null)
                ifc._mth29();
        }

        private void Block$()
        {
            $perThreadDeployedInstances = new WeakHashMap();
        }

        private WeakHashMap $perThreadDeployedInstances; /* synthetic field */
        private Thread $deploymentThread; /* synthetic field */
        private final CaesarTestCase_0 this$0;

        InnerAspect_Sub.ThreadMapper()
        {
            this$0 = CaesarTestCase_0.this;
            Block$();
        }
    }

    static class InnerAspect_Sub.Registry extends InnerAspect.Registry
        implements AspectRegistry
    {

        public synchronized void $deploy(Deployable arg0, Thread arg1)
        {
            if(arg0 == null)
                return;
            Set set = (Set)AspectRegistry.threadLocalRegistries.get();
            set.add(this);
            arg0.$setDeploymentThread(arg1);
            if($deployedInstances == null)
            {
                $deployedInstances = (InnerAspect_Sub.Ifc)arg0;
                return;
            } else
            {
                $deployedInstances = (InnerAspect_Sub.Ifc)$deployedInstances.$deploy(arg0);
                return;
            }
        }

        public synchronized void $undeploy()
        {
            $deployedInstances = (InnerAspect_Sub.Ifc)$deployedInstances.$undeploy();
        }

        public synchronized Deployable $getDeployedInstances()
        {
            return $deployedInstances;
        }

        public Deployable $getThreadLocalDeployedInstances()
        {
            if($getDeployedInstances() != null)
                return $getDeployedInstances().$getThreadLocalDeployedInstances();
            else
                return null;
        }

        private static void ajc$clinit()
        {
            ajc$perSingletonInstance = new InnerAspect_Sub.Registry();
            try
            {
                Class.forName("generated.CaesarTestCase_0$InnerAspect_Sub");
                return;
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                return;
            }
        }

        public final void ajc$before$generated_CaesarTestCase_0$InnerAspect_Sub$29()
        {
            if($getDeployedInstances() != null)
                ((InnerAspect_Sub.Ifc)$getDeployedInstances())._mth29();
        }

        private InnerAspect_Sub.Ifc $deployedInstances; /* synthetic field */
        public static final InnerAspect_Sub.Registry ajc$perSingletonInstance; /* synthetic field */

        private static 
        {
            ajc$clinit();
        }

        InnerAspect_Sub.Registry()
        {
        }
    }


    public CaesarTestCase_0()
    {
        super("test");
        Block$();
    }

    public void test()
    {
        InnerAspect inneraspect = new InnerAspect();
        try
        {
            if(inneraspect != null)
                inneraspect.getSingletonAspect().$deploy(inneraspect, Thread.currentThread());
            InnerAspect_Sub.Registry.ajc.perSingletonInstance._mth29();
            InnerAspect.Registry.ajc.perSingletonInstance._mth24();
            InnerAspect_Sub.Registry.ajc.perSingletonInstance._mth24();
            foo();
        }
        finally
        {
            if(inneraspect != null)
                inneraspect.getSingletonAspect().$undeploy();
        }
        InnerAspect_Sub inneraspect_sub = new InnerAspect_Sub();
        try
        {
            if(inneraspect_sub != null)
                inneraspect_sub.getSingletonAspect().$deploy(inneraspect_sub, Thread.currentThread());
            InnerAspect_Sub.Registry.ajc.perSingletonInstance._mth29();
            InnerAspect.Registry.ajc.perSingletonInstance._mth24();
            InnerAspect_Sub.Registry.ajc.perSingletonInstance._mth24();
            foo();
        }
        finally
        {
            if(inneraspect_sub != null)
                inneraspect_sub.getSingletonAspect().$undeploy();
        }
        Assert.assertEquals(expectedResult, result.toString());
    }

    public void foo()
    {
        result.append(":foo");
    }

    private void Block$()
    {
        result = new StringBuffer();
        expectedResult = ":before foo:foo:subbefore foo:before foo:foo";
    }

    public StringBuffer result;
    public String expectedResult;
}



/***** DECOMPILATION REPORT *****

	DECOMPILED FROM: /home/sven/workspace/Caesar/test/AutomaticCaesarTests/generated/CaesarTestCase_0.class


	TOTAL TIME: 69 ms


	JAD REPORTED MESSAGES/ERRORS:


	EXIT STATUS:	0


	CAUGHT EXCEPTIONS:

 ********************************/
