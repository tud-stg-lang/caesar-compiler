package generated.test234;

import junit.framework.TestCase;

/**
 * Purpose:  restricting access in overriden method protected -> private
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 234: should not start");
	}
}

public cclass OuterA {

	protected void m() {
	}
}

public cclass OuterB extends OuterA {

	private void m() {
	}
}