package generated.test200;

import junit.framework.TestCase;

/**
 * Purpose: cyclic dependencies
 *
 * @author Ivica Aracic
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 200: should not start");
	}
}

public class X {
    public X(String p1, int p2) {}
}

public cclass A extends B {}

public cclass B extends C & D {}

public cclass C extends A {}

public cclass D extends A {}