package generated.test32;

import junit.framework.*;
import java.util.*;

/**
 * Test introducing new inheritance.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, A.B.A, A.C.A";


	public void test()
	{
		System.out.println("-------> VCTest 32: Test Introducing New Inheritance: start");

		OuterB.InnerC bc = (OuterB.InnerC)new OuterB_Impl(null).$newInnerC();

		String result = bc.queryA();

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 32: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}

	public cclass InnerB extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.B.A";
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C.A";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerC extends InnerB
	{ }
}

