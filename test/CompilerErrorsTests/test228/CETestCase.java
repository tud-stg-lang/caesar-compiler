package generated.test228;

import junit.framework.TestCase;

/**
 * Purpose: construction of virtual class in static context
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 228: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	static public void m() {

		InnerA a = new InnerA();
	}
}