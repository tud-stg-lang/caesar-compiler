package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_2 extends TestCase {

	public VCTestCase_2() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        X x = new Y()
        X.B = (X.B)x.$newB();
	}       
}

public cclass X {
	public X() {
	}

	public cclass A {
		public A() {			
		}
	}
	
	public cclass B {
		public B() {
			super();
		}
	}	
}

public cclass Y extends X {
	public Y() {
		super();
	}	
	
	public cclass A {
		public A() {
			super();
		}
	}
}
