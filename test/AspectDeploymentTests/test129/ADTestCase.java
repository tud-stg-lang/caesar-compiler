package generated.test129;

import junit.framework.TestCase;
import org.caesarj.runtime.CaesarThread;
import org.aspectj.runtime.internal.CFlowStack;

/**
 * Tests nested croscutting classes 
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

	public String expectedResult = "X";

	public void test()
	{
		System.out.println("-------> ADTest 29");

		deploy(new B().new X()) {
		    Test test = new Test();
		    test.foo();
		}
		
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 29: end");
	}
}

public class Test {
    public void foo() {}
}

public cclass A
{
    public cclass X 
    {
        after() : execution(public void Test.foo()) {
            System.out.println("----> X");
            ADTestCase.result.append("X");
        }
    } 
}


public cclass B {
    public cclass X {}
}
