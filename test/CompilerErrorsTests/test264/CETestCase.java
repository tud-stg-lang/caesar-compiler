package generated.test264;

import junit.framework.TestCase;

/**
 * Purpose:  outer class objects not covariant
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 264: should not start");
	}
}

public cclass OuterA {

	public OuterA getA() {
		return this;
	}

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerA {

		public void m() {
			InnerA a1 = $outer.getA().new InnerA();
		}
	}
}