package generated.test255;

import junit.framework.TestCase;

/**
 * Purpose: overriding method in subclass with incompatible signature
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 255: should not start");
	}
}

public cclass OuterA {

	public void m() {
	}
}

public cclass OuterB extends OuterA {

	public int m() {
	}
}