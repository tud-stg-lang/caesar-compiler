package generated.test223;

import junit.framework.TestCase;

/**
 * Purpose: direct construction of inner class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 223: should not start");

		OuterA.InnerA a = new OuterA.InnerA();
	}
}


public cclass OuterA {

	public cclass InnerA {
	}
}