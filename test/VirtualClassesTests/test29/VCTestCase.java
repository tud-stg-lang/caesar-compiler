package generated.test29;

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

	public static final String expectedResult =
		"A.A.A, B.D.A, A.B.A, B.B.A, A.C.A, B.C.A; " +	// C.A
		"A.B.B, B.B.B, B.C.B; " +						// C.B
		"A.C.C; " +										// C.C
		"B.D.D, B.B.D, B.C.D; " +						// C.D
		"B.E.E, B.C.E";									// C.E


	public void test()
	{
		System.out.println("-------> VCTest 29: Test Introducing New Inheritance: start");

		final OuterB ob = new OuterB();
		ob.InnerC bc = ob.new InnerC();

		String resA = bc.queryA();
		String resB = bc.queryB();
		String resC = bc.queryC();
		String resD = bc.queryD();
		String resE = bc.queryE();

		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE;

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 29: end");
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

		public String queryB()
		{
			return "A.B.B";
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
	}
}

public cclass OuterB extends OuterA
{
    public cclass InnerA {}
    
	public cclass InnerD extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.D.A";
		}

		public String queryD()
		{
			return "B.D.D";
		}
	}

	public cclass InnerE
	{
		public String queryE()
		{
			return "B.E.E";
		}
	}

	public cclass InnerC extends InnerB & InnerE
	{
		public String queryA()
		{
			return super.queryA() + ", B.C.A";
		}

		public String queryB()
		{
			return super.queryB() + ", B.C.B";
		}

		public String queryE()
		{
			return super.queryE() + ", B.C.E";
		}

		public String queryD()
		{
			return super.queryD() + ", B.C.D";
		}
	}

	public cclass InnerB extends InnerD
	{
		public String queryA()
		{
			return super.queryA() + ", B.B.A";
		}

		public String queryB()
		{
			return super.queryB() + ", B.B.B";
		}

		public String queryD()
		{
			return super.queryD() + ", B.B.D";
		}
	}
}

