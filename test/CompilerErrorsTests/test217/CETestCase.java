package generated.test217;

import junit.framework.TestCase;

/**
 * Purpose: cclass in implements
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 217: should not start");
	}
}

public cclass OuterA {
}

public cclass OuterB implements OuterA {
}