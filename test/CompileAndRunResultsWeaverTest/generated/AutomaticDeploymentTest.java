// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AutomaticDeploymentTest.java

package generated;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.aspectj.runtime.reflect.Factory;
import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            DeploymentThread, AutomaticDeploymentTestAspect, AutomaticDeploymentTestSubAspect, AutomaticDeploymentTestSubAspect_AspectRegistry, 
//            AutomaticDeploymentTestAspect_AspectRegistry

public class AutomaticDeploymentTest extends TestCase
{

    public AutomaticDeploymentTest()
    {
        super("test");
    }

    public void test()
    {
        DeploymentThread deploymentthread = new DeploymentThread();
        deploymentthread.start();
        StringBuffer stringbuffer = new StringBuffer();
        String s = "before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))AutomaticDeploymentTestMethodafter : execution(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))AutomaticDeploymentTestMethod";
        AutomaticDeploymentTestAspect automaticdeploymenttestaspect = new AutomaticDeploymentTestAspect();
        try
        {
            if(automaticdeploymenttestaspect != null)
                automaticdeploymenttestaspect._getSingletonAspect()._deploy(automaticdeploymenttestaspect, Thread.currentThread());
            AutomaticDeploymentTestAspect automaticdeploymenttestaspect1 = new AutomaticDeploymentTestAspect();
            try
            {
                if(automaticdeploymenttestaspect1 != null)
                    automaticdeploymenttestaspect1._getSingletonAspect()._deploy(automaticdeploymenttestaspect1, Thread.currentThread());
                AutomaticDeploymentTestSubAspect automaticdeploymenttestsubaspect = new AutomaticDeploymentTestSubAspect();
                try
                {
                    if(automaticdeploymenttestsubaspect != null)
                        automaticdeploymenttestsubaspect._getSingletonAspect()._deploy(automaticdeploymenttestsubaspect, Thread.currentThread());
                    StringBuffer stringbuffer1 = stringbuffer;
                    AutomaticDeploymentTest automaticdeploymenttest = this;
                    Object aobj[] = new Object[1];
                    aobj[0] = stringbuffer1;
                    org.aspectj.lang.JoinPoint joinpoint = Factory.makeJP(ajc$tjp_0, this, automaticdeploymenttest, aobj);
                    AutomaticDeploymentTestAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AutomaticDeploymentTestAspect$3c(stringbuffer1, joinpoint);
                    AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AutomaticDeploymentTestAspect$3c(stringbuffer1, joinpoint);
                    automaticdeploymenttest.automaticDeploymentTestMethod(stringbuffer1);
                }
                finally
                {
                    if(automaticdeploymenttestsubaspect != null)
                        automaticdeploymenttestsubaspect._getSingletonAspect()._undeploy();
                }
            }
            finally
            {
                if(automaticdeploymenttestaspect1 != null)
                    automaticdeploymenttestaspect1._getSingletonAspect()._undeploy();
            }
        }
        finally
        {
            if(automaticdeploymenttestaspect != null)
                automaticdeploymenttestaspect._getSingletonAspect()._undeploy();
        }
        StringBuffer stringbuffer2 = stringbuffer;
        AutomaticDeploymentTest automaticdeploymenttest1 = this;
        Object aobj1[] = new Object[1];
        aobj1[0] = stringbuffer2;
        org.aspectj.lang.JoinPoint joinpoint1 = Factory.makeJP(ajc$tjp_1, this, automaticdeploymenttest1, aobj1);
        AutomaticDeploymentTestAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AutomaticDeploymentTestAspect$3c(stringbuffer2, joinpoint1);
        AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance.ajc$before$generated_AutomaticDeploymentTestAspect$3c(stringbuffer2, joinpoint1);
        automaticdeploymenttest1.automaticDeploymentTestMethod(stringbuffer2);
        Assert.assertEquals(s, stringbuffer.toString());
    }

    public void automaticDeploymentTestMethod(StringBuffer arg0)
    {
        StringBuffer stringbuffer = arg0;
        Object aobj[] = new Object[1];
        aobj[0] = stringbuffer;
        org.aspectj.lang.JoinPoint joinpoint = Factory.makeJP(ajc$tjp_2, this, this, aobj);
        try
        {
            arg0.append("AutomaticDeploymentTestMethod");
        }
        catch(Throwable throwable)
        {
            AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_AutomaticDeploymentTestSubAspect$43(stringbuffer, joinpoint);
            throw throwable;
        }
        AutomaticDeploymentTestSubAspect_AspectRegistry.ajc$perSingletonInstance.ajc$after$generated_AutomaticDeploymentTestSubAspect$43(stringbuffer, joinpoint);
    }

    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_0;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_1;
    public static final org.aspectj.lang.JoinPoint.StaticPart ajc$tjp_2;

    static 
    {
        Factory factory = new Factory("AutomaticDeploymentTest.java", Class.forName("generated.AutomaticDeploymentTest"));
        ajc$tjp_0 = factory.makeSJP("method-call", factory.makeMethodSig("1-automaticDeploymentTestMethod-generated.AutomaticDeploymentTest-java.lang.StringBuffer:-arg0:--void-"), 27);
        ajc$tjp_1 = factory.makeSJP("method-call", factory.makeMethodSig("1-automaticDeploymentTestMethod-generated.AutomaticDeploymentTest-java.lang.StringBuffer:-arg0:--void-"), 33);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1-automaticDeploymentTestMethod-generated.AutomaticDeploymentTest-java.lang.StringBuffer:-arg0:--void-"), 42);
    }
}
