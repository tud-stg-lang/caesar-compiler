package generated.test200;

import junit.framework.TestCase;
import java.util.LinkedList;

/**
 * Purpose: cyclic dependencies
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
		System.out.println("-------> VCTest 200: start");

		// just compile
		// the cycles below should be recognized without StackOverflowError
		
		System.out.println("-------> VCTest 200: end");
	}
}

public class X {
    public X(String p1, int p2) {}
}

public cclass A extends B {}

public cclass B extends C & D {}

public cclass C extends A {}

public cclass D extends A {}