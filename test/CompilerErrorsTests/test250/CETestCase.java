package generated.test250;

import junit.framework.TestCase;

/**
 * Purpose: private access in overriden class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 250: should not start");
	}
}

public cclass ClassA {

	public cclass InnerA {
		private int a;
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {

		public void m() {
			a = 2;
		}
	}
}


