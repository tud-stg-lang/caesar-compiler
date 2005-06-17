package generated.test123;

import junit.framework.TestCase;

/**
 * Test multi-instance around calls
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

	public String expectedResult =
		":in a.setIntA -9:in b.setIntA -7:in c.setIntA -5" +
			":in a.setIntB" +
				":in b.setIntB" +
					":in c.setIntB:setVal -3:setVal -3:out c.setIntB" +
					":in c.setIntB:setVal -3:setVal -3:out c.setIntB" +
				":out b.setIntB" +
				":in b.setIntB" +
					":in c.setIntB:setVal -3:setVal -3:out c.setIntB" +
					":in c.setIntB:setVal -3:setVal -3:out c.setIntB" +
				":out b.setIntB" +
			":out a.setIntB" +
		":out c.setIntA:out b.setIntA:out a.setIntA\n" +

		":in a.getIntA:in b.getIntA:in c.getIntA" +
			":in a.getIntB:in b.getIntB:in c.getIntB" +
				":getVal -3" +
			":out c.getIntB 0:out b.getIntB 0:out a.getIntB 0" +
		":out c.getIntA 0:out b.getIntA 0:out a.getIntA 0" +
		":ret 0\n" +

		":in a.setIntA 9:in b.setIntA 11:in c.setIntA 13" +
			":in a.setIntB" +
				":in b.setIntB" +
					":in c.setIntB:setVal 15:setVal 15:out c.setIntB" +
					":in c.setIntB:setVal 15:setVal 15:out c.setIntB" +
				":out b.setIntB" +
				":in b.setIntB" +
					":in c.setIntB:setVal 15:setVal 15:out c.setIntB" +
					":in c.setIntB:setVal 15:setVal 15:out c.setIntB" +
				":out b.setIntB" +
			":out a.setIntB" +
		":out c.setIntA:out b.setIntA:out a.setIntA\n" +

		":in a.getIntA:in b.getIntA:in c.getIntA" +
			":in a.getIntB:in b.getIntB:in c.getIntB" +
				":getVal 15" +
			":out c.getIntB 15:out b.getIntB 15:out a.getIntB 15" +
		":out c.getIntA 10:out b.getIntA 10:out a.getIntA 10" +
		":ret 10\n";

	public void test()
	{
		System.out.println("-------> ADTest 23: Multi-instance around calls");

		new DeployA().test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 23: end");
	}
}

public cclass DeployA
{
    public void test()
    {
		Object[] arr = new Object[] {
			new IntAspect().init("a"),
			new IntAspect().init("b"),
			new IntAspect().init("c"),
		};

		int ret = 0;
		ModelA a = new ModelA();

        deploy(arr[0])
        {
			deploy(arr[1])
			{
				deploy(arr[2])
				{
					a.setVal(-9);
					ADTestCase.result.append("\n");
					ret = a.getVal();
					ADTestCase.result.append(":ret " + ret + "\n");
					a.setVal(9);
					ADTestCase.result.append("\n");
					ret = a.getVal();
					ADTestCase.result.append(":ret " + ret + "\n");
				}
			}
		}
    }
}

public cclass IntAspect
{
	protected String _id;

	public IntAspect init(String id)
	{
		_id = id;
		return this;
	}

	pointcut callGetInt(ModelA model) : call(int ModelA.get*()) && target(model) && !within(IntAspect_Impl);

	pointcut callSetInt(ModelA model, int val) : call(* ModelA.set*(int)) && args(val) && target(model) && !within(IntAspect_Impl);

	int around(ModelA model) : callGetInt(model)
	{
		ADTestCase.result.append(":in " + _id + ".getIntA");
		int retval = proceed(model);
		if (retval > 10)
			retval = 10;
		ADTestCase.result.append(":out " + _id + ".getIntA " + retval);
		return retval;
	}

	int around(ModelA model) : callGetInt(model)
	{
		ADTestCase.result.append(":in " + _id + ".getIntB");
		int retval = proceed(model);
		if (retval < 0)
			retval = 0;
		ADTestCase.result.append(":out " + _id + ".getIntB " + retval);
		return retval;
	}

	void around(ModelA model, int val) : callSetInt(model, val)
	{
		ADTestCase.result.append(":in " + _id + ".setIntA " + val);
		proceed(model, val + 2);
		ADTestCase.result.append(":out " + _id + ".setIntA");
	}

	void around(ModelA model, int val) : callSetInt(model, val)
	{
		ADTestCase.result.append(":in " + _id + ".setIntB");
		proceed(model, val);
		proceed(model, val);
		ADTestCase.result.append(":out " + _id + ".setIntB");
	}
}

public cclass ModelA
{
	private int _val;

	public int getVal()
	{
		ADTestCase.result.append(":getVal " + _val);
		return _val;
	}

	public void setVal(int val)
	{
		ADTestCase.result.append(":setVal " + val);
		_val = val;
	}
}