// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrivilegedAccessTest.java

package generated;


class PrivateAccessClass
{

    private boolean privateMethod()
    {
        return true;
    }

    PrivateAccessClass()
    {
        Block$();
    }

    private void Block$()
    {
        privateInt = 5;
        privateString = "a private String";
    }

    public static int ajc$privFieldGet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateInt(PrivateAccessClass privateaccessclass)
    {
        return privateaccessclass.privateInt;
    }

    public static void ajc$privFieldSet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateInt(PrivateAccessClass privateaccessclass, int i)
    {
        privateaccessclass.privateInt = i;
    }

    public static String ajc$privFieldGet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateString(PrivateAccessClass privateaccessclass)
    {
        return privateaccessclass.privateString;
    }

    public static void ajc$privFieldSet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateString(PrivateAccessClass privateaccessclass, String s)
    {
        privateaccessclass.privateString = s;
    }

    public boolean ajc$privMethod$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateMethod()
    {
        return privateMethod();
    }

    private int privateInt;
    private String privateString;
}
