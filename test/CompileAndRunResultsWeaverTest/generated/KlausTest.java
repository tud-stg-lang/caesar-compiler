// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   KlausTest.java

package generated;

import junit.framework.TestCase;
import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            Foo_Impl, Fooo, FooAspect2, Foo, 
//            FooAspect2_AspectRegistry

public class KlausTest extends TestCase
{

    public KlausTest()
    {
        super("test");
    }

    public void test()
    {
        Foo_Impl foo_impl;
        Fooo.Goo goo;
        FooAspect2 fooaspect2;
        foo_impl = new Foo_Impl();
        goo = (Fooo.Goo)foo_impl._createGoo();
        fooaspect2 = new FooAspect2("Outer");
        if(fooaspect2 != null)
            fooaspect2._getSingletonAspect()._deploy(fooaspect2, Thread.currentThread());
        foo_impl.foo();
        goo;
        goo();
        break MISSING_BLOCK_LABEL_71;
        Throwable throwable;
        throwable;
        FooAspect2_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_FooAspect2$2e();
        throw throwable;
        FooAspect2_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_FooAspect2$2e();
        return;
        if(fooaspect2 != null)
            fooaspect2._getSingletonAspect()._undeploy();
        JVM INSTR ret 5;
    }
}
