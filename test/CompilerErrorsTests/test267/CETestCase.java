package generated.test267;

import junit.framework.TestCase;

/**
 * Purpose: wraps in simple class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 267: should not start");
	}
}

public class ClassA {
}

public class ClassB wraps ClassA {
}
