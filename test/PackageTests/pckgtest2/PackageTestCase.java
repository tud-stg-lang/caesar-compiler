package generated.pckgtest2;

import junit.framework.TestCase;

/**
 * Purpose: Test deep nested externalized classes
 *
 * @author Ivica Aracic
 */
public class PackageTestCase extends TestCase {

	public PackageTestCase() {
		super("test");
	}
	
	private String expectedResult = "aa/ac/aaa/bca/aab/";

	public void test() {
		System.out.println("-------> PackageTest 2: Test deep nested externalized classes: start");
		
		OuterB b = new OuterB();
		OuterA.InnerA ba = b.new InnerA();
		OuterB.InnerC bc = b.new InnerC();
		OuterB.InnerC.DeepA bca = bc.new DeepA();
		OuterB.InnerC.DeepB bcb = bc.new DeepB();
		
		String result = ba.aa() + bc.ac() + bca.aaa() + bca.bca() + bcb.aab();
		System.out.println(result);
		assertEquals(result, expectedResult);
				
		System.out.println("-------> PackageTest 2: end");
	}	
}
