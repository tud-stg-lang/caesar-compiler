package generated.test125;

import junit.framework.TestCase;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Test passing join point reflection info
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
		":a:0:0.0:0.0\n" +
		":b:0:0.0:0.0\n" +
		":a:2:3.0:4.0:int\n" +
		":b:2:3.0:4.0:int\n" +
		":a:2:3.0:4.0:float\n" +
		":b:2:3.0:4.0:float\n" +
		":a:2:3.0:4.0:double\n" +
		":b:2:3.0:4.0:double\n" +
		"4:5.0:6.0";

	public void test()
	{
		System.out.println("-------> ADTest 25: Passing join point reflection");

		new DeployA_Impl(null).test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 25: end");
	}
}

cclass DeployA
{
    public void test()
    {
		ModelA a = new ModelA();

        deploy(new AspectA().init("a"))
        {
			deploy(new AspectA().init("b"))
			{
				a.setVal(0, 1.0f, 2.0);
				ADTestCase.result.append("" + a.getIntVal() + ":" + a.getFloatVal() + ":" + a.getDoubleVal());
			}
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

	Object around() : execution(* ModelA.get*(..))
	{
		ADTestCase.result.append(":" + _id + ":" + thisJoinPoint.getThis().toString());
		MethodSignature signature = (MethodSignature)thisJoinPointStaticPart.getSignature();
		Class type = signature.getReturnType();
		ADTestCase.result.append(":" + type.toString() + "\n");

		if (type == Float.TYPE) {
			return new Float(((Float)proceed()).floatValue() + 1.0f);
		}
		else if (type == Integer.TYPE) {
			return new Integer(((Integer)proceed()).intValue() + 1);
		}
		else if (type == Double.TYPE) {
			return new Double(((Double)proceed()).doubleValue() + 1.0);
		}
		else {
			return proceed();
		}
	}

	pointcut execSetVal(int i1, float f1, double d1):
		execution(void ModelA.setVal(..)) && args(i1, f1, d1);


	void around(int i1, float f1, double d1) :
		execSetVal(i1, f1, d1)
	{
		ADTestCase.result.append(":" + _id + ":" + thisJoinPoint.getThis().toString() + "\n");
		proceed(i1 + 1, f1 + 1.0f, d1 + 1.0);
	}
}

cclass ModelA
{
	int _i1;

	float _f1;

	double _d1;

	public void setVal(int i1, float f1, double d1)
	{
		_i1 = i1;
		_f1 = f1;
		_d1 = d1;
	}

	public int getIntVal()
	{
		return _i1;
	}

	public float getFloatVal()
	{
		return _f1;
	}

	public double getDoubleVal()
	{
		return _d1;
	}


	public String toString()
	{
		return "" + _i1 + ":" + _f1 + ":" + _d1;
	}
}