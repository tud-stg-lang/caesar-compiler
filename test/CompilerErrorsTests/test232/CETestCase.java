package generated.test232;

import junit.framework.TestCase;

/**
 * Purpose: restricting access in overriden method public -> protected
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 232: should not start");
	}
}

public cclass ClassA {

	public void m() {
	}
}

public cclass ClassB extends ClassA {

	protected void m() {
	}
}





