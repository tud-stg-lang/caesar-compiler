package generated;


import junit.framework.TestCase;



/**
 * static Aspect w/concrete Pointcut inherits from static abstract aspect 
 * with concrete advice.
 */
public class CaesarTestCase_11 extends TestCase {

	public CaesarTestCase_11() {
			super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":before foo(bar):foo";

	public void test() {
		deploy(new Aspect_11a()){
			foo("bar");
		}
		assertEquals(expectedResult, result.toString());
		
	}

	public void foo(String s) {
		result.append(":foo");
		System.out.println(":foo");
	}

}

	abstract crosscutting class Aspect_11 {
		abstract pointcut execFoo(String s);
	
		before(String s) : execFoo(s) {
			CaesarTestCase_11.result.append(":before foo(" + s +")");
			System.out.println(":before foo(" + s +")");
		}
	}

	crosscutting class Aspect_11a extends Aspect_11 {
		pointcut execFoo(String s) : execution(* CaesarTestCase_11.foo(String)) && args(s);
	}


