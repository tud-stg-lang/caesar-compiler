package generated.test208;

import junit.framework.TestCase;

/**
 * Purpose: inner extends external class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 208: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB {

	public cclass InnerB extends OuterA.InnerA {
	}
}