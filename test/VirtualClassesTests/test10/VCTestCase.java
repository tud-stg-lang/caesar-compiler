package generated.test10;

import junit.framework.*;
import java.util.*;

/**
 * Test factory methods of outer classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, A.B.A, B.A.A, A.B.A, B.C.A, B.A.A, A.B.A";

	public void test() {

		System.out.println("-------> VCTest 10: Outer Factory Methods: start");

		OuterA oa = new OuterA(); 
		OuterB ob = new OuterB();

		String resAA = oa.new InnerA().queryA();
		String resAB = oa.new InnerB().queryA();
		String resBA = ob.new InnerA().queryA();
		String resBB = ob.new InnerB().queryA();
		String resBC = ob.new InnerC().queryA();

		oa = ob;
		String resBA1 = oa.new InnerA().queryA();
		String resBB1 = oa.new InnerB().queryA();

		String result = resAA + ", " + resAB + ", " + resBA + ", " + resBB + ", " + resBC + ", " + resBA1 + ", " + resBB1;

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 10: end");
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

	public cclass InnerB
	{
		public String queryA()
		{
			return "A.B.A";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "B.A.A";
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return "B.C.A";
		}
	}
}