package generated.test133;

import junit.framework.TestCase;

/**
 * Tests deploying fields in non-crosscutting class
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public static StringBuffer result = new StringBuffer();
	public String expectedResult = ":A-around:A-before:foo:A-after";

	public ADTestCase()
	{
		super("test");
	}

	public void test()
	{
		System.out.println("-------> ADTest 33");

		Test test = new Test();
		test.foo();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 33: end");
	}
}

public class Test {
    public void foo() {
    	ADTestCase.result.append(":foo");
    }
}

public abstract cclass AspectA {

	pointcut execFoo() : (execution(* Test.foo(..)));

	void around() : execFoo() {
		ADTestCase.result.append(":A-around");
		proceed();
	}

	before() : execFoo() {
		ADTestCase.result.append(":A-before");
	}

	after() : execFoo() {
		ADTestCase.result.append(":A-after");
	}	
}

public cclass AspectB extends AspectA {	
}

public cclass Deployment {
	public static deployed AspectB fieldB = new AspectB();
}