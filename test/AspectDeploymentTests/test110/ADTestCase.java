package generated.test110;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test statically deployed around advices
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

	public String expectedResult = ":around foo:foo:around foo";

	public void test()
	{
		System.out.println("-------> ADTest 10: Statically Deployed Around Advice: start");

		foo();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 10: end");
	}

	public static void foo()
	{
		result.append(":foo");
	}
}

public deployed cclass AspectA
{
	pointcut execFoo() : execution(* ADTestCase.foo());

	void around() : execFoo()
	{
		ADTestCase.result.append(":around foo");
		proceed();
		ADTestCase.result.append(":around foo");
	}
}

