package generated.test209;

import junit.framework.TestCase;

/**
 * Purpose: outer cclass extends external inner cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 201: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA.InnerA {
}