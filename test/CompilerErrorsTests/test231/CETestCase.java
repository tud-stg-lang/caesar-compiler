package generated.test231;

import junit.framework.TestCase;

/**
 * Purpose: constructor with wrong name
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 231: should not start");
	}
}

cclass OuterA {
}

