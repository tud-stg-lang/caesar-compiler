package generated.test254;

import junit.framework.TestCase;

/**
 * Purpose:  overriding inner with incompatible signature
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 254: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {

		public void m() {

		}
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerA {

		public int m() {

		}
	}
}