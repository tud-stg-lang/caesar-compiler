package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * Test & in inner class
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_1 extends TestCase {

	public VCTestCase_1() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        
	}       
}


public cclass A {
	public cclass X {	
		//int x;
		void m() throws Exception {}
	}
}

public cclass B extends A {
	public cclass X {
		//int x;
		void m() {}
	}
}

public cclass C extends A {
	public cclass X {
		//int x;
		void m() throws Exception {}
	}
}

public cclass D extends B & C {
}