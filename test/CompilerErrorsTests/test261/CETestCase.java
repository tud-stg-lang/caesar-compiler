package generated.test261;

import junit.framework.TestCase;

/**
 * Purpose: assigning to more specific virtual class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 261: should not start");

		ClassB.InnerA a = new ClassA().new InnerA();
	}
}

public cclass ClassA {

	public cclass InnerA {
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
	}
}


