// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JoinPointReflectionTest.java

package generated;

import junit.framework.TestCase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;
import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            ReflectionAspect, World, StaticAspect, ReflectionAspect_AspectRegistry

public class JoinPointReflectionTest extends TestCase
{

    public JoinPointReflectionTest()
    {
        super("test");
        Block$();
    }

    public void test()
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s = "";
        ReflectionAspect reflectionaspect = aspect;
        try
        {
            if(reflectionaspect != null)
                reflectionaspect._getSingletonAspect()._deploy(reflectionaspect, Thread.currentThread());
            StringBuffer stringbuffer1 = stringbuffer;
            World world1 = world;
            Object aobj[] = new Object[1];
            aobj[0] = stringbuffer1;
            JoinPoint joinpoint = Factory.makeJP(ajc$tjp_0, this, world1, aobj);
            StaticAspect.ajc$perSingletonInstance.ajc$before$generated_StaticAspect$9();
            Object aobj1[] = new Object[4];
            aobj1[0] = this;
            aobj1[1] = world1;
            aobj1[2] = stringbuffer1;
            aobj1[3] = joinpoint;
            ReflectionAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_ReflectionAspect$2f(world1, stringbuffer1, new AjcClosure1(aobj1), ajc$tjp_0, joinpoint, ajc$tjp_1);
        }
        finally
        {
            if(reflectionaspect != null)
                reflectionaspect._getSingletonAspect()._undeploy();
        }
        StringBuffer stringbuffer2 = stringbuffer;
        World world2 = world;
        Object aobj2[] = new Object[1];
        aobj2[0] = stringbuffer2;
        JoinPoint joinpoint1 = Factory.makeJP(ajc$tjp_2, this, world2, aobj2);
        StaticAspect.ajc$perSingletonInstance.ajc$before$generated_StaticAspect$9();
        Object aobj3[] = new Object[4];
        aobj3[0] = this;
        aobj3[1] = world2;
        aobj3[2] = stringbuffer2;
        aobj3[3] = joinpoint1;
        ReflectionAspect_AspectRegistry.ajc$perSingletonInstance.ajc$around$generated_ReflectionAspect$2f(world2, stringbuffer2, new AjcClosure3(aobj3), ajc$tjp_2, joinpoint1, ajc$tjp_1);
    }

    private void Block$()
    {
        world = new World();
        aspect = new ReflectionAspect();
    }

    static final void m_aroundBody0(JoinPointReflectionTest joinpointreflectiontest, World world1, StringBuffer stringbuffer, JoinPoint joinpoint)
    {
        try
        {
            world1.m(stringbuffer);
        }
        catch(Throwable throwable)
        {
            StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
            throw throwable;
        }
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
    }

    static final void m_aroundBody2(JoinPointReflectionTest joinpointreflectiontest, World world1, StringBuffer stringbuffer, JoinPoint joinpoint)
    {
        try
        {
            world1.m(stringbuffer);
        }
        catch(Throwable throwable)
        {
            StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
            throw throwable;
        }
        StaticAspect.ajc$perSingletonInstance.ajc$after$generated_StaticAspect$d();
    }

    private World world;
    private ReflectionAspect aspect;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_0;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_1;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_2;

    static 
    {
        Factory factory = new Factory("JoinPointReflectionTest.java", Class.forName("generated.JoinPointReflectionTest"));
        ajc$tjp_0 = factory.makeSJP("method-call", factory.makeMethodSig("1-m-generated.World-java.lang.StringBuffer:-arg0:--void-"), 22);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1-test-generated.JoinPointReflectionTest----void-"), 17);
        ajc$tjp_2 = factory.makeSJP("method-call", factory.makeMethodSig("1-m-generated.World-java.lang.StringBuffer:-arg0:--void-"), 26);
    }

    private class AjcClosure1 extends AroundClosure
    {

        public Object run(Object aobj[])
        {
            Object aobj1[] = super.state;
            JoinPointReflectionTest.m_aroundBody0((JoinPointReflectionTest)aobj1[0], (World)aobj[0], (StringBuffer)aobj[1], (JoinPoint)aobj1[3]);
            return null;
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
            JoinPointReflectionTest.m_aroundBody2((JoinPointReflectionTest)aobj1[0], (World)aobj[0], (StringBuffer)aobj[1], (JoinPoint)aobj1[3]);
            return null;
        }

        public AjcClosure3(Object aobj[])
        {
            super(aobj);
        }
    }

}
