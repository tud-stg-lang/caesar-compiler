package generated;


import junit.framework.TestCase;

/**
 * static Aspect w/concrete Pointcut inherits from static abstract aspect
 * with concrete advice.
 */
public class CaesarTestCase_11 extends TestCase
{

    public CaesarTestCase_11()
    {
        super("test");
    }

    public static StringBuffer result = new StringBuffer();

    public String expectedResult = ":before foo(bar):foo";

    public void test()
    {
        new TestCase_11_Impl(null).test();
        assertEquals(expectedResult, result.toString());
    }

}

public cclass TestCase_11
{
    public void test()
    {
        deploy(new Aspect_11a_Impl(null))
        {
            foo("bar");
        }
    }

    public void foo(String s)
    {
        CaesarTestCase_11.result.append(":foo");
    }

}

abstract cclass Aspect_11
{
	abstract pointcut execFoo(String s);

	before(String s) : execFoo(s)
	{
		CaesarTestCase_11.result.append(":before foo(" + s +")");
	}
}

cclass Aspect_11a extends Aspect_11
{
	pointcut execFoo(String s) : execution(* TestCase_11.foo(String)) && args(s);
}


