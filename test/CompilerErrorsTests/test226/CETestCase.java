package generated.test226;

import junit.framework.TestCase;

/**
 * Purpose: qualified new operator inside cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 226: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	public void m() {
		InnerA a = OuterA.new InnerA();
	}
}