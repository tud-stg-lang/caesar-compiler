package generated;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * around
 */
public cclass CaesarTestCase_6 extends TestCase {

	public CaesarTestCase_6() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":foo:around foo 42:around bar:ioe";

	public void test() {
		deploy(new Aspect_6()) {
			int i = foo();

			try {
				bar();
			} catch (IOException e) {
				result.append(e.getMessage());
			}
		}

		assertEquals(expectedResult, result.toString());
	}

	public int foo() {

		result.append(":foo");

		return 42;

	}

	public void bar() throws IOException {

		result.append(":bar");
	}

}

cclass Aspect_6 {

	pointcut callFoo() : call(* CaesarTestCase_6.foo());

	pointcut callBar() : call(* CaesarTestCase_6.bar());

	Object around() : callFoo() {
		int result = ((Integer) proceed()).intValue();

		CaesarTestCase_6.result.append(":around foo " + result);

		return new Integer(result);
	}

	void around() throws IOException : callBar() {
		CaesarTestCase_6.result.append(":around bar");

		throw new IOException(":ioe");
	}

}
