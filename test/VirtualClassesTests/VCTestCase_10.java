package generated;

import junit.framework.*;
import java.util.*;

/**
 * Test factory methods of outer classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase_10 extends TestCase
{

	public VCTestCase_10()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, A.B.A, B.A.A, A.B.A, B.C.A, B.A.A, A.B.A";

	public void test() {

		System.out.println("-------> VCTestCase_10: Outer Factory Methods: start");

		OuterA oa = new OuterA_Impl(null); // !!! remove parameter
		OuterB ob = new OuterB_Impl(null); // !!! remove parameter

		String resAA = oa.$newInnerA().queryA();
		String resAB = oa.$newInnerB().queryA();
		String resBA = ob.$newInnerA().queryA();
		String resBB = ob.$newInnerB().queryA();
		String resBC = ob.$newInnerC().queryA();

		oa = ob;
		String resBA1 = oa.$newInnerA().queryA();
		String resBB1 = oa.$newInnerB().queryA();

		String result = resAA + ", " + resAB + ", " + resBA + ", " + resBB + ", " + resBC + ", " + resBA1 + ", " + resBB1;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTestCase_10: end");
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

	public cclass InnerB
	{
		public String queryA()
		{
			return "A.B.A";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "B.A.A";
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return "B.C.A";
		}
	}
}