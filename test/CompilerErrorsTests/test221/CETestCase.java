package generated.test221;

import junit.framework.TestCase;

/**
 * Purpose: new operator with parameter
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 221: should not start");

		ClassA a = new ClassA(1);
	}
}

public cclass ClassA {

}




