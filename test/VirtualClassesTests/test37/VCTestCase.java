package generated.test37;

import junit.framework.*;
import java.util.*;

/**
 * Test automatic casts
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> VCTest 37: automatic casts: start");

        // just compile for now

        System.out.println("-------> VCTest 37: end");
	}
}


public cclass OuterA {
	public cclass InnerA {
	    public void aa() {
	    }
	}
	
	public cclass InnerB {
		protected InnerA a;
		
		public void ab() {
			a.aa();
		}
	}
}

public cclass OuterB extends OuterA {	
	public cclass InnerA {
		public void ba() {}
	}
	
	public cclass InnerB {
		public void bb() {
		    OuterB.InnerA aa = a;
			a.ba();
		}
	}
}
