package generated.test106;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test around advices
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

	public String expectedResult = ":foo:around foo 42:around bar:ioe";

	public void test()
	{
		System.out.println("-------> ADTest 6: Around Advice: start");

        new DeployA_Impl(null).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 6: end");
	}

}

public cclass DeployA
{
    public void test()
    {
        deploy(new AspectA_Impl(null))
        {
            int i = foo();

            try
            {
                bar();
            }
            catch (IOException e)
            {
                ADTestCase.result.append(e.getMessage());
            }
        }
    }

    public int foo()
    {
	    ADTestCase.result.append(":foo");
        return 42;
    }

    public void bar() throws IOException
    {
	    ADTestCase.result.append(":bar");
    }
}

cclass AspectA
{
	pointcut callFoo() : call(* DeployA.foo());

	pointcut callBar() : call(* DeployA.bar());

	Object around() : callFoo()
	{
		int result = ((Integer) proceed()).intValue();

		ADTestCase.result.append(":around foo " + result);

		return new Integer(result);
	}

	void around() throws IOException : callBar()
	{
		ADTestCase.result.append(":around bar");

		throw new IOException(":ioe");
	}
}
