package generated.test9;

import junit.framework.*;
import java.util.*;

/**
 * Test polymorphism of inner classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "B.A.A, A.B.B, A.A.C, B.A.D, B.A.E, B.A.F";

	public void test() {

		System.out.println("-------> VCTest 9: Inner Class Polymorphism: start");

		OuterB ob = new OuterB_Impl(null); // !!! remove parameter
		OuterA.InnerA ba = (OuterB.InnerA)ob.$newInnerA();

		String resA = ((InterfaceA)ba).queryA();
		String resB = ((InterfaceB)ba).queryB();
		String resC = ba.queryC();
		String resD = ba.queryD();
		String resE = ((InterfaceE)ba).queryE();
		String resF = ((OuterA.InnerB)ba).queryF();
		String result = resA + ", " + resB + ", " + resC + ", " + resD + ", "
							 + resE + ", " + resF;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 9: end");
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

public interface InterfaceE
{
	public String queryE();
}

public cclass OuterA
{
	public cclass InnerB implements InterfaceE
	{
		public String queryB()
		{
			return "A.B.B";
		}

		public String queryE()
		{
			return "A.B.E";
		}

		public String queryF()
		{
			return "A.B.F";
		}
	}

	public cclass InnerA extends InnerB implements InterfaceA
	{
		public String queryA()
		{
			return "A.A.A";
		}

		public String queryC()
		{
			return "A.A.C";
		}

		public String queryD()
		{
			return "A.A.D";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA implements InterfaceB
	{
		public String queryA()
		{
			return "B.A.A";
		}

		public String queryD()
		{
			return "B.A.D";
		}

		public String queryE()
		{
			return "B.A.E";
		}

		public String queryF()
		{
			return "B.A.F";
		}
	}
}