package generated.test246;

import junit.framework.TestCase;

/**
 * Purpose: static method call from within the class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 246: should not start");
	}
}

public cclass OuterA {

	public void m() {
		OuterA.m();
	}
}