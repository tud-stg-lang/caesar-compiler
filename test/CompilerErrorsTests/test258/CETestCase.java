package generated.test258;

import junit.framework.TestCase;

/**
 * Purpose: mixing inner classes with incompatible signatures
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 258: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {

		public void m() {
		}
	}
}

public cclass OuterB {

	public cclass InnerA {

		public int m() {
			return 2;
		}
	}
}

public cclass OuterC extends OuterA & OuterB {
}