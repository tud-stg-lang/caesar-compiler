package generated.test214;

import junit.framework.TestCase;

/**
 * Purpose: mixing outer with inner
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 214: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA & OuterA.InnerA {
}