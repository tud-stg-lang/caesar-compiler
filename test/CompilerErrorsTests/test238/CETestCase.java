package generated.test238;

import junit.framework.TestCase;

/**
 * Purpose: accessing protected of the another same class object
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 238: should not start");
	}
}

public cclass OuterA {

	protected void n() {

	}

	public void m(OuterA a) {
		a.n();
	}
}
