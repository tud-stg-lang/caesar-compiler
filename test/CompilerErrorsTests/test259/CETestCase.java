package generated.test259;

import junit.framework.TestCase;

/**
 * Purpose: non-existing super call
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 259: should not start");
	}
}

public cclass OuterA {

}

public cclass OuterB extends OuterA {

	public void m(double a) {
		super.m(a);
	}
}
