package generated.test206;

import junit.framework.TestCase;

/**
 * Purpose: inner extends outer
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 206: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA extends OuterA {
	}
}