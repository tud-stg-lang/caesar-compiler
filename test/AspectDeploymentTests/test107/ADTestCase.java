package generated.test107;

import java.io.IOException;

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

	public String expectedResult =
		":before foo:before foo:before foo:foo:foo:after foo:after foo:after foo";

	public void test()
	{
		System.out.println("-------> ADTest 7: Deployment Thread Safety: start");

	    new DeployA().test();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 7: end");
	}

    public static void foo()
    {
        Barrier.getInstance().check(); // Checkpoint 1, both threads

        ADTestCase.result.append(":foo");

        Barrier.getInstance().check(); // Checkpoint 2, both threads
    }
}

public cclass DeployA
{
    public void test()
    {
        Runnable anotherThread = new AnotherThread();
        new Thread(anotherThread).start();

        deploy(new AspectA())
        {
            ADTestCase.foo();
        }

        //barrier is important, since otherwise the assertion could done before
        //the other thread has written its result to the StringBuffer
        Barrier.getInstance().check(); //Checkpoint 3, thread main
    }
}

public cclass AnotherThread implements Runnable
{
	public void run()
	{
		deploy(new AspectA())
		{
			deploy(new AspectA())
			{
                ADTestCase.foo();
			}
		}

		Barrier.getInstance().check(); //Checkpoint 3, child thread
	}
}

public cclass AspectA
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
