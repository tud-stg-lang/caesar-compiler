package generated.test23;

import junit.framework.*;
import java.util.*;

/**
 * Test join inner classes.
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
		"A.A, C.A, B.A; " +								  										// A
		"A.A, C.A, B.A, A.B, C.B; "	+					  										// B < A
		"A.A, C.A, B.A, A.C, C.C, B.C; "	+ 					  								// C < A
		"A.A, C.A, B.A, A.C, C.C, B.C, A.B, C.B, A.D, C.D, B.D; " +			  					// D < B & C
		"A.A, C.A, B.A, A.E; " +						  										// E < A
		"A.A, C.A, B.A, A.E, A.C, C.C, B.C, A.B, C.B, A.D, C.D, B.D, A.F, B.F; " +				// F < D & A
		"A.A, C.A, B.A, A.E, A.C, C.C, B.C, A.B, C.B, A.D, C.D, B.D, A.F, B.F, B.G; " + 		// G < C & F
		"A.A, C.A, B.A, A.C, C.C, B.C, A.B, C.B, C.H; " +	  									// H < B & C
		"A.A, C.A, B.A, A.E, A.C, C.C, B.C, A.B, C.B, A.D, C.D, B.D, A.F, B.F, D.I";			// I < F & D

	public void test() {

		System.out.println("-------> VCTest 23: Test joined polymorphism: start");

		OuterD od = new OuterD();

		String resA = od.new InnerA().queryA();
		String resB = od.new InnerB().queryA();
		String resC = od.new InnerC().queryA();
		String resD = od.new InnerD().queryA();
		String resE = od.new InnerE().queryA();
		String resF = od.new InnerF().queryA();
		String resG = od.new InnerG().queryA();
		String resH = od.new InnerH().queryA();
		String resI = od.new InnerI().queryA();

		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG +
						"; " + resH + "; " + resI;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 23: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A";
		}
	}

	public cclass InnerB extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.B";
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C";
		}
	}

	public cclass InnerD extends InnerB & InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", A.D";
		}
	}

	public cclass InnerE extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.E";
		}
	}

	public cclass InnerF extends InnerD & InnerE
	{
		public String queryA()
		{
			return super.queryA() + ", A.F";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.A";
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", B.C";
		}
	}

	public cclass InnerD
	{
		public String queryA()
		{
			return super.queryA() + ", B.D";
		}
	}

	public cclass InnerF {
		public String queryA()
		{
			return super.queryA() + ", B.F";
		}
	}

	public cclass InnerG extends InnerC & InnerF
	{
		public String queryA()
		{
			return super.queryA() + ", B.G";
		}
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", C.A";
		}
	}

	public cclass InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", C.B";
		}
	}

	public cclass InnerC {
		public String queryA()
		{
			return super.queryA() + ", C.C";
		}
	}

	public cclass InnerD
	{
		public String queryA()
		{
			return super.queryA() + ", C.D";
		}
	}

	public cclass InnerH extends InnerB & InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", C.H";
		}
	}
}

public cclass OuterD extends OuterB & OuterC
{
    public cclass InnerI extends InnerF & InnerD
	{
		public String queryA()
		{
			return super.queryA() + ", D.I";
		}
	}
}