package generated.test17;

import junit.framework.*;
import java.util.*;

/**
 * Test deep inheritance relationships.
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

		System.out.println("-------> VCTest 17: Deep Inheritance: start");

		OuterB.InnerB b = (OuterB.InnerB)(new OuterB_Impl(null)).$newInnerB(); // !!! remove parameter

		String resA = b.$newDeepestA().queryA();
		String resB = b.$newDeepestB().queryA();
		String resC = b.$newDeepestC().queryA();
		String resD = b.$newDeepestD().queryA();
		String resE = b.$newDeepestE().queryA();
		String resF = b.$newDeepestF().queryA();
		String resG = b.$newDeepestG().queryA();
		String resH = b.$newDeepestH().queryA();
		String resI = b.$newDeepestI().queryA();
		String resJ = b.$newDeepestJ().queryA();
		String resK = b.$newDeepestK().queryA();

		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG
		              + "; " + resH + "; " + resI + "; " + resJ + "; " + resK;

		System.out.println(result);
		//assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 17: end");
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
				return "A.A.A";
			}
		}

		public cclass DeepestB extends DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.A.B";
			}
		}

		public cclass DeepestC extends DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.A.C";
			}
		}

		public cclass DeepestJ
		{
			public String queryA()
			{
				return "A.A.J";
			}
		}
	}

	public cclass InnerB extends InnerA
	{
		public cclass DeepestD extends InnerA.DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.D";
			}
		}

		public cclass DeepestE extends InnerA.DeepestB
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.E";
			}
		}

		public cclass DeepestF extends InnerA.DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.F";
			}
		}

		public cclass DeepestG extends InnerA.DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.G";
			}
		}

		public cclass DeepestH
		{
			public String queryA()
			{
				return "A.B.H";
			}
		}

		public cclass DeepestI extends InnerA.DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.I";
			}
		}

		public cclass DeepestK extends InnerA.DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.K";
			}
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public cclass DeepestD extends OuterA.InnerA.DeepestB
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.D";
			}
		}

		public cclass DeepestE extends OuterA.InnerA.DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.E";
			}
		}

		public cclass DeepestF extends OuterA.InnerA.DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.F";
			}
		}

		public cclass DeepestG
		{
			public String queryA()
			{
				return "B.A.G";
			}
		}

		public cclass DeepestH extends OuterA.InnerA.DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.H";
			}
		}

		public cclass DeepestI extends OuterA.InnerA.DeepestB
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.I";
			}
		}

		public cclass DeepestK extends OuterA.InnerA.DeepestJ
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.K";
			}
		}
	}
}