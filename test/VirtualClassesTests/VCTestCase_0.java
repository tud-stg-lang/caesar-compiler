package generated;

import junit.framework.TestCase;
import java.util.LinkedList;

/**
 * Simple Parser Test
 * Parsing A&B - Supertype
 */
public cclass VCTestCase_0 extends TestCase & String & LinkedList {

	public VCTestCase_0() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        // call String.length
        int len = length();
        
        // call List add
        add(0, new Object());
	}

}

public class A {
}

public class B extends A {
}
