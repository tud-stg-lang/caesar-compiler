package generated.test205;

import junit.framework.TestCase;

/**
 * Purpose: class implements cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 205: should not start");
	}
}

public cclass CclassA {
}

public class ClassA implements CclassA {
}