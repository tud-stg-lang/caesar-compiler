package generated.test244;

import junit.framework.TestCase;

/**
 * Purpose:  outer this
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 244: should not start");
	}
}

public cclass OuterA {

	public void m() {
	}

	public cclass InnerA {

		public void n() {
			OuterA.this.m();
		}
	}
}