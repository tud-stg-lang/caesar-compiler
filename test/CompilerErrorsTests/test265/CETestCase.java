package generated.test265;

import junit.framework.TestCase;

/**
 * Purpose: passing more general virtual type as parameter for more specific
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 265: should not start");

		OuterA oa = new OuterA();
		OuterB ob = new OuterB();

		ob.n(oa.m());
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	public InnerA m() {
		return null;
	}

	public void n(InnerA a) {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerA {
	}
}