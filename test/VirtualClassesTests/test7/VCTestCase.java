package generated.test7;

import junit.framework.*;
import java.util.*;

/**
 * Test polymorphism of outer classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "B.A, A.B, B.C, A.D";

	public void test() {

		System.out.println("-------> VCTest 7: Outer Class Polymorphism: start");

		OuterA ob = new OuterB_Impl(null); // !!! remove parameter

		String resA = ((InterfaceA)ob).queryA();
		String resB = ((InterfaceB)ob).queryB();
		String resC = ob.queryC();
		String resD = ob.queryD();
		String result = resA + ", " + resB + ", " + resC + ", " + resD;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 7: end");
	}
}

public interface InterfaceA
{
	public String queryA();
}

public interface InterfaceB
{
	public String queryB();
}

public cclass OuterA implements InterfaceA
{
	public String queryA()
	{
		return "A.A";
	}

	public String queryB()
	{
		return "A.B";
	}

	public String queryC()
	{
		return "A.C";
	}

	public String queryD()
	{
		return "A.D";
	}

	// not used
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}
}

public cclass OuterB extends OuterA implements InterfaceB
{
	public String queryA()
	{
		return "B.A";
	}

	public String queryC()
	{
		return "B.C";
	}

	// not used
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}
}