package generated;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * thread safity of deployment
 */
public class CaesarTestCase_11 extends TestCase {

	public CaesarTestCase_11() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":before foo(bar):foo";

	public void test() {
		foo("bar");		
	}

	public static void foo(String s) {
		result.append(":foo");
	}

}

abstract crosscutting class Aspect_11 {
	abstract pointcut execFoo(String s);
	
	before(String s) : execFoo(s) {
		CaesarTestCase_11.result.append(":before foo(" + s +")");
	}
}

crosscutting class Aspect_11a {
	pointcut execFoo(String s) : execution(* foo(..)) && args(s);
}

