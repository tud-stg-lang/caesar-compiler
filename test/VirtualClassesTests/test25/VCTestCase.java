package generated.test25;

import junit.framework.*;
import java.util.*;

/**
 * Test default constructors.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A; " + 					// A
												"A.A; " +					// A.A
												"A, B; " +					// B
												"A.A, B.A, A.B, B.B; " +	// B.B
												"A.A, A.C, A.B, A.D"; 		// A.D

	public void test() {

		System.out.println("-------> VCTest 25: Test Default Constructors: start");

		OuterA oa = new OuterA();
		OuterA.InnerA ia = oa.new InnerA();

		OuterB ob = new OuterB();
		OuterB.InnerB ib = (OuterB.InnerB)ob.new InnerB();
		OuterA.InnerD id = (OuterA.InnerD)oa.new InnerD();

		String result = oa.queryA() + "; " + ia.queryA() + "; " + ob.queryA() + "; " + ib.queryA() + "; " + id.queryA();

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 25: end");
	}
}

public cclass OuterA
{
	String _a;

	public OuterA()
	{
		_a = "A";
	}

	public String queryA()
	{
		return _a;
	}

	public cclass InnerA
	{
		String _aa;

		public InnerA()
		{
			_aa = "A.A";
		}

		public String queryA()
		{
			return _aa;
		}
	}

	public cclass InnerB extends InnerA
	{
		String _ab;


		public InnerB()
		{
			_ab = "A.B";
		}


		public String queryA()
		{
			return super.queryA() + ", " + _ab;
		}
	}

	public cclass InnerC extends InnerA
	{
		String _ac;

		public InnerC()
		{
			_ac = "A.C";
		}

		public String queryA()
		{
			return super.queryA() + ", " + _ac;
		}
	}

	public cclass InnerD extends InnerB & InnerC
	{
		String _ad;

		public InnerD()
		{
			_ad = "A.D";
		}

		public String queryA()
		{
			return super.queryA() + ", " + _ad;
		}
	}
}

public cclass OuterB extends OuterA
{
	String _b;

	public OuterB()
	{
		_b = "B";
	}

	public String queryA()
	{
		return super.queryA() + ", " + _b;
	}

	public cclass InnerA
	{
		String _ba;

		public InnerA()
		{
			_ba = "B.A";
		}

		public String queryA()
		{
			return super.queryA() + ", " + _ba;
		}
	}

	public cclass InnerB
	{
		String _bb;

		public InnerB()
		{
			_bb = "B.B";
		}

		public String queryA()
		{
			return super.queryA() + ", " + _bb;
		}
	}
}