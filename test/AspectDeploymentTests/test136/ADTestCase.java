package generated.test136;

import junit.framework.TestCase;

/**
 * Test precedence declarations with +
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
		":before foo D:before foo C:before foo B:foo";

	public void test()
	{
		System.out.println("-------> ADTest 36: Aspect Precedence: start");

		new DeployA().test();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 36: end");
	}
}

public cclass DeployA
{
    public void test()
    {
        deploy(new AspectB())
        {
            deploy(new AspectC())
            {
                foo();
            }
        }
    }

    public void foo()
    {
        ADTestCase.result.append(":foo");
    }
}

public deployed cclass Ordering
{
	declare precedence : *, AspectA+;   
	declare precedence : AspectD, AspectC+;   
}

public cclass AspectA
{
}

public cclass AspectC
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo C");
	}
}

public cclass AspectB extends AspectA
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo B");
	}
}

public deployed cclass AspectD
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo D");
	}
}