package generated;

import junit.framework.TestCase;

/**
 * after returning, after throwing, usage of join point reflection
 */
public cclass CaesarTestCase_5 extends TestCase {

	public CaesarTestCase_5() {
		super("test");
	}

	public static int fooCounter = 0;

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":around foo:foo:after returning foo:around foo:after throwing ex foo:after returning bar 42";

	public void test() {
		deploy(new Aspect_5()) {
			foo();

			try {
				foo();
			} catch (RuntimeException e) {
			}

			bar();
		}

		assertEquals(expectedResult, result.toString());
	}

	public void foo() throws RuntimeException {
		if (fooCounter == 1) {
			throw new RuntimeException("ex");
		}

		result.append(":foo");
		fooCounter++;
	}

	public int bar() {
		return 42;
	}

}

cclass Aspect_5 {

	pointcut callFoo() : call(* CaesarTestCase_5.foo());

	pointcut callBar() : call(* CaesarTestCase_5.bar());

	void around() : callFoo() {
		CaesarTestCase_5.result.append(":around foo");
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();		
		proceed();
	}

	after() returning : callFoo() {
		CaesarTestCase_5.result.append(":after returning foo");
	}

	after() throwing(RuntimeException e) : callFoo() {
		CaesarTestCase_5.result.append(
			":after throwing " + e.getMessage() + " foo");
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();
	}

	after() returning(int i) : callBar() {
		CaesarTestCase_5.result.append(":after returning bar " + i);
		thisJoinPoint.toString();
		thisJoinPointStaticPart.toString();
		thisEnclosingJoinPointStaticPart.toString();
	}
}
