// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrivilegedAccessTest.java

package generated;

import junit.framework.TestCase;

// Referenced classes of package generated:
//            PrivateAccessClass

public class PrivilegedAccessTest extends TestCase
{

    public PrivilegedAccessTest()
    {
        super("test");
    }

    public void test()
    {
        PrivateAccessClass privateaccessclass = new PrivateAccessClass();
        PrivateAccessClass.ajc$privFieldSet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateString(privateaccessclass, "new private String");
        PrivateAccessClass.ajc$privFieldSet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateInt(privateaccessclass, 3);
        String s = PrivateAccessClass.ajc$privFieldGet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateString(privateaccessclass);
        int i = PrivateAccessClass.ajc$privFieldGet$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateInt(privateaccessclass);
        privateaccessclass.ajc$privMethod$generated_PrivilegedAccessTest$generated_PrivateAccessClass$privateMethod();
    }
}
