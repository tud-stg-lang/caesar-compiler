package generated.test14b;

import junit.framework.*;
import java.util.*;

/**
 * Test super calls using extends for furtherbindings.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "B.C2, C.C2, B.L1, A.L1";

	public void test() {

		System.out.println("-------> VCTest 14b: Test Super Calls and extends for furtherbindings: start");

		OuterC oc = new OuterC();

		String result = oc.new Leaf1().queryA();

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 14: end");
	}
}

public cclass _synth {
	public cclass Cat1 {}
	public cclass Cat2 {}
	public cclass Leaf1 {}
}

// [A, 0]
public cclass OuterA extends _synth
{
	// [A.C1, 0.C1]
	public cclass Cat1
	{
		public String queryA() {
			return "A.C1";
		}
	}

//	 [A.L1, O.L1, A.C1, 0.C1]
	public cclass Leaf1 extends Cat1
	{
		public String queryA() {
			return super.queryA() + ", A.L1";
		}
	}
}

// [B, A, 0]
public cclass OuterB extends _synth
{
	// [B.C2, O.C2]
	public cclass Cat2
	{
		public String queryA() {
			return "B.C2";
		}
	}

	// [B.L1, B.C2, O.C2]
	public cclass Leaf1 extends Cat2
	{
		public String queryA() {
			return super.queryA() + ", B.L1";
		}
	}
}

// [C, A, B, 0]
public cclass OuterC extends OuterA & OuterB
{
	// [C.C2, O.C2, C.C1, A.C1, B.C1, O.C1]
	public cclass Cat2 extends Cat1 {
		public String queryA() {
			return super.queryA() + ", C.C2";
		}
	}

	// Leaf1 : [A.L1, B.L1, C.C2, O.C2, C.C1, A.C1, B.C1, O.C1]
}