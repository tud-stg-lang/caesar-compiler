// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StaticAspect.java

package generated;

import java.io.PrintStream;

public class StaticAspect
{

    private StaticAspect()
    {
    }

    private static void ajc$clinit()
    {
        ajc$perSingletonInstance = new StaticAspect();
    }

    public void ajc$before$generated_StaticAspect$9()
    {
        System.out.println("Statically deployed Before-Advice.");
    }

    public void ajc$after$generated_StaticAspect$d()
    {
        System.out.println("Statically deployed After-Advice.");
    }

    public static final StaticAspect ajc$perSingletonInstance; /* synthetic field */

    private static 
    {
        ajc$clinit();
    }
}
