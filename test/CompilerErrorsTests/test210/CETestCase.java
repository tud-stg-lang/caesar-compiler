package generated.test210;

import junit.framework.TestCase;

/**
 * Purpose: inner inheritance cycle through different branches of mixin combination
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 210: should not start");
	}
}

public cclass OuterA {

	public cclass InnerA {
	}

	public cclass InnerB {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends InnerA {
	}
}

public cclass OuterC extends OuterA {

	public cclass InnerA extends InnerB {
	}
}

public cclass OuterD extends OuterB & OuterC {
}




