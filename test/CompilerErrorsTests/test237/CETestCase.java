package generated.test237;

import junit.framework.TestCase;

/**
 * Purpose: accessing protected method from inner class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 237: should not start");
	}
}

public cclass OuterA {

	protected void m() {
	}

	public cclass InnerA {

		public void n() {
			$outer.m();
		}
	}
}