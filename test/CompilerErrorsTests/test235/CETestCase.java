package generated.test235;

import junit.framework.TestCase;

/**
 * Purpose: accessing private method from subclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 235: should not start");
	}
}

public cclass ClassA {

	private void m() {
	}
}

public cclass ClassB extends ClassA {

	public void n() {
		m();
	}
}