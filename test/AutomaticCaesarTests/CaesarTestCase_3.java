package generated;

import junit.framework.TestCase;

/**
 * abstract crosscutting classes, implementing abstract pointcuts
 */
public class CaesarTestCase_3 extends TestCase {

	public CaesarTestCase_3() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":before foo:foo";

	public void test() {
		deploy(new ConcreteAspect_3()) {
			foo();
		}

		assertEquals(expectedResult, result.toString());
	}

	public void foo() {
		result.append(":foo");
	}
}

abstract crosscutting class AbstractAspect_3 {
	abstract pointcut callFoo();
	
	before() : callFoo() {
		CaesarTestCase_3.result.append(":before foo");
	}
}

crosscutting class ConcreteAspect_3 extends AbstractAspect_3 {
	pointcut callFoo() : call(* CaesarTestCase_3.foo());
}
