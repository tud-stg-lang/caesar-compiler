// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JoinPointReflectionTest.java

package generated;

import org.aspectj.runtime.reflect.Factory;

// Referenced classes of package generated:
//            AnotherAspect_AspectRegistry, SuperAspect_AspectRegistry

public class World
{

    public void m(StringBuffer arg0)
    {
        StringBuffer stringbuffer = arg0;
        Object aobj[] = new Object[1];
        aobj[0] = stringbuffer;
        org.aspectj.lang.JoinPoint joinpoint = Factory.makeJP(ajc$tjp_0, this, this, aobj);
        SuperAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_SuperAspect$7(joinpoint);
        AnotherAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AnotherAspect$7(joinpoint);
        arg0.append("World: m");
    }

    World()
    {
    }

    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_0;

    static 
    {
        Factory factory = new Factory("JoinPointReflectionTest.java", Class.forName("generated.World"));
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1-m-generated.World-java.lang.StringBuffer:-arg0:--void-"), 34);
    }
}
