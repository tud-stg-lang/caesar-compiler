package generated.test249;

import junit.framework.TestCase;

/**
 * Purpose: changing outer
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 249: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {

		public void m() {
			$outer = new OuterA();
		}
	}
}