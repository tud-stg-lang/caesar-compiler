package generated.test242;

import junit.framework.TestCase;

/**
 * Purpose: implicit outer method call
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 242: should not start");
	}
}

public cclass OuterA {

	public void m() {
	}

	public cclass InnerA {

		public void n() {
			m();
		}
	}
}





