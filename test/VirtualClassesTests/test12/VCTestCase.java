package generated.test12;

import junit.framework.*;
import java.util.*;

/**
 * Test factory methods of inner classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A.A, A.A.B.A, A.A.C.A, A.B.A.A, B.B.B.A, A.A.C.A, B.B.D.A"
											  + ", A.B.A.A, B.B.B.A, A.A.C.A";

	public void test() {

		System.out.println("-------> VCTest 12: Inner Factory Methods: start");

		final OuterA oa = new OuterA();
		final OuterB ob = new OuterB();
		oa.InnerA aa = oa.new InnerA();
		ob.InnerB bb = ob.new InnerB();

		String resAAA = aa.new DeepestA().queryA();
		String resAAB = aa.new DeepestB().queryA();
		String resAAC = aa.new DeepestC().queryA();
		String resBBA = bb.new DeepestA().queryA();
		String resBBB = bb.new DeepestB().queryA();
		String resBBC = bb.new DeepestC().queryA();
		String resBBD = bb.new DeepestD().queryA();

		final OuterA oba = ob;
		oba.InnerA aaa = (oba.InnerA)bb;
		String resAAA1 = aaa.new DeepestA().queryA();
		String resAAB1 = aaa.new DeepestB().queryA();
		String resAAC1 = aaa.new DeepestC().queryA();

		String result = resAAA + ", " + resAAB + ", " + resAAC + ", " + resBBA + ", " + resBBB + ", " + resBBC + ", " + resBBD
			+ ", " + resAAA1 + ", " + resAAB1 + ", " + resAAC1;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 12: end");
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