package generated.test0;

import junit.framework.TestCase;
import java.util.LinkedList;

/**
 * Purpose: Test factory methods late bound new
 * TODO
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
		System.out.println("-------> VCTest 0: start");

        TestCase0 testCase = new TestCase0_Impl(null); 	// !!! remove the parameter !!!
        TestCase0.A a = testCase.$newA();
        TestCase0.B b = testCase.$newB();
		TestCase0.A.X x1 = a.$newX();
		TestCase0.A.X x2 = b.$newX();
		assertEquals(x1.toString()+'-'+x2.toString(), "A.X-B.X");

		System.out.println("-------> VCTest 0: end");
	}
}

public cclass TestCase0 {
	public cclass A {
		public cclass X {
			public String toString() {
				return "A.X";
			}
		}
	}

	public cclass B extends A {
		public cclass X {
			public String toString() {
				return "B.X";
			}
		}
	}
}
