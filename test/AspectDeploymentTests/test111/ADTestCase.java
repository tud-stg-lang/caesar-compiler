package generated.test111;

import junit.framework.TestCase;

/**
 * Test static aspect with concrete pointcut inherits
 * from static abstract aspect with concrete advice.
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
    public ADTestCase()
    {
        super("test");
    }

    public static StringBuffer result = new StringBuffer();

    public String expectedResult = ":before foo(bar):foo";

    public void test()
    {
		System.out.println("-------> ADTest 11: Static Aspect Inherits Abstract Aspect: start");

        new OuterA_Impl(null).test();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 11: end");
    }
}

public cclass OuterA
{
    public void test()
    {
        foo("bar");
    }

    public void foo(String s)
    {
        ADTestCase.result.append(":foo");
    }
}

abstract cclass AspectA
{
	abstract pointcut execFoo(String s);

	before(String s) : execFoo(s)
	{
		ADTestCase.result.append(":before foo(" + s +")");
	}
}

deployed cclass AspectB extends AspectA
{
	pointcut execFoo(String s) : execution(* OuterA.foo(String)) && args(s);
}


