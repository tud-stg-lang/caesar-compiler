package generated;

import junit.framework.TestCase;

/**
 * declare precdence for crosscutting and deployed classes
 */
public class CaesarTestCase_4 extends TestCase
{

	public CaesarTestCase_4()
	{
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before foo C:before foo A:before foo D:before foo B:foo";

	public void test()
	{
		new TestCase_4_Impl(null).test();

		assertEquals(expectedResult, result.toString());
	}
}

cclass TestCase_4
{
    public void test()
    {
        deploy(new A_4_Impl(null))
        {
            deploy(new B_4_Impl(null))
            {
                deploy(new C_4_Impl(null))
                {
                    foo();
                }
            }
        }
    }

    public void foo()
    {
        CaesarTestCase_4.result.append(":foo");
    }
}

deployed cclass Ordering
{
    // TODO
	declare precedence : C_4_Impl, A_4_Impl, D_4_Impl, B_4_Impl;
    //declare precedence : C_4, A_4, D_4, B_4;
}


cclass A_4
{
	pointcut callFoo() : call(* TestCase_4.foo());

	before() : callFoo()
	{
		CaesarTestCase_4.result.append(":before foo A");
	}
}

cclass B_4
{
	pointcut callFoo() : call(* TestCase_4.foo());

	before() : callFoo()
	{
		CaesarTestCase_4.result.append(":before foo B");
	}
}

cclass C_4
{
	pointcut callFoo() : call(* TestCase_4.foo());

	before() : callFoo()
	{
		CaesarTestCase_4.result.append(":before foo C");
	}
}

deployed cclass D_4
{
	pointcut callFoo() : call(* TestCase_4.foo());

	before() : callFoo()
	{
		CaesarTestCase_4.result.append(":before foo D");
	}
}
