package generated.test33b;

import junit.framework.*;
import java.util.*;

/**
 * Test virtual class scoping with deeper nesting
 * 
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {
	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "A.U.X, B.U.Y";

	public void test() {
		System.out.println("-------> VCTest 33b: Inheritance from Implicit Type with deeper nesting: start");
        
		String result = new B().new U().new Y().queryB();
		assertEquals(expectedResult, result);		

        System.out.println("-------> VCTest 33b: end");
	}
}

public cclass A {
	public cclass U {	
		public cclass X {
			public String queryA() {
				return "A.U.X";
			}
		}
	}
}

public cclass B extends A {
    public cclass U {       
    	public cclass Y extends X {
		    public String queryB() {
				return queryA()+", B.U.Y";
			}
    	}
	}
}