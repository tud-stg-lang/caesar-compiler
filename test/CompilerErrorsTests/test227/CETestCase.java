package generated.test227;

import junit.framework.TestCase;

/**
 * Purpose: constructing non-existing inner class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 227: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB {
	}

	public void m() {
		OuterB.InnerB a = new OuterA().new InnerB();
	}
}