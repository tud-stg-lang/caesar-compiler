package generated;

import junit.framework.TestCase;

public class CaesarTestCase_2 extends TestCase
{

	public CaesarTestCase_2()
	{
		super("bar"); // cannot dynamically call "test", because such call is not recognized by weaver
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before call foo:before exec foo:before exec foo:before exec foo:foo:after exec foo:after exec foo:" + // first foo
		"before call foo:before exec foo:before exec foo:foo:after exec foo"; 									// second foo

    public void bar()
    {
    	test();
    }

	public void test()
	{
		deploy(new Aspect_2_Sub_Impl(null))
		{
			foo();
		}

		foo();

		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());
	}

	public void foo()
	{
		result.append(":foo");
	}
}

deployed cclass StaticAspect_2 {

	pointcut callFoo() : cflow(call(* CaesarTestCase_2.test())) && call(* CaesarTestCase_2.foo());

	before() : callFoo()
	{
		CaesarTestCase_2.result.append(":before call foo");
	}

}

/* inheritance from statically deployed classes is not supported
deployed cclass StaticAspect_2_Sub extends StaticAspect_2
{
	after() : callFoo()
	{
		CaesarTestCase_2.result.append(":after foo");
	}
}
*/

cclass Aspect_2
{
	public final static deployed Aspect_2 THIS = new Aspect_2_Impl(null);

	pointcut execFoo() : execution(* CaesarTestCase_2.foo());

	before() : execFoo()
	{
		CaesarTestCase_2.result.append(":before exec foo");
	}
}

cclass Aspect_2_Sub extends Aspect_2
{
	public final static deployed Aspect_2_Sub THIS = new Aspect_2_Sub_Impl(null);

	after() : execFoo()
	{
		CaesarTestCase_2.result.append(":after exec foo");
	}
}
