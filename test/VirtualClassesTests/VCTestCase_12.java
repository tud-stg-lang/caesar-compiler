package generated;

import junit.framework.*;
import java.util.*;

/**
 * Test factory methods of inner classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase_12 extends TestCase
{

	public VCTestCase_12()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A.A, A.A.B.A, A.A.C.A, A.B.A.A, B.B.B.A, A.A.C.A, B.B.D.A"
											  + ", A.B.A.A, B.B.B.A, A.A.C.A";

	public void test() {

		System.out.println("-------> VCTestCase_12: Inner Factory Methods: start");

		OuterA.InnerA aa = (OuterA.InnerA)(new OuterA_Impl(null)).$newInnerA(); // !!! remove parameter
		OuterB.InnerB bb = (OuterB.InnerB)(new OuterB_Impl(null)).$newInnerB(); // !!! remove parameter

		String resAAA = aa.$newDeepestA().queryA();
		String resAAB = aa.$newDeepestB().queryA();
		String resAAC = aa.$newDeepestC().queryA();
		String resBBA = bb.$newDeepestA().queryA();
		String resBBB = bb.$newDeepestB().queryA();
		String resBBC = bb.$newDeepestC().queryA();
		String resBBD = bb.$newDeepestD().queryA();

		aa = bb;
		String resAAA1 = aa.$newDeepestA().queryA();
		String resAAB1 = aa.$newDeepestB().queryA();
		String resAAC1 = aa.$newDeepestC().queryA();

		String result = resAAA + ", " + resAAB + ", " + resAAC + ", " + resBBA + ", " + resBBB + ", " + resBBC + ", " + resBBD
			+ ", " + resAAA1 + ", " + resAAB1 + ", " + resAAC1;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTestCase_12: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return "A.A.A.A";
			}
		}

		public cclass DeepestB
		{
			public String queryA()
			{
				return "A.A.B.A";
			}
		}

		public cclass DeepestC
		{
			public String queryA()
			{
				return "A.A.C.A";
			}
		}
	}

	public cclass InnerB extends InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return "A.B.A.A";
			}
		}

		public cclass DeepestD
		{
			public String queryA()
			{
				return "A.B.D.A";
			}
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerB
	{
		public cclass DeepestB
		{
			public String queryA()
			{
				return "B.B.B.A";
			}
		}

		public cclass DeepestD
		{
			public String queryA()
			{
				return "B.B.D.A";
			}
		}
	}
}