package generated.test8;

import junit.framework.*;
import java.util.*;

/**
 * Test type inheritance of inner classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, B.A.B, A.A.C, B.A.D, A.B.E, A.B.F, A.B.F";

	public void test() {

		System.out.println("-------> VCTest 8: Inner Class Type Inheritance: start");

		OuterB ob = new OuterB();
		OuterB.InnerA ba = ob.new InnerA();

		String resA = ((InterfaceA)ba).queryA();
		String resB = ((InterfaceB)ba).queryB();
		String resC = ((OuterA.InnerA)ba).queryC();
		String resD = ba.queryD();
		String resE = ((InterfaceE)ba).queryE();
		String resF = ((OuterA.InnerB)ba).queryF();
		String resF1= ((OuterB.InnerB)ba).queryF();
		String result = resA + ", " + resB + ", " + resC + ", " + resD + ", "
							 + resE + ", " + resF + ", " + resF1;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 8: end");
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
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA implements InterfaceB
	{
		public String queryB()
		{
			return "B.A.B";
		}

		public String queryD()
		{
			return "B.A.D";
		}
	}
}