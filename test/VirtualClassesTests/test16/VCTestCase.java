package generated.test16;

import junit.framework.*;
import java.util.*;

/**
 * Test state inheritance.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResultBB = "A.A.a, A.A.a, A.A.a, A.A.a; " +
	                                              "A.A.b, B.A.b, A.B.b, A.B.b; " +
	                                              "A.A.c, A.A.c, A.B.c, A.B.c; " +
	                                              "A.A.d, B.A.d, B.A.d; " +
	                                              "A.B.e, A.B.e; " +
	                                              "A.B.f, B.B.f; " +
	                                              "A.A.g, B.A.g, A.B.g, B.B.g";
	public static final String expectedResultBA = "A.A.a; " +
	                                              "A.A.b, B.A.b; " +
	                                              "A.A.c; " +
	                                              "A.A.d, B.A.d; " +
	                                              "B.A.e; " +
	                                              "B.A.f; " +
	                                              "A.A.g, B.A.g";

	public void test() {

		System.out.println("-------> VCTest 16: Test State Inheritance: start");

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
		//assertEquals(result, expectedResultBB);

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
		//assertEquals(result, expectedResultBA);

		System.out.println("-------> VCTest 16: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		protected String _a = "A.A.a";

		protected String _b = "A.A.b";

		protected String _c = "A.A.c";

		protected String _d = "A.A.d";

		protected String _g = "A.A.g";

		public String queryA()
		{
			return _a;
		}

		public String queryB()
		{
			return _b;
		}

		public String queryC()
		{
			return _c;
		}

		public String queryD()
		{
			return _d;
		}

		public String queryG()
		{
			return _g;
		}
	}

	public cclass InnerB extends InnerA
	{
		protected String _b = "A.B.b";

		protected String _c = "A.B.c";

		protected String _e = "A.B.e";

		protected String _f = "A.B.f";

		protected String _g = "A.B.g";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}

		public String queryB()
		{
			return super.queryB() + ", " + _b;
		}

		public String queryC()
		{
			return super.queryC() + ", " + _c;
		}

		public String queryD()
		{
			return super.queryD() + ", " + _d;
		}

		public String queryE()
		{
			return _e;
		}

		public String queryF()
		{
			return _f;
		}

		public String queryG()
		{
			return super.queryG() + ", " + _g;
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		protected String _b = "B.A.b";

		protected String _d = "B.A.d";

		protected String _e = "B.A.e";

		protected String _f = "B.A.f";

		protected String _g = "B.A.g";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}

		public String queryB()
		{
			return super.queryB() + ", " + _b;
		}

		public String queryC()
		{
			return super.queryC() + ", " + _c;
		}

		public String queryD()
		{
			return super.queryD() + ", " + _d;
		}

		public String queryE()
		{
			return _e;
		}

		public String queryF()
		{
			return _f;
		}

		public String queryG()
		{
			return super.queryG() + ", " + _g;
		}
	}

	public cclass InnerB
	{
		protected String _f = "B.B.f";

		protected String _g = "B.B.g";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}

		public String queryB()
		{
			return super.queryB() + ", " + _b;
		}

		public String queryC()
		{
			return super.queryC() + ", " + _c;
		}

		// _d is ambiguous
		public String queryD()
		{
			return super.queryD() + ", " + _d;
		}

		public String queryE()
		{
			return super.queryE() + ", " + _e;
		}

		public String queryF()
		{
			return super.queryF() + ", " + _f;
		}

		public String queryG()
		{
			return super.queryG() + ", " + _g;
		}
	}
}