package generated.test22;

import junit.framework.*;
import java.util.*;

/**
 * Test polymorphism of joined classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "D.M, D.N, A.O, D.Q, D.A, D.B, D.C";

	public void test() {

		System.out.println("-------> VCTest 22: Test joined polymorphism: start");

		OuterD od = new OuterD_Impl(null);

		String resM = ((InterfaceM)od).queryM();
		String resN = ((InterfaceN)od).queryN();
		String resO = ((InterfaceO)od).queryO();
		String resQ = ((InterfaceQ)od).queryQ();
		String resA = ((OuterA)od).queryA();
		String resB = ((OuterB)od).queryB();
		String resC = ((OuterC)od).queryC();

		String result = resM + ", " + resN + ", " + resO + ", " + resQ + ", " + resA + ", " + resB + ", " + resC;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 22: end");
	}
}

interface InterfaceM
{
	public String queryM();
}

interface InterfaceN
{
	public String queryN();
}

interface InterfaceO
{
	public String queryO();
}

interface InterfaceQ
{
	public String queryQ();
}

public cclass OuterA implements InterfaceM
{
	public String queryA()

	{
		return "A.A";
	}


	public String queryM()
	{
		return "A.M";
	}

	public String queryO()
	{
		return "A.O";
	}

	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}
}

public cclass OuterB extends OuterA implements InterfaceN
{
	public String queryB()

	{
		return "B.B";
	}


	public String queryN()
	{
		return "B.N";
	}

	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.A.A";
		}
	}
}

public cclass OuterC extends OuterA implements InterfaceO
{
	public String queryC()
	{
		return "C.C";
	}

	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", C.A.A";
		}
	}
}

public cclass OuterD extends OuterB & OuterC implements InterfaceQ
{
	public String queryA()
	{
		return "D.A";
	}

	public String queryB()
	{
		return "D.B";
	}

	public String queryC()
	{
		return "D.C";
	}

	public String queryM()
	{
		return "D.M";
	}

	public String queryN()
	{
		return "D.N";
	}

	public String queryQ()
	{
		return "D.Q";
	}
}