package generated.test211;

import junit.framework.TestCase;

/**
 * Purpose: interface in cclass
 *
 * @author Ivica Aracic
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 211: should not start");
	}
}

public cclass ClassA {

	interface IfcA {
	}
}