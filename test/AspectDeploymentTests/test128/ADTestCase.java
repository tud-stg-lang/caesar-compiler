package generated.test128;

import junit.framework.TestCase;
import org.caesarj.runtime.CaesarThread;
import org.aspectj.runtime.internal.CFlowStack;

/**
 * Test cross-thread cflow pointcuts
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
		System.out.println("-------> ADTest 28: Test cross-tread cflow");

		new DeployA().test();

		System.out.println(result);
		//assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 28: end");
	}
}

public cclass DeployA
{
    public void test()
    {
		CalcA c1 = new CalcA().init("c1");
		CalcA c2 = new CalcA().init("c2");

		AspectA a1 = new AspectA().init("a1");

		deploy(a1)
        {
			Thread th1 = new AnotherThread(c1, 2);
			Thread th2 = new AnotherThread(c2, 2);
			th1.run();
			th2.run();
			c1.calc();
		}
    }
}

public cclass AspectA
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

public cclass CalcA
{
	String _id;

	int _res = 0;

	public CalcA init(String id)
	{
		_id = id;
		return this;
	}

	public String getId()
	{
		return _id;
	}

	public void calc()
	{
		_res = 1;
	}

	public void calcArg(int n)
	{
		if (n > 0)
		{
			Thread th = new AnotherThread(this, n - 1);
			th.start();
			_res++;
		}
		else
		{
			calc();
		}
	}
}

class AnotherThread extends CaesarThread
{
	CalcA _a;
	int _n;

	public AnotherThread(CalcA a, int n)

	{
		_a = a; _n = n;
	}

	public void run()
	{
		_a.calcArg(_n);
	}
}

public deployed cclass ObserveCFlowStack
{
	after() : call(* CFlowStack.*(..))
	{
		System.out.println(":cflowstack called");
	}
}