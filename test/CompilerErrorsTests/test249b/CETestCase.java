package generated.test249b;

import junit.framework.TestCase;

/**
 * Purpose: changing wrappee
 *
 * @author Ivica Aracic
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 249b: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA wraps String {

		public void m() {
			$wrappee = "boom!";
		}
	}
}