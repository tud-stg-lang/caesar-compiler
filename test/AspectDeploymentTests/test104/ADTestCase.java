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

		new DeployA().test();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 4: end");
	}
}

public cclass DeployA
{
    public void test()
    {
        deploy(new AspectA())
        {
            deploy(new AspectB())
            {
                deploy(new AspectC())
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

public deployed cclass Ordering
{
	declare precedence : AspectC, AspectA, AspectD, AspectB;   
}

public cclass AspectA
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo A");
	}
}

public cclass AspectB
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo B");
	}
}

public cclass AspectC
{
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo C");
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
