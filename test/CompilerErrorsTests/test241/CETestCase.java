package generated.test241;

import junit.framework.TestCase;

/**
 * Purpose: access outer field
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 241: should not start");
	}
}

public cclass OuterA {

	public int v;

	public cclass InnerA {

		public void m() {
			int a = $outer.v;
		}
	}
}

