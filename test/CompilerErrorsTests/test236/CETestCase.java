package generated.test236;

import junit.framework.TestCase;

/**
 * Purpose: accessing private method from inner class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 236: should not start");
	}
}

public cclass OuterA {

	private void m() {
	}

	public cclass InnerA {

		public void n() {
			$outer.m();
		}
	}
}