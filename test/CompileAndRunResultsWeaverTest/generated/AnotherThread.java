// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DeploymentTest.java

package generated;

import java.io.PrintStream;
import org.aspectj.runtime.internal.Conversions;
import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            SubSimpleAspect, SimpleAspect, DeploymentTest, SimpleAspect_AspectRegistry, 
//            StaticAspect, SubSimpleAspect_AspectRegistry

class AnotherThread extends Thread
{

    public void run()
    {
        lockObject = new Object();
        SubSimpleAspect subsimpleaspect = new SubSimpleAspect();
        try
        {
            if(subsimpleaspect != null)
                subsimpleaspect._getSingletonAspect()._deploy(subsimpleaspect, Thread.currentThread());
            SubSimpleAspect subsimpleaspect1 = new SubSimpleAspect();
            try
            {
                if(subsimpleaspect1 != null)
                    subsimpleaspect1._getSingletonAspect()._deploy(subsimpleaspect1, Thread.currentThread());
                SimpleAspect simpleaspect = new SimpleAspect();
                try
                {
                    if(simpleaspect != null)
                        simpleaspect._getSingletonAspect()._deploy(simpleaspect, Thread.currentThread());
                    byte byte0 = 42;
                    Object aobj[] = new Object[2];
                    aobj[0] = this;
                    aobj[1] = Conversions.intObject(byte0);
                    System.out.println(SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_SubSimpleAspect$9(byte0, new AjcClosure1(aobj)));
                    synchronizeThreads();
                }
                finally
                {
                    if(simpleaspect != null)
                        simpleaspect._getSingletonAspect()._undeploy();
                }
            }
            finally
            {
                if(subsimpleaspect1 != null)
                    subsimpleaspect1._getSingletonAspect()._undeploy();
            }
        }
        finally
        {
            if(subsimpleaspect != null)
                subsimpleaspect._getSingletonAspect()._undeploy();
        }
        byte byte1 = 100;
        Object aobj1[] = new Object[2];
        aobj1[0] = this;
        aobj1[1] = Conversions.intObject(byte1);
        System.out.println(SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_SubSimpleAspect$9(byte1, new AjcClosure3(aobj1)));
    }

    public void synchronizeThreads()
    {
        synchronized(DeploymentTest.lockObject)
        {
            DeploymentTest.otherThreadReady = true;
            DeploymentTest.lockObject.notify();
        }
        synchronized(lockObject)
        {
            if(!otherThreadReady)
                try
                {
                    lockObject.wait();
                }
                catch(InterruptedException interruptedexception)
                {
                    System.out.println(interruptedexception.toString());
                }
        }
    }

    AnotherThread()
    {
    }

    static final int m_aroundBody0(AnotherThread anotherthread, int i)
    {
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SimpleAspect$9(i);
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SimpleAspect$9(i);
        StaticAspect.ajc$perSingletonInstance.ajc$before$generated_StaticAspect$9();
          goto _L1
        throwable;
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        throw throwable;
_L1:
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        break MISSING_BLOCK_LABEL_60;
        throwable1;
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
        throw throwable1;
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
          goto _L2
        throwable2;
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        throw throwable2;
_L2:
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        return DeploymentTest.m(i);
    }

    static final int m_aroundBody2(AnotherThread anotherthread, int i)
    {
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SimpleAspect$9(i);
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SimpleAspect$9(i);
        StaticAspect.ajc$perSingletonInstance.ajc$before$generated_StaticAspect$9();
          goto _L1
        throwable;
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        throw throwable;
_L1:
        SimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        break MISSING_BLOCK_LABEL_60;
        throwable1;
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
        throw throwable1;
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
          goto _L2
        throwable2;
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        throw throwable2;
_L2:
        SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_SimpleAspect$11(i);
        return DeploymentTest.m(i);
    }

    public static Object lockObject;
    public static boolean otherThreadReady = false;


    private class AjcClosure1 extends AroundClosure
    {

        public Object run(Object aobj[])
        {
            Object aobj1[] = super.state;
            return Conversions.intObject(AnotherThread.m_aroundBody0((AnotherThread)aobj1[0], Conversions.intValue(aobj[0])));
        }

        public AjcClosure1(Object aobj[])
        {
            super(aobj);
        }
    }


    private class AjcClosure3 extends AroundClosure
    {

        public Object run(Object aobj[])
        {
            Object aobj1[] = super.state;
            return Conversions.intObject(AnotherThread.m_aroundBody2((AnotherThread)aobj1[0], Conversions.intValue(aobj[0])));
        }

        public AjcClosure3(Object aobj[])
        {
            super(aobj);
        }
    }

}
