package generated.test247;

import junit.framework.TestCase;

/**
 * Purpose: accessing cclass data members
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 247: should not start");

		ClassA a = new ClassA();
		int v = a.f;
	}
}

public cclass ClassA {

	public int f;
}