package generated;

import junit.framework.TestCase;

public cclass CaesarTestCase_0 extends TestCase {

	public CaesarTestCase_0() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public String expectedResult =
	    ":before foo:foo:subbefore foo:before foo:foo";		

	public void test() {
		deploy(new InnerAspect()) {
			foo();
		}
		deploy(new InnerAspect_Sub()) {
			foo();
		}

	
		
		assertEquals(expectedResult, result.toString());
	}

	public void foo() {
		result.append(":foo");
	}

	class InnerAspect {
	    pointcut fooCall() : call(* CaesarTestCase_0.foo());
	
	    before() : fooCall() {
		result.append(":before foo");	
	    }
	}
	class InnerAspect_Sub extends InnerAspect {
before() : fooCall() {
		    result.append(":subbefore foo");	
		}
	}
}
