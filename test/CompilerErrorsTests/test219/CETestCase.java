package generated.test219;

import junit.framework.TestCase;

/**
 * Purpose: extending overrriden classes
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 219: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends OuterA.InnerA {
	}
}