package generated.test202;

import junit.framework.TestCase;

/**
 * Purpose: cclass in class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 202: should not start");
	}
}

public class OuterA {

	public cclass InnerA {
	}
}