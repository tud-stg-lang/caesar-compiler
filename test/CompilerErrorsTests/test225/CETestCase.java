package generated.test225;

import junit.framework.TestCase;

/**
 * Purpose: statically qualified new operator
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 225: should not start");
	}

	OuterA.InnerA a = OuterA.new InnerA();
}

public cclass OuterA {

	public cclass InnerA {
	}
}