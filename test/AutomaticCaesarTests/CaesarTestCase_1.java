package generated;

import junit.framework.TestCase;

public cclass CaesarTestCase_1 extends TestCase {

	public CaesarTestCase_1() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public String expectedResult =
		":foo:before foo:foo:before foo:foo:after foo";

	/**
	 * tests inheriting aspects
	 *
	 */
	public void test() {
		deploy(new InnerAspect()) {
			foo();
		}

		deploy(new InnerAspect_Sub()) {
			foo();
		}

		deploy(new InnerAspect_Sub_Sub()) {
			foo();
		}
		
		
		assertEquals(expectedResult, result.toString());
	}

	public void foo() {
		result.append(":foo");
	}

	cclass InnerAspect {
		pointcut fooCall() : call(* CaesarTestCase_1.foo());
		before() : fooCall() {
			System.out.println("just for testing");
		}
	}

	cclass InnerAspect_Sub extends InnerAspect {
		before() : fooCall() {
			result.append(":before foo");	
		}

	}

	cclass InnerAspect_Sub_Sub extends InnerAspect_Sub {
		after() : fooCall() {
			result.append(":after foo");
		}
	}
	
	
	
}
