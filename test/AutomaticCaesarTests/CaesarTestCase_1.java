package generated;

import junit.framework.TestCase;

public class CaesarTestCase_1 extends TestCase {

	public CaesarTestCase_1() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public String expectedResult =
		":foo:before foo:foo:before foo:foo:after foo";

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

	crosscutting class InnerAspect {
		pointcut fooCall() : call(* CaesarTestCase_1.foo());
	}

	crosscutting class InnerAspect_Sub extends InnerAspect {
		before() : fooCall() {
			result.append(":before foo");	
		}

	}

	crosscutting class InnerAspect_Sub_Sub extends InnerAspect_Sub {
		after() : fooCall() {
			result.append(":after foo");
		}
	}
	
	
	
}
