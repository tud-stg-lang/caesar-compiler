package generated;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * deployment of multiple instances vs. around advices
 */
public class CaesarTestCase_12 extends TestCase {

	public CaesarTestCase_12() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":bar1:bar2:bar3:bar4:foo";

	public void test() {
		deploy(new Aspect_12(1)) {
			deploy(new Aspect_12(2)) {
				deploy(new Aspect_12(3)) {
					deploy(new Aspect_12(4)) {
						foo("string");
					}
				}
			}
		}

		assertEquals(expectedResult, result.toString());
	}

	public static char foo(String s) {
		result.append(":foo");
		
		return 'a';
	}

}

crosscutting class Aspect_12 {
	public Aspect_12(int i) {
		this.i = i;
	}
	private int i;
	
	pointcut callFoo(String str) : call(* CaesarTestCase_12.foo(..)) && args(str);

	char around(String s) : callFoo(s) {
		CaesarTestCase_12.result.append(":bar"+i);
		return proceed(s);
	}
}
