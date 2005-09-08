package generated.test139;

import java.util.List;

import junit.framework.TestCase;

/**
 * Test resolving copied advice
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
		":before testC:before testA:before testB:before testA:test";

	public void test()
	{
		System.out.println("-------> ADTest 39: Resolving copied advice");
		
		AspectB b = new AspectB();
		AspectC c = new AspectC();
		deploy b;
		deploy c;
		new Test().test(null);
		undeploy b;
		undeploy c;
		
		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 39: end");
	}
}

public cclass Test
{
    public void test(List lst)
    {
    	ADTestCase.result.append(":test");    	
    }
}


