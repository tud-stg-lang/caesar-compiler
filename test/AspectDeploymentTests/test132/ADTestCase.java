package generated.test132;

import junit.framework.TestCase;

/**
 * Tests deploying class without crosscuts
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
		System.out.println("-------> ADTest 32");

		Test test = new Test();
		test.foo();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 32: end");
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

deployed public cclass AspectB extends AspectA {	
}