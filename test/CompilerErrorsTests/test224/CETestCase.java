package generated.test224;

import junit.framework.TestCase;

/**
 * Purpose: constructing external inner class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 224: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB {

	public cclass InnerA {
	}

	public void m() {
		InnerA a = new OuterA.InnerA();
	}
}