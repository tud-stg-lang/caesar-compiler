package generated;

import junit.framework.TestCase;

public class CaesarTestCase_2 extends TestCase {

	public CaesarTestCase_2() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before foo:before exec foo:before exec foo:before exec foo:foo:after exec foo:after exec foo";

    public void bar() {
    	test();
    }
	public void test() {

//		deploy(new Aspect_2_Sub()) {
			foo();
//		}

		assertEquals(expectedResult, result.toString());
	}

	public void foo() {
		result.append(":foo");
	}
}

deployed class StaticAspect_2 {

	pointcut callFoo() : cflow(call(* CaesarTestCase_2.test())) && call(* CaesarTestCase_2.foo());

	before() : callFoo() {
		CaesarTestCase_2.result.append(":before foo");
	}

}
/*
deployed class StaticAspect_2_Sub extends StaticAspect_2 {
	after() : callFoo() {
		System.out.println("Hallo");
	}
}

crosscutting class Aspect_2 {

	public final static deployed Aspect_2 THIS = new Aspect_2();

	pointcut execFoo() : execution(* CaesarTestCase_2.foo());

	before() : execFoo() {
		CaesarTestCase_2.result.append(":before exec foo");
	}
}

crosscutting class Aspect_2_Sub extends Aspect_2 {

	public final static deployed Aspect_2_Sub THIS = new Aspect_2_Sub();

	after() : execFoo() {
		CaesarTestCase_2.result.append(":after exec foo");
	}
}
*/