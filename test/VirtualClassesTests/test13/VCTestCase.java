package generated.test13;

import junit.framework.*;
import java.util.*;

/**
 * Test inherited methods.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, A.B.B, A.B.C, B.A.D, A.B.E, B.B.F, B.B.G";

	public void test() {

		System.out.println("-------> VCTest 13: Inherited Methods: start");

		OuterB ob = new OuterB();
		OuterB.InnerB bb = (OuterB.InnerB)ob.new InnerB();

		String resA = bb.queryA();
		String resB = bb.queryB();
		String resC = bb.queryC();
		String resD = bb.queryD();
		String resE = bb.queryE();
		String resF = bb.queryF();
		String resG = bb.queryG();
		String result = resA + ", " + resB + ", " + resC + ", " + resD + ", " + resE + ", " + resF + ", " + resG;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 13: end");
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
			return "A.B.B";
		}

		public String queryC()
		{
			return "A.B.C";
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
			return "A.B.G";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryB()
		{
			return "B.A.B";
		}


		public String queryD()
		{
			return "B.A.D";
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
			return "B.A.G";
		}
	}

	public cclass InnerB
	{
		public String queryF()
		{
			return "B.B.F";
		}

		public String queryG()
		{
			return "B.B.G";
		}
	}
}