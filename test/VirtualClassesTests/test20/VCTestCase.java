package generated.test20;

import junit.framework.*;
import java.util.*;

/**
 * Test joins linearization of state.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.a; " +						// A
												"A.a, B.a; " +					// B < A
												"A.a, C.a; " +					// C < A
												"A.a, B.a, D.a; " +				// D < B
												"A.a, C.a, B.a; " +				// E < B & C
												"A.a, C.a, F.a; " + 			// F < A & C
												"A.a, C.a, B.a, D.a; " +		// G < E & D
												"A.a, C.a, F.a, B.a, H.a; " +	// H < E & F
												"A.a, C.a, F.a, B.a, D.a, H.a";	// I < G & H

	public void test() {

		System.out.println("-------> VCTest 20: Joins Linearization: start");

		String resA = (new OuterA_Impl(null)).$newInnerA().queryA();
		String resB = (new OuterB_Impl(null)).$newInnerA().queryA();
		String resC = (new OuterC_Impl(null)).$newInnerA().queryA();
		String resD = (new OuterD_Impl(null)).$newInnerA().queryA();
		String resE = (new OuterE_Impl(null)).$newInnerA().queryA();
		String resF = (new OuterF_Impl(null)).$newInnerA().queryA();
		String resG = (new OuterG_Impl(null)).$newInnerA().queryA();
		String resH = (new OuterH_Impl(null)).$newInnerA().queryA();
		String resI = (new OuterI_Impl(null)).$newInnerA().queryA();
		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG
					         + "; " + resH + "; " + resI;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 20: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		String _a = "A.a";

		public String queryA()
		{
			return _a;
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		String _a = "B.a";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerA
	{
		String _a = "C.a";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}
	}
}

public cclass OuterD extends OuterB
{
	public cclass InnerA
	{
		String _a = "D.a";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}
	}
}

public cclass OuterE extends OuterB & OuterC
{

}

public cclass OuterF extends OuterA & OuterC
{
	public cclass InnerA
	{
		String _a = "F.a";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}
	}
}

public cclass OuterG extends OuterE & OuterD
{

}

public cclass OuterH extends OuterE & OuterF
{
	public cclass InnerA
	{
		String _a = "H.a";

		public String queryA()
		{
			return super.queryA() + ", " + _a;
		}
	}
}

public cclass OuterI extends OuterG & OuterH
{

}



