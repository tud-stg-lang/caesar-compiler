package generated.test222;

import junit.framework.TestCase;

/**
 * Purpose: new array on cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 222: should not start");

		Object a = new ClassA[2];
	}
}

public cclass ClassA {

}