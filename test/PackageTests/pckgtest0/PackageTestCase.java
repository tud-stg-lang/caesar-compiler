package generated.pckgtest0;

import junit.framework.TestCase;

/**
 * Purpose: Test externalized class
 *
 * @author Ivica Aracic
 */
public class PackageTestCase extends TestCase {

	public PackageTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> PackageTest 0: Test externalized class: start");
		
		OuterA.InnerA a = new OuterA().new InnerA();
		assertEquals(a.m(), 5);
		
		System.out.println("-------> PackageTest 0: end");
	}	
}
