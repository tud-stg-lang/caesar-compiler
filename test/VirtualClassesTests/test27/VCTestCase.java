package generated.test27;

import junit.framework.*;
import java.util.*;

/**
 * Test multiple outer joins.
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
		"A.A, D.A, C.A, F.A, E.A, G.A; " +
		"A.A, D.A, C.A, F.A, E.A, G.A, B.A, H.A; " +
		"A.B, C.B, E.B, G.B; " +
		"A.B, C.B, E.B, G.B";


	public void test() {

		System.out.println("-------> VCTest 27: Test Multiple Outer Joins: start");

		OuterG og = new OuterG_Impl(null);
		OuterH oh = new OuterH_Impl(null);

		String resGA = og.queryA();
		String resHA = oh.queryA();
		String resGB = og.queryB();
		String resHB = oh.queryB();

		String result = resGA + "; " + resHA + "; " + resGB + "; " + resHB;

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 27: end");
	}
}

public cclass OuterA
{
	public String queryA()
	{
		return "A.A";
	}

	public String queryB()
	{
		return "A.B";
	}
}

public cclass OuterB extends OuterA
{
	public String queryA()
	{
		return super.queryA() + ", B.A";
	}
}

public cclass OuterC extends OuterA
{
	public String queryA()
	{
		return super.queryA() + ", C.A";
	}

	public String queryB()
	{
		return super.queryB() + ", C.B";
	}
}

public cclass OuterD extends OuterA
{
	public String queryA()
	{
		return super.queryA() + ", D.A";
	}
}

public cclass OuterE extends OuterC
{
	public String queryA()
	{
		return super.queryA() + ", E.A";
	}

	public String queryB()
	{
		return super.queryB() + ", E.B";
	}
}

public cclass OuterF extends OuterC
{
	public String queryA()
	{
		return super.queryA() + ", F.A";
	}
}

public cclass OuterG extends OuterE & OuterD & OuterF
{
	public String queryA()
	{
		return super.queryA() + ", G.A";
	}

	public String queryB()
	{
		return super.queryB() + ", G.B";
	}
}

public cclass OuterH extends OuterA & OuterB & OuterC & OuterD & OuterE & OuterF & OuterG
{
	public String queryA()
	{
		return super.queryA() + ", H.A";
	}
}
