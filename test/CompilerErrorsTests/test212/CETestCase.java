package generated.test212;

import junit.framework.TestCase;

/**
 * Purpose: mixing classes
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 212: should not start");
	}
}

public class ClassA {
}

public class ClassB {
}

public cclass CclassA extends ClassA & ClassB {

}