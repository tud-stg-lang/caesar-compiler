package generated.test263;

import junit.framework.TestCase;

/**
 * Purpose: assigning to more specific virtual class inside context of more general class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 263: should not start");
	}
}

public cclass ClassA {

	public cclass InnerA {
		public void m(InnerA a) {
			ClassB.InnerA a1 = a;
		}
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
	}
}
