package generated.test239;

import junit.framework.TestCase;

/**
 * Purpose: accessing protected of newly created same class object
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 239: should not start");
	}
}

public cclass OuterA {

	protected void n() {
	}

	public void m() {
		new OuterA().n();
	}
}