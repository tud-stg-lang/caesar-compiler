package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * Test implements caluse for cclass.
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_1 extends TestCase {

	public VCTestCase_1() {
		super("test");
	}

	public static final String expectedResult = "A.X->B.X";

	public static interface I {
		String msg();
	}

	public void test() {
		TestCase1 testCase = new TestCase1();		
        TestCase1.A a = testCase.$newB();
        I i = a.$newX();
        assertEquals(i.msg(), expectedResult);
	}       
}

public cclass TestCase1 {
	public cclass A {
		public cclass X implements VCTestCase_1.I {	
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