package generated.test262;

import junit.framework.TestCase;

/**
 * Purpose: assigning to more specific virtual class inside context of more specific class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 262: should not start");
	}
}

public cclass ClassA {

	public cclass InnerA {
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
		public void m() {
			InnerA a = new ClassA().new InnerA();
		}
	}
}





