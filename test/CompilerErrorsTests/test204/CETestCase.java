package generated.test204;

import junit.framework.TestCase;

/**
 * Purpose: class extends cclass
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 204: should not start");
	}
}


public cclass CclassA {

}

public class ClassA extends CclassA {
}