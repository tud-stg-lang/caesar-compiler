package generated.test233;

import junit.framework.TestCase;

/**
 * Purpose: inner inheritance leads to visibility restriction public -> protected
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 233: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {

		public void m() {
		}
	}

	public cclass InnerB {

		protected void m() {
		}
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends InnerA {
	}
}