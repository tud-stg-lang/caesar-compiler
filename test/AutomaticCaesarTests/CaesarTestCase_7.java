package generated;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * thread safity of deployment
 */
public class CaesarTestCase_7 extends TestCase {

	public CaesarTestCase_7() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before foo:before foo:before foo:foo:foo:after foo:after foo:after foo";

	public void test() {
	    new TestCase_7().test();
        
        assertEquals(expectedResult, result.toString());
	}

    public static void foo() {
        Barrier.getInstance().check(); //Checkpoint 1, both threads

        CaesarTestCase_7.result.append(":foo");

        Barrier.getInstance().check(); //Checkpoint 2, both threads
    }
}

cclass TestCase_7 {

    public void test() {
        Runnable anotherThread = new AnotherThread_7();
        new Thread(anotherThread).start();

        deploy(new Aspect_7()) {
            CaesarTestCase_7.foo();
        }

        //barrier is important, since otherwise the assertion could done before
        //the other thread has written its result to the StringBuffer
        Barrier.getInstance().check(); //Checkpoint 3, thread main
    }

}

cclass AnotherThread_7 implements Runnable {
	public void run() {
		deploy(new Aspect_7()) {
			deploy(new Aspect_7()) {
                CaesarTestCase_7.foo();
			}
		}

		Barrier.getInstance().check(); //Checkpoint 3, child thread

	}
}

cclass Aspect_7 {

	pointcut callFoo() : call(* CaesarTestCase_7.foo());

	before() : callFoo() {
		CaesarTestCase_7.result.append(":before foo");
	}

	after() : callFoo() {
		CaesarTestCase_7.result.append(":after foo");
	}

}
