package generated.test269;

import junit.framework.TestCase;

/**
 * Purpose: overriding wraps in subclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 269: should not start");
	}
}

public class ClassA {
}

public class ClassB {
}

public cclass OuterA {

	public cclass InnerA wraps ClassA {
	}

	public cclass InnerB extends InnerA wraps ClassB {
	}
}
