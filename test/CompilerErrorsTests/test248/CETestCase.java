package generated.test248;

import junit.framework.TestCase;

/**
 * Purpose: accessing outer from outer class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 248: should not start");
	}
}

public cclass OuterA {

	public void m() {

		System.out.prinln($outer.toString());
	}
}
