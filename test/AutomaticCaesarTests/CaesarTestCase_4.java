package generated;

import junit.framework.TestCase;

/**
 * declare precdence for crosscutting and deployed classes
 */
public class CaesarTestCase_4 extends TestCase {

	public CaesarTestCase_4() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before foo C:before foo A:before foo D:before foo B:foo";

	public void test() {
		deploy(new A_4()) {
			deploy(new B_4()) {
				deploy(new C_4()) {
					foo();
				}
			}
		}

		assertEquals(expectedResult, result.toString());
	}

	public void foo() {
		result.append(":foo");
	}
}

deployed class Ordering {
	declare precedence : C_4, A_4, D_4, B_4;
}


crosscutting class A_4 {

	pointcut callFoo() : call(* CaesarTestCase_4.foo());

	before() : callFoo() {
		CaesarTestCase_4.result.append(":before foo A");
	}
}

crosscutting class B_4 {

	pointcut callFoo() : call(* CaesarTestCase_4.foo());

	before() : callFoo() {
		CaesarTestCase_4.result.append(":before foo B");
	}
}

crosscutting class C_4 {

	pointcut callFoo() : call(* CaesarTestCase_4.foo());

	before() : callFoo() {
		CaesarTestCase_4.result.append(":before foo C");
	}
}

deployed class D_4 {

	pointcut callFoo() : call(* CaesarTestCase_4.foo());

	before() : callFoo() {
		CaesarTestCase_4.result.append(":before foo D");
	}

}
