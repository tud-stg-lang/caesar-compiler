package generated.test131;

import junit.framework.TestCase;

/**
 * Tests advice precedence inside a class
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public static StringBuffer result = new StringBuffer();
	public String expectedResult = ":B-before1:B-before2:A-before1:A-before2:foo:A-after1:A-after2:B-after1:B-after2";

	public ADTestCase()
	{
		super("test");
	}

	public void test()
	{
		System.out.println("-------> ADTest 31");

		Test test = new Test();
		test.foo();

		System.out.println(result);
        assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 31: end");
	}
}

public class Test {
    public void foo() {
    	ADTestCase.result.append(":foo");
    }
}

public abstract cclass AspectB {

	pointcut execFoo() : (execution(* Test.foo(..)));

	before() : execFoo() {
		ADTestCase.result.append(":A-before1");
	}

	before() : execFoo() {
		ADTestCase.result.append(":A-before2");
	}

	after() : execFoo() {
		ADTestCase.result.append(":A-after1");
	}

	after() : execFoo() {
		ADTestCase.result.append(":A-after2");
	}

}

public deployed cclass AspectA extends AspectB {
	before() : execFoo() {
		ADTestCase.result.append(":B-before1");
	}

	before() : execFoo() {
		ADTestCase.result.append(":B-before2");
	}

	after() : execFoo() {
		ADTestCase.result.append(":B-after1");
	}

	after() : execFoo() {
		ADTestCase.result.append(":B-after2");
	}
}