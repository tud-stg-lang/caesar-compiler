package generated.test28;

import junit.framework.*;
import java.util.*;

/**
 * Test merging class with multiple parents.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult =	"A.A.A, A.C.A; B.B.B, B.C.B; A.C.C; A.A.D; B.B.E; B.C.F; A.C.G";


	public void test()
	{

		System.out.println("-------> VCTest 28: Test Merging Class With Multiple Parents: start");

		OuterC.InnerC cc = new OuterC().new InnerC();

		String resA = cc.queryA();
		String resB = cc.queryB();
		String resC = cc.queryC();
		String resD = cc.queryD();
		String resE = cc.queryE();
		String resF = cc.queryF();
		String resG = cc.queryG();

		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG;

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

		public String queryD()
		{
			return "A.A.D";
		}

		public String queryF()
		{
			return "A.A.F";
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C.A";
		}

		public String queryC()
		{
			return "A.C.C";
		}

		public String queryG()
		{
			return "A.C.G";
		}
	}
}

public cclass OuterB extends AllTypes
{
	public cclass InnerB
	{
		public String queryB()
		{
			return "B.B.B";
		}

		public String queryE()
		{
			return "B.B.E";
		}

		public String queryG()
		{
			return "B.B.G";
		}
	}

	public cclass InnerC extends InnerB
	{
		public String queryB()
		{
			return super.queryB() + ", B.C.B";
		}

		public String queryC()
		{
			return "B.C.C";
		}

		public String queryF()
		{
			return "B.C.F";
		}
	}
}

public cclass OuterC extends OuterA & OuterB
{
}

