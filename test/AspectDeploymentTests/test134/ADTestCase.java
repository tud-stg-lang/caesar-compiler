package generated.test134;

import junit.framework.TestCase;


/**
 * Test declares in abstract classes
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
		System.out.println("-------> ADTest 34: Declares in abstract class: start");

		new DeployA().test();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 34: end");
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

abstract public cclass AbstractOrdering
{
    declare precedence :  AspectC, AspectA, AspectD, AspectB;    
}

public deployed cclass Ordering extends AbstractOrdering {
	pointcut notUsed() : call(* DeployA.foo());
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
