package generated.test215;

import junit.framework.TestCase;

/**
 * Purpose: mixing interfaces
 *
 * @author Vaidas Gasiunas
 */
public class CETestCase extends TestCase {

	public CETestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> CETest 215: should not start");
	}
}


public interface IfcA {
}

public interface IfcB {
}

public cclass ClassA extends IfcA & IfcB {
}