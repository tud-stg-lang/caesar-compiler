package generated.test201;

import junit.framework.TestCase;

/**
 * Purpose: class in cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 201: should not start");
	}
}

public cclass OuterA {

	public class InnerA {
	}
}