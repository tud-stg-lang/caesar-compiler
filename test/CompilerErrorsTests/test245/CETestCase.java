package generated.test245;

import junit.framework.TestCase;

/**
 * Purpose: method call from static context
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 245: should not start");

		OuterA.m();
	}
}

public cclass OuterA {

	public void m() {
	}
}