package generated.test100;

import junit.framework.TestCase;

/**
 * Test dynamic deployment with inheritance
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
    }

    public StringBuffer result = new StringBuffer();

    public String expectedResult =
        "|before-foo|foo|subbefore-foo|before-foo|before-foo|foo";

    public void test()
    {
		System.out.println("-------> ADTest 0: Dynamic Deployment Inner Aspects: start");

        new OuterA_Impl(null).init(result).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 0: end");
    }
}

public cclass OuterA
{
    StringBuffer result;

    public OuterA init(StringBuffer result)
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

	public cclass InnerAspect
	{
		pointcut fooCall() : call(* OuterA.foo());

		before() : fooCall()
		{
		  $outer.getResult().append("|before-foo");
		}
	}

	public cclass InnerAspect_Sub extends InnerAspect
	{
		before() : fooCall()
		{
			$outer.getResult().append("|subbefore-foo");
		}
	}
}



