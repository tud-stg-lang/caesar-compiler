package generated.test220;

import junit.framework.TestCase;

/**
 * Purpose: changing mixing order
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 220: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	public cclass InnerB {
	}

	public cclass InnerC extends InnerA & InnerB {
	}
}


public cclass OuterB extends OuterA {

	public cclass InnerC extends InnerB & InnerA {
	}
}