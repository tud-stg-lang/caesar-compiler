package generated.test240;

import junit.framework.TestCase;

/**
 * Purpose: protected cclass constructor
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 240: should not start");

		ClassA a = new ClassA();
	}
}

public cclass ClassA {

	protected ClassA() {

	}
}

