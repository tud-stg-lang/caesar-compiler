package generated;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * thread safity of deployment
 */
public cclass CaesarTestCase_10 extends TestCase {

	public CaesarTestCase_10() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":around foo:foo:around foo";

	public void test() {
		foo();		
	}

	public static void foo() {
		result.append(":foo");
	}

}

deployed cclass Aspect_10 {
	pointcut execFoo() : execution(* CaesarTestCase_10.foo());
	
	void around() : execFoo() {
		CaesarTestCase_10.result.append(":around foo");
		proceed();
		CaesarTestCase_10.result.append(":around foo");		
	}
}

