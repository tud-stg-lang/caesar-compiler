package generated.test104;

import junit.framework.TestCase;


/**
 * Test precedence declaration for crosscutting and deployed classes
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

	public String expectedResult =
		":before foo C:before foo A:before foo D:before foo B:foo";

	public void test()
	{
		System.out.println("-------> ADTest 4: Aspect Precedence: start");

		new DeployA_Impl(null).test();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 4: end");
	}
}

cclass DeployA
{
    public void test()
    {
        deploy(new AspectA_Impl(null))
        {
            deploy(new AspectB_Impl(null))
            {
                deploy(new AspectC_Impl(null))
                {
                    foo();
                }
            }
        }
    }

    public void foo()
    {
        ADTestCase.result.append(":foo");
    }
}

deployed cclass Ordering
{
    // TODO
	declare precedence : AspectC_Impl, AspectA_Impl, AspectD_Impl, AspectB_Impl;
    //declare precedence : AspectC, AspectA, AspectD, AspectB;
}


cclass AspectA
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo A");
	}
}

cclass AspectB
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo B");
	}
}

cclass AspectC
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo C");
	}
}

deployed cclass AspectD
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo D");
	}
}
