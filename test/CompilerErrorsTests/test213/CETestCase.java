package generated.test213;

import junit.framework.TestCase;

/**
 * Purpose: repeated mixing
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 213: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	public cclass InnerB extends InnerA & InnerA {
	}
}