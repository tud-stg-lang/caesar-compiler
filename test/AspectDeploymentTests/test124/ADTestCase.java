package generated.test124;

import junit.framework.TestCase;

/**
 * Test wrapping primitive types in around
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

	public String expectedResult = "4:5.0:6.0:7:8:e:false:9";

	public void test()
	{
		System.out.println("-------> ADTest 24: Primitive types in around");

		new DeployA_Impl(null).test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 24: end");
	}
}

cclass DeployA
{
    public void test()
    {
		ModelA a = new ModelA();

        deploy(new AspectA())
        {
			deploy(new AspectA())
			{
				a.setVal(0, 1.0f, 2.0, (short)3, (byte)4, 'a', false, 5l);
				ADTestCase.result.append("" + a.getIntVal() + ":" + a.getFloatVal() + ":" + a.getDoubleVal() + ":"
									   + a.getShortVal() + ":" + a.getByteVal() + ":" + a.getCharVal() + ":"
									   + a.getBoolVal() + ":" + a.getLongVal());
			}
		}
    }
}

cclass AspectA
{
	int around() : execution(int ModelA.*(..))
	{
		return proceed() + 1;
	}

	float around() : execution(float ModelA.*(..))
	{
		return proceed() + 1.0f;
	}

	double around() : execution(double ModelA.*(..))
	{
		return proceed() + 1.0;
	}

	short around() : execution(short ModelA.*(..))
	{
		return (short)(proceed() + 1);
	}

	byte around() : execution(byte ModelA.*(..))
	{
		return (byte)(proceed() + 1);
	}

	char around() : execution(char ModelA.*(..))
	{
		return (char)(proceed() + 1);
	}

	boolean around() : execution(boolean ModelA.*(..))
	{
		return !proceed();
	}

	long around() : execution(long ModelA.*(..))
	{
		return proceed() + 1l;
	}

	pointcut execSetVal(int i1, float f1, double d1, short s1, byte b1, char c1, boolean bool1, long l1):
		execution(void ModelA.setVal(..)) && args(i1, f1, d1, s1, b1, c1, bool1, l1);


	void around(int i1, float f1, double d1, short s1, byte b1, char c1, boolean bool1, long l1) :
		execSetVal(i1, f1, d1, s1, b1, c1, bool1, l1)
	{
		proceed(i1 + 1, f1 + 1.0f, d1 + 1.0, (short)(s1 + 1), (byte)(b1 + 1), (char)(c1 + 1), !bool1, l1 + 1l);
	}
}

cclass ModelA
{
	int _i1;


	float _f1;


	double _d1;


	short _s1;


	byte _b1;


	char _c1;


	boolean _bool1;

	long _l1;

	public void setVal(int i1, float f1, double d1, short s1, byte b1, char c1, boolean bool1, long l1)
	{
		_i1 = i1;
		_f1 = f1;
		_d1 = d1;
		_s1 = s1;
		_b1 = b1;
		_c1 = c1;
		_bool1 = bool1;
		_l1 = l1;
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

	public short getShortVal()
	{
		return _s1;
	}

	public byte getByteVal()
	{
		return _b1;
	}

	public char getCharVal()
	{
		return _c1;
	}

	public boolean getBoolVal()
	{
		return _bool1;
	}

	public long getLongVal()
	{
		return _l1;
	}
}