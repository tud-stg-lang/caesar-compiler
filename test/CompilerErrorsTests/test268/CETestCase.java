package generated.test268;

import junit.framework.TestCase;

/**
 * Purpose: overriding wraps in overriden class
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 268: should not start");
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

public cclass OuterB extends OuterA {

	public cclass InnerA wraps ClassB {
	}
}