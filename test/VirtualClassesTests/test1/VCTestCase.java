package generated.test1;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * Test implements caluse for cclass.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "A.X->B.X";

	public static interface I {
		String msg();
	}

	public void test() {
		System.out.println("-------> VCTest 1: start");

		final TestCase1 testCase = new TestCase1();
		testCase.A a = testCase.new B();
        I i = a.new X();
        assertEquals(i.msg(), expectedResult);

        System.out.println("-------> VCTest 1: end");
	}
}

public cclass TestCase1 {
	public cclass A {
		public cclass X implements VCTestCase.I {
			public String msg() {
				return "A.X";
			}
		}
	}

	public cclass B extends A {
		public cclass X {
			public String msg() {
				return super.msg()+"->B.X";
			}
		}
	}
}