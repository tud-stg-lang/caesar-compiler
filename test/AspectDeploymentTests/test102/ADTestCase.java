package generated.test102;

import junit.framework.TestCase;

/**
 * Test static aspect deployment
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("bar"); // cannot dynamically call "test", because such call is not recognized by weaver
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before call foo:before exec foo:before exec foo:before exec foo:foo:after exec foo:after exec foo" + // first foo
		":before call foo:before exec foo:before exec foo:foo:after exec foo"; 								   // second foo

    public void bar()
    {
		System.out.println("-------> ADTest 2: Dynamic Deployment Inner Aspects: start");

    	test();

    	System.out.println(result.toString());
				assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 2: end");
    }

	public void test()
	{
		deploy(new AspectB())
		{
			foo();
		}

		foo();
	}

	public void foo()
	{
		result.append(":foo");
	}
}

public deployed cclass StaticAspectA {

	pointcut callFoo() : cflow(call(* ADTestCase.test())) && call(* ADTestCase.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before call foo");
	}

}

public cclass AspectA
{
	public final static deployed AspectA THIS = new AspectA();

	pointcut execFoo() : execution(* ADTestCase.foo());

	before() : execFoo()
	{
		ADTestCase.result.append(":before exec foo");
	}
}

public cclass AspectB extends AspectA
{
	public final static deployed AspectB THIS = new AspectB();

	after() : execFoo()
	{
		ADTestCase.result.append(":after exec foo");
	}
}
