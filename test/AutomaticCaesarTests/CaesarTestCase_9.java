package generated;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * thread safity of deployment
 */
public class CaesarTestCase_9 extends TestCase {

	public CaesarTestCase_9() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = ":before foo:foo:after foo:before foo:foo:after foo:foo";

	public void test() {
		deploy(new Aspect_9()) {
			Thread anotherThread = new AnotherThread_9();
			anotherThread.start();
		}
		
		Barrier.getInstance().check(); //1	
		
		Barrier.getInstance().check();	//2
        
        foo();
        
		assertEquals(expectedResult, result.toString());
	}

	public static void foo() {
		result.append(":foo");
	}

}

class AnotherThread_9 extends CaesarThread {
	public void run() {
		CaesarTestCase_9.foo();
		
		Barrier.getInstance().check();	//1

		CaesarTestCase_9.foo();		
		
		Barrier.getInstance().check(); //2

	}
}

crosscutting class Aspect_9 {

	pointcut callFoo() : call(* generated.CaesarTestCase_9.foo());

	before() : callFoo() {
		CaesarTestCase_9.result.append(":before foo");
	}

	after() : callFoo() {
		CaesarTestCase_9.result.append(":after foo");
	}

}
