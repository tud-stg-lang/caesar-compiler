package generated.test6;

import junit.framework.*;
import java.util.*;

/**
 * Test type inheritance of outer classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A, B.B, A.C, B.D";

	public void test() {

		System.out.println("-------> VCTest 6: Outer Class Type Inheritance: start");

		OuterB ob = new OuterB();

		String resA = ((InterfaceA)ob).queryA();
		String resB = ((InterfaceB)ob).queryB();
		String resC = ((OuterA)ob).queryC();
		String resD = ob.queryD();
		String result = resA + ", " + resB + ", " + resC + ", " + resD;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 6: end");
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

	public String queryC()
	{
		return "A.C";
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
	public String queryB()
	{
		return "B.B";
	}

	public String queryD()
	{
		return "B.D";
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