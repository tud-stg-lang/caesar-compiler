package generated.test257;

import junit.framework.TestCase;

/**
 * Purpose: mixing incompatible methods
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 257: should not start");
	}
}

public cclass OuterA {

	public int m() {
		return 0;
	}
}

public cclass OuterB {

	public void m() {
	}
}

public cclass OuterC extends OuterA & OuterB {
}