package generated.test256;

import junit.framework.TestCase;

/**
 * Purpose: overriding method with exception specification
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 256: should not start");
	}
}

public cclass OuterA {

	public void m() {
	}
}

public cclass OuterB extends OuterA {

	public void m() throws Exception {
	}
}