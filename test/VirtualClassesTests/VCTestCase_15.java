package generated;

import junit.framework.*;
import java.util.*;

/**
 * Test long inheritance sequence.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase_15 extends TestCase
{

	public VCTestCase_15()
	{
		super("test");
	}

	public static final String expectedResultCD = "A.A.A, B.A.A, A.B.A, B.B.A, C.B.A, A.C.A, C.C.A, B.D.A";
	public static final String expectedResultCF = "A.A.A, B.A.A, C.A.E, C.A.F";

	public void test() {

		System.out.println("-------> VCTestCase_15: Long Inheritance Sequence: start");

		OuterC oc = new OuterC_Impl(null); // !!! remove parameter
		OuterC.InnerD cd = (OuterC.InnerD)oc.$newInnerD();

		String result = cd.queryA();

		System.out.println(result);
//		assertEquals(result, expectedResultCD);

		OuterC.InnerF cf = (OuterC.InnerF)oc.$newInnerF();

		result = cf.queryA();

		System.out.println(result);
//		assertEquals(result, expectedResultCF);

        System.out.println("-------> VCTestCase_15: end");
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
	}

	public cclass InnerC extends InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", A.C.A";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.A.A";
		}
	}

	public cclass InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", B.B.A";
		}
	}

	public cclass InnerD extends InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", B.D.A";
		}
	}
}

public cclass OuterC extends OuterB
{
	public cclass InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", C.B.A";
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", C.C.A";
		}
	}

	public cclass InnerE extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", C.E.A";
		}
	}

	public cclass InnerF extends InnerE
	{
		public String queryA()
		{
			return super.queryA() + ", C.F.A";
		}
	}
}