package generated.test103;

import junit.framework.TestCase;

/**
 * Test abstract crosscutting classes, implementing abstract pointcuts
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

	public String expectedResult = ":before foo:foo";

	public void test()
	{
		System.out.println("-------> ADTest 3: Abstract Pointcuts: start");

		deploy(new ConcreteAspect())
		{
			foo();
		}

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 3: end");
	}

	public void foo()
	{
		result.append(":foo");
	}
}

public abstract cclass AbstractAspect
{
	abstract pointcut callFoo();

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo");
	}
}

public cclass ConcreteAspect extends AbstractAspect
{
	pointcut callFoo() : call(* ADTestCase.foo());
}
