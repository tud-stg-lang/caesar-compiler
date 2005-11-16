package generated.pckgtest6;

import junit.framework.TestCase;

/**
 * Purpose: Test externalized class imports
 *
 * @author Ivica Aracic
 */
public class PackageTestCase extends TestCase {

	public PackageTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> PackageTest 1: Test externalized class imports: start");
		
		OuterA.InnerA a = new OuterA().new InnerA();
		a.m();		
		String res = (String)a.getList().get(0);
		assertEquals(res, "aaa");
		
		OuterA.InnerB b = new OuterA().new InnerB();
		b.m();		
		String res2 = (String)b.getList().get(0);
		assertEquals(res2, "aaa");
		
		System.out.println("-------> PackageTest 1: end");
	}	
}
