package generated.test127;

import junit.framework.TestCase;

/**
 * Test cflow pointcuts
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
	}

	public static int fooCounter = 0;

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":before outerCalcArg(a1, c1, 2):before1 calc(a1, c1, 0):before2 calc(a1)";

	public void test()
	{
		System.out.println("-------> ADTest 27: Test cflow");

		new DeployA().test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 27: end");
	}
}

cclass DeployA
{
    public void test()
    {
		CalcA c1 = new CalcA().init("c1");

		AspectA a1 = new AspectA().init("a1");

		deploy(a1)
        {
			int res1 = c1.calcArg(2);
			int res2 = c1.calc();
		}
    }
}

cclass AspectA
{
	String _id;

	public AspectA init(String id)
	{
		_id = id;
		return this;
	}

	pointcut calcArgCall(Object c, CalcA a, int n) : call(* CalcA.calcArg(..)) && target(a) && args(n) && this(c);

	before(Object c1, CalcA a1, int n1) :
		calcArgCall(c1, a1, n1) && cflow(calcArgCall(Object, CalcA, int)) && !cflowbelow(calcArgCall(Object, CalcA, int))
	{
		ADTestCase.result.append(":before outerCalcArg(" + _id + ", " + a1.getId() + ", " + n1 + ")");
	}

	before(Object c1, CalcA a1, int n1) :
			call(* CalcA.calc()) && cflow(calcArgCall(c1, a1, n1))
	{
		ADTestCase.result.append(":before1 calc(" + _id + ", " + a1.getId() + ", " + n1 + ")");
	}

	before() :
			call(* CalcA.calc()) && cflowbelow(calcArgCall(Object, CalcA, int))
	{
		ADTestCase.result.append(":before2 calc(" + _id + ")");
	}
}


cclass CalcA
{
	String _id;

	public CalcA init(String id)
	{
		_id = id;
		return this;
	}

	public String getId()
	{
		return _id;
	}

	public int calc()
	{
		return 1;
	}

	public int calcArg(int n)
	{
		if (n > 0)
		{
			return calcArg(n - 1) + 1;
		}
		else
		{
			return calc();
		}
	}
}
