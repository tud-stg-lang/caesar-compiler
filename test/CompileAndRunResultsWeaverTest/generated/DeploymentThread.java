// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AutomaticDeploymentTest.java

package generated;

import org.caesarj.runtime.AspectRegistry;

// Referenced classes of package generated:
//            AutomaticDeploymentTestAspect

class DeploymentThread extends Thread
{

    public void run()
    {
        AutomaticDeploymentTestAspect automaticdeploymenttestaspect = new AutomaticDeploymentTestAspect();
        try
        {
            if(automaticdeploymenttestaspect != null)
                automaticdeploymenttestaspect._getSingletonAspect()._deploy(automaticdeploymenttestaspect, Thread.currentThread());
        }
        finally
        {
            if(automaticdeploymenttestaspect != null)
                automaticdeploymenttestaspect._getSingletonAspect()._undeploy();
        }
    }

    DeploymentThread()
    {
    }
}
