package generated.test216;

import junit.framework.TestCase;

/**
 * Purpose: class extends interface
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 216: should not start");
	}
}

public interface IfcA {
}

public cclass ClassA extends IfcA {
}