package generated.test19;

import junit.framework.*;
import java.util.*;

/**
 * Test multiple inheritance of state.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.a, A.a, A.a, A.a; " +
												"A.b, A.b, B.b, B.b; " +
												"A.c, C.c, C.c, C.c; " +
	                                            "A.d, C.d, B.d, B.d; " +
	                                            "B.e, B.e; " +
	                                            "B.f, D.f; " +
	                                            "A.g, C.g, B.g, D.g";

	public void test() {

		System.out.println("-------> VCTest 19: Multiple Inheritance of State: start");

		OuterD d = new OuterD();

		String resA = d.queryA();
		String resB = d.queryB();
		String resC = d.queryC();
		String resD = d.queryD();
		String resE = d.queryE();
		String resF = d.queryF();
		String resG = d.queryG();
		String result = resA + "; " + resB + "; " + resC + "; " + resD + "; " + resE + "; " + resF + "; " + resG;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 19: end");
	}
}

public cclass OuterA
{
    protected String _a = "A.a";

    protected String _b = "A.b";

    protected String _c = "A.c";

    protected String _d = "A.d";

    protected String _g = "A.g";

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

public cclass OuterB extends OuterA
{
    protected String _b = "B.b";

    protected String _d = "B.d";

    protected String _e = "B.e";

    protected String _f = "B.f";

    protected String _g = "B.g";

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

public cclass OuterC extends OuterA
{
    protected String _c = "C.c";

    protected String _d = "C.d";

    protected String _e = "C.e";

    protected String _f = "C.f";

    protected String _g = "C.g";

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

public cclass OuterD extends OuterB & OuterC
{
    protected String _f = "D.f";

    protected String _g = "D.g";

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

