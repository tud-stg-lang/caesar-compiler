package generated.test135;

import junit.framework.TestCase;

/**
 * Test aspect() method
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
		":before foo 1:foo:before foo 2:foo";

	public void test()
	{
		System.out.println("-------> ADTest 35: start");

		new DeployA().test();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 35: end");
	}
}

public cclass DeployA
{
    public void test()
    {
    	AspectD_Impl.aspect().setId("1");
    	foo();
    	AspectD_Impl.aspect().setId("2");
    	foo();
    }

    public void foo()
    {
        ADTestCase.result.append(":foo");
    }
}

public deployed cclass AspectD
{
	private String id = "";
	
	public void setId(String id) {
		this.id = id;
	}
	
	pointcut callFoo() : call(* DeployA.foo());

	before() : callFoo()
	{
		ADTestCase.result.append(":before foo " + id);
	}
}
