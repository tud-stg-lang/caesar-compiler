package generated;

import junit.framework.*;
import java.util.*;

/**
 * Test super calls.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase_14 extends TestCase
{

	public VCTestCase_14()
	{
		super("test");
	}

	public static final String expectedResultBB = "A.A.A; A.A.B, B.A.B, A.B.B; A.A.C, A.B.C; A.A.D, B.A.D; A.B.E; A.B.F, B.B.F; A.A.G, B.A.G, A.B.G, B.B.G";
	public static final String expectedResultBA = "A.A.A; A.A.B, B.A.B; A.A.C; A.A.D, B.A.D; B.A.E; B.A.F; A.A.G, B.A.G";

	public void test() {

		System.out.println("-------> VCTestCase_14: Test Super Calls: start");

		OuterB ob = new OuterB_Impl(null); // !!! remove parameter
		OuterB.InnerB bb = (OuterB.InnerB)ob.$newInnerB();

		String resA = bb.queryA();
		String resB = bb.queryB();
		String resC = bb.queryC();
		String resD = bb.queryD();
		String resE = bb.queryE();
		String resF = bb.queryF();
		String resG = bb.queryG();
		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG;

		System.out.println(result);
		assertEquals(result, expectedResultBB);

		OuterB.InnerA ba = (OuterB.InnerA)ob.$newInnerA();

		resA = ba.queryA();
		resB = ba.queryB();
		resC = ba.queryC();
		resD = ba.queryD();
		resE = ba.queryE();
		resF = ba.queryF();
		resG = ba.queryG();
		result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG;

		System.out.println(result);
		assertEquals(result, expectedResultBA);

        System.out.println("-------> VCTestCase_14: end");
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

		public String queryB()
		{
			return "A.A.B";
		}

		public String queryC()
		{
			return "A.A.C";
		}

		public String queryD()
		{
			return "A.A.D";
		}

		public String queryG()
		{
			return "A.A.G";
		}
	}

	public cclass InnerB extends InnerA
	{
		public String queryB()
		{
			return super.queryB() + ", A.B.B";
		}

		public String queryC()
		{
			return super.queryC() + ", A.B.C";
		}

		public String queryE()
		{
			return "A.B.E";
		}

		public String queryF()
		{
			return "A.B.F";
		}

		public String queryG()
		{
			return super.queryG() + ", A.B.G";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryB()
		{
			return super.queryB() + ", B.A.B";
		}


		public String queryD()
		{
			return super.queryD() + ", B.A.D";
		}

		public String queryE()
		{
			return "B.A.E";
		}

		public String queryF()
		{
			return "B.A.F";
		}

		public String queryG()
		{
			return super.queryG() + ", B.A.G";
		}
	}

	public cclass InnerB
	{
		public String queryF()
		{
			return super.queryF() + ", B.B.F";
		}

		public String queryG()
		{
			return super.queryG() + ", B.B.G";
		}
	}
}