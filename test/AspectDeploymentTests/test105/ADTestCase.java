package generated.test105;

import junit.framework.TestCase;

/**
 * Test after returning, after throwing, usage of join point reflection
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
	}

	public static int fooCounter = 0;

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":around foo:foo:after returning foo:around foo:after throwing ex foo:after returning bar 42";

	public void test()
	{
		System.out.println("-------> ADTest 5: Returning, Throwing, Reflection: start");

		new DeployA().test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 5: end");
	}
}

cclass DeployA
{
    public void test()
    {
        deploy(new AspectA())
        {
            foo();

            try
            {
                foo();
            }
            catch (RuntimeException e)
            { }

            bar();
        }
    }

    public void foo() throws RuntimeException
    {
        if (ADTestCase.fooCounter == 1)
        {
            throw new RuntimeException("ex");
        }

        ADTestCase.result.append(":foo");
        ADTestCase.fooCounter++;
    }

    public int bar()
    {
        return 42;
    }
}

cclass AspectA
{
	pointcut callFoo() : call(* DeployA.foo());

	pointcut callBar() : call(* DeployA.bar());

	void around() : callFoo()
	{
		ADTestCase.result.append(":around foo");
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();
		proceed();
	}

	after() returning : callFoo()
	{
		ADTestCase.result.append(":after returning foo");
	}

	after() throwing(RuntimeException e) : callFoo()
	{
		ADTestCase.result.append(
			":after throwing " + e.getMessage() + " foo");
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();
	}

	after() returning(int i) : callBar()
	{
		ADTestCase.result.append(":after returning bar " + i);
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();
	}
}
