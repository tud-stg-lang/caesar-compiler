package generated.test101;

import junit.framework.TestCase;

/**
 * Test longer inner aspect inheritance sequence
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
		":foo:before foo:foo:before foo:foo:after foo";

    public void test()
    {
		System.out.println("-------> ADTest 1: Inner Aspects Inheritance Sequence: start");

        new OuterA().init(result).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 1: end");
    }
}

cclass OuterA
{
    StringBuffer result;

	public OuterA init(StringBuffer result)
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
	 */
	public void test()
	{
		deploy(new InnerAspect())
		{
			foo();
		}

		deploy(new InnerAspect_Sub())
		{
			foo();
		}

		deploy(new InnerAspect_Sub_Sub())
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
		pointcut fooCall() : call(* OuterA.foo());

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
