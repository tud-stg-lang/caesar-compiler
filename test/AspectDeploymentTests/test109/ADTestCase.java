package generated.test109;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test thread safety of deployment
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

    public String expectedResult = ":before foo:foo:after foo:before foo:foo:after foo:foo";

    public void test()
    {
		System.out.println("-------> ADTest 9: Deployment Thread Safety: start");

        new DeployA_Impl(null).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 9: end");
    }

    public static void foo()
    {
        result.append(":foo");
    }
}

public cclass DeployA
{
    public void test()
    {
        deploy(new Aspect_Impl(null))
        {
            Thread anotherThread = new AnotherThread();
            anotherThread.start();
        }

        Barrier.getInstance().check(); // 1

        Barrier.getInstance().check(); // 2

        ADTestCase.foo();
    }
}

class AnotherThread extends CaesarThread
{
	public void run()
	{
		ADTestCase.foo();

		Barrier.getInstance().check(); // 1

		ADTestCase.foo();

		Barrier.getInstance().check(); // 2
	}
}

cclass Aspect
{
	pointcut callFoo() : call(* ADTestCase.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo");
	}

	after() : callFoo()
	{
		ADTestCase.result.append(":after foo");
	}
}
