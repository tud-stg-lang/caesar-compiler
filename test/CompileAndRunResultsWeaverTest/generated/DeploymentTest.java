// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DeploymentTest.java

package generated;

import java.io.PrintStream;
import java.io.Serializable;
import junit.framework.TestCase;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            AnotherThread, SimpleAspect, AnotherAspect, SubSimpleAspect, 
//            SimpleAspect_AspectRegistry, StaticAspect, SubSimpleAspect_AspectRegistry, AnotherAspect_AspectRegistry, 
//            SuperAspect_AspectRegistry

public class DeploymentTest extends TestCase
    implements Serializable
{

    public DeploymentTest()
    {
        super("test");
        Block$();
    }

    public void test()
    {
        lockObject = new Object();
        AnotherThread anotherthread = new AnotherThread();
        anotherthread.start();
        SimpleAspect simpleaspect = new SimpleAspect();
        try
        {
            if(simpleaspect != null)
                simpleaspect._getSingletonAspect()._deploy(simpleaspect, Thread.currentThread());
            SimpleAspect simpleaspect1 = new SimpleAspect();
            try
            {
                if(simpleaspect1 != null)
                    simpleaspect1._getSingletonAspect()._deploy(simpleaspect1, Thread.currentThread());
                SimpleAspect simpleaspect2 = new SimpleAspect();
                try
                {
                    if(simpleaspect2 != null)
                        simpleaspect2._getSingletonAspect()._deploy(simpleaspect2, Thread.currentThread());
                    byte byte0 = 12;
                    Object aobj[] = new Object[2];
                    aobj[0] = this;
                    aobj[1] = Conversions.intObject(byte0);
                    System.out.println(SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_SubSimpleAspect$9(byte0, new AjcClosure1(aobj)));
                    synchronizeThreads();
                }
                finally
                {
                    if(simpleaspect2 != null)
                        simpleaspect2._getSingletonAspect()._undeploy();
                }
            }
            finally
            {
                if(simpleaspect1 != null)
                    simpleaspect1._getSingletonAspect()._undeploy();
            }
        }
        finally
        {
            if(simpleaspect != null)
                simpleaspect._getSingletonAspect()._undeploy();
        }
        byte byte1 = 66;
        Object aobj1[] = new Object[2];
        aobj1[0] = this;
        aobj1[1] = Conversions.intObject(byte1);
        System.out.println(SubSimpleAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_SubSimpleAspect$9(byte1, new AjcClosure3(aobj1)));
    }

    public static int m(int arg0)
    {
        int i = arg0;
        Object aobj[] = new Object[1];
        aobj[0] = Conversions.intObject(i);
        org.aspectj.lang.JoinPoint joinpoint = Factory.makeJP(ajc$tjp_0, null, null, aobj);
        SuperAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SuperAspect$7(joinpoint);
        AnotherAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AnotherAspect$7(joinpoint);
        System.out.println("Method execution in " + Thread.currentThread().toString());
        return arg0;
    }

    public void synchronizeThreads()
    {
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
        synchronized(AnotherThread.lockObject)
        {
            AnotherThread.otherThreadReady = true;
            AnotherThread.lockObject.notify();
        }
    }

    private void Block$()
    {
        aspect = new SimpleAspect();
        subAspect = new SubSimpleAspect();
    }

    static final int m_aroundBody0(DeploymentTest deploymenttest, int i)
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
        return m(i);
    }

    static final int m_aroundBody2(DeploymentTest deploymenttest, int i)
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
        return m(i);
    }

    public SimpleAspect aspect;
    public SimpleAspect subAspect;
    public static final AnotherAspect anotherAspect;
    public static boolean otherThreadReady = false;
    public static Object lockObject;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_0;

    private static 
    {
        Factory factory = new Factory("DeploymentTest.java", Class.forName("generated.DeploymentTest"));
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("9-m-generated.DeploymentTest-int:-arg0:--int-"), 49);
        anotherAspect = new AnotherAspect();
        anotherAspect._getSingletonAspect()._deploy(anotherAspect, Thread.currentThread());
    }

    private class AjcClosure1 extends AroundClosure
    {

        public Object run(Object aobj[])
        {
            Object aobj1[] = super.state;
            return Conversions.intObject(DeploymentTest.m_aroundBody0((DeploymentTest)aobj1[0], Conversions.intValue(aobj[0])));
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
            return Conversions.intObject(DeploymentTest.m_aroundBody2((DeploymentTest)aobj1[0], Conversions.intValue(aobj[0])));
        }

        public AjcClosure3(Object aobj[])
        {
            super(aobj);
        }
    }

}
