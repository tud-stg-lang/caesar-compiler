package generated;

import java.util.HashMap;

import junit.framework.TestCase;

public class DeploymentTest extends TestCase {
	
	declare parents : DeploymentTest implements java.io.Serializable;
	
//	declare warning : call(* *(..)) : "declared warning";

	public SimpleAspect aspect = new SimpleAspect();

	public SimpleAspect subAspect = new SubSimpleAspect();

	public static final deployed AnotherAspect anotherAspect =
		new AnotherAspect();

	public static boolean otherThreadReady = false;

	public static Object lockObject;

	public DeploymentTest() {
		super("test");
	}

	public void test() {
		lockObject = new Object();

		Thread thread = new AnotherThread();
		thread.start();

		deploy(new SimpleAspect()) {
			deploy(new SimpleAspect()) {
				deploy(new SimpleAspect()) {

					System.out.println(m(12));
					synchronizeThreads();
				}
			}
		}

		System.out.println(m(66));

	}

	public static int m(int i) {
		System.out.println(
			"Method execution in " + Thread.currentThread().toString());

		return i;
	}

	public void synchronizeThreads() {
		synchronized (lockObject) {
			if (!otherThreadReady) {
				try {
					lockObject.wait();
				} catch (InterruptedException e) {
					System.out.println(e.toString());
				}
			}
		}

		synchronized (AnotherThread.lockObject) {

			AnotherThread.otherThreadReady = true;
			AnotherThread.lockObject.notify();
		}

	}

}

class AnotherThread extends Thread {

	public static Object lockObject;

	public static boolean otherThreadReady = false;

	public void run() {
		lockObject = new Object();

		deploy(new SubSimpleAspect()) {
			deploy(new SubSimpleAspect()) {
				deploy(new SimpleAspect()) {
					System.out.println(DeploymentTest.m(42));
					synchronizeThreads();
				}
			}
		}

		System.out.println(DeploymentTest.m(100));

	}

	public void synchronizeThreads() {
		synchronized (DeploymentTest.lockObject) {

			DeploymentTest.otherThreadReady = true;
			DeploymentTest.lockObject.notify();
		}

		synchronized (lockObject) {
			if (!otherThreadReady) {
				try {
					lockObject.wait();
				} catch (InterruptedException e) {
					System.out.println(e.toString());
				}
			}
		}

	}

}