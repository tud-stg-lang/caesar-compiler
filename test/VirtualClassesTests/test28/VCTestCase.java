package generated.test28;

import junit.framework.*;
import java.util.*;

/**
 * Test super calls of merged classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult =	"A.A.A, C.C.A; B.B.B, C.C.B";


	public void test()
	{

		System.out.println("-------> VCTest 28: Test Super Calls of Merged Classes: start");

		OuterC.InnerC ca = (OuterC.InnerC)new OuterC_Impl(null).$newInnerC();

		String resA = ca.queryA();
		String resB = ca.queryB();

		String result = resA + "; " + resB;

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 28: end");
	}
}

public cclass AllTypes
{
	public cclass InnerA
	{ }

	public cclass InnerB
	{ }

	public cclass InnerC
	{ }
}

public cclass OuterA extends AllTypes
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}

	public cclass InnerC extends InnerA
	{ }
}

public cclass OuterB extends AllTypes
{
	public cclass InnerB
	{
		public String queryB()
		{
			return "B.B.B";
		}
	}

	public cclass InnerC extends InnerB
	{ }
}

public cclass OuterC extends OuterA & OuterB
{
	public cclass InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", C.C.A";
		}

		public String queryB()
		{
			return super.queryB() + ", C.C.B";
		}
	}
}

