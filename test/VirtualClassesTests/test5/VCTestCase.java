package generated.test5;

import junit.framework.*;
import java.util.*;

/**
 * Test outer class access.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult1 = "A.A";

	public static final String expectedResult2 = "B.A, B.A, B.B";

	public void test() {

		System.out.println("-------> VCTest 5: Outer Class Access: start");

		OuterA oa = new OuterA_Impl(null);	// !!! remove the parameter !!!
		OuterB ob = new OuterB_Impl(null);
		OuterA.InnerA a1 = (OuterA.InnerA)oa.$newInnerA();
		OuterB.InnerA a2 = (OuterB.InnerA)ob.$newInnerA();

		System.out.println(a1.accessOuterA());
		assertEquals(a1.accessOuterA(), expectedResult1);

		System.out.println(a2.accessOuterB());
		assertEquals(a2.accessOuterB(), expectedResult2);

        System.out.println("-------> VCTest 5: end");
	}
}

public cclass OuterA
{
	public String queryA()
	{
		return "A.A";
	}

	public cclass InnerA
	{
		public String accessOuterA()
		{
			return $outer.queryA();
		}
	}
}

public cclass OuterB extends OuterA
{
	public String queryA()
	{
		return "B.A";
	}

	public String queryB()
	{
		return "B.B";
	}

	public cclass InnerA
	{
		public String accessOuterB()
		{
			return accessOuterA() + ", " + $outer.queryA() + ", " + $outer.queryB();
		}
	}
}