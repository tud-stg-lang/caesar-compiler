package generated.test243;

import junit.framework.TestCase;

/**
 * Purpose: implicit outer inner class construction
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 243: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {

		public void n() {
			InnerA a = new InnerA();
		}
	}
}
