package generated;

import junit.framework.TestCase;

public class CaesarTestCase_0 extends TestCase
{

    public StringBuffer result = new StringBuffer();

    public String expectedResult =
        "|before-foo|foo|subbefore-foo|before-foo|before-foo|foo";

    public CaesarTestCase_0()
    {
        super("test");
    }

    public void test()
    {
        new TestCase_0_Impl(null).init(result).test();
        System.out.println(result);
        assertEquals(expectedResult, result.toString());
    }
}

cclass TestCase_0
{
    StringBuffer result;

    public TestCase_0 init(StringBuffer result)
    {
		this.result = result;
		return this;
	}

	public void test()
	{
		deploy($newInnerAspect())
		{
			foo();

            deploy($newInnerAspect_Sub())
            {
                foo();
            }
		}
	}

	public StringBuffer getResult()
	{
		return result;
	}

	public void foo()
	{
		result.append("|foo");
	}

	cclass InnerAspect
	{
	    pointcut fooCall() : call(* TestCase_0.foo());

	    before() : fooCall()
	    {
		  $outer.getResult().append("|before-foo");
	    }
	}

    cclass InnerAspect_Sub extends InnerAspect
    {
		before() : fooCall()
        {
            $outer.getResult().append("|subbefore-foo");
		}
	}
}
