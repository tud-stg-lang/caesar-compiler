package generated.test270;

import junit.framework.TestCase;

/**
 * Purpose: applying wrapper function on wrong type
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 270: should not start");

		OuterA oa = new OuterA();
		OuterA.InnerA ia = oa.InnerA(new ClassB());
	}
}

public class ClassA {
}

public class ClassB {
}

public cclass OuterA {

	public cclass InnerA wraps ClassA {
	}
}

