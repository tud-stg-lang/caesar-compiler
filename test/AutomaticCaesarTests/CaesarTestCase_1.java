package generated;

import junit.framework.TestCase;

public class CaesarTestCase_1 extends TestCase
{

	public CaesarTestCase_1()
	{
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public String expectedResult =
		":foo:before foo:foo:before foo:foo:after foo";

    public void test()
    {
        new TestCase_1_Impl(null).init(result).test();
        System.out.println(result);
        assertEquals(expectedResult, result.toString());
    }
}

cclass TestCase_1 {

    StringBuffer result;

	public TestCase_1 init(StringBuffer result)
	{
		this.result = result;
		return this;
	}

	public StringBuffer getResult()
	{
		return result;
	}

	/**
	 * tests inheriting aspects
	 *
	 */
	public void test()
	{
		deploy($newInnerAspect())
		{
			foo();
		}

		deploy($newInnerAspect_Sub())
		{
			foo();
		}

		deploy($newInnerAspect_Sub_Sub())
		{
			foo();
		}
	}

	public void foo()
	{
		result.append(":foo");
	}

	cclass InnerAspect
	{
		pointcut fooCall() : call(* TestCase_1.foo());

		before() : fooCall()
		{
			System.out.println("just for testing");
		}
	}

	cclass InnerAspect_Sub extends InnerAspect
	{
		before() : fooCall()
		{
			$outer.getResult().append(":before foo");
		}

	}

	cclass InnerAspect_Sub_Sub extends InnerAspect_Sub
	{
		after() : fooCall()
		{
			$outer.getResult().append(":after foo");
		}
	}
}
