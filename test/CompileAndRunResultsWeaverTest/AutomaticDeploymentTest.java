package generated;

import junit.framework.TestCase;

public class AutomaticDeploymentTest extends TestCase {

	public AutomaticDeploymentTest() {
		super("test");
	}

	public void test() {
		Thread deploymentThread = new DeploymentThread();
		deploymentThread.start();

		StringBuffer testBuffer = new StringBuffer();
		String expectedTestResult =
			"before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))"
				+ "before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))"
				+ "before : call(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))"
				+ "AutomaticDeploymentTestMethod"
				+ "after : execution(void generated.AutomaticDeploymentTest.automaticDeploymentTestMethod(StringBuffer))"
				+ "AutomaticDeploymentTestMethod";

		deploy(new AutomaticDeploymentTestAspect()) {
			deploy(new AutomaticDeploymentTestAspect()) {
				deploy(new AutomaticDeploymentTestSubAspect()) {
					automaticDeploymentTestMethod(testBuffer);
				}
			}

		}

		automaticDeploymentTestMethod(testBuffer);

		assertEquals(expectedTestResult, testBuffer.toString());

		//		System.out.println(testBuffer.toString());

	}

	public void automaticDeploymentTestMethod(StringBuffer testBuffer) {
		testBuffer.append("AutomaticDeploymentTestMethod");
	}
}

class AutomaticDeploymentTestSuperAspect {
	pointcut execMethod(StringBuffer buffer) : execution(
		* automaticDeploymentTestMethod(..))
		&& args(buffer);

}

class AutomaticDeploymentTestAspect
	extends AutomaticDeploymentTestSuperAspect {

	pointcut callMethod(StringBuffer buffer) : call(
		* AutomaticDeploymentTest.automaticDeploymentTestMethod(..))
		&& args(buffer);

	before(StringBuffer b) : callMethod(b) {
		b.append("before : " + thisJoinPoint.toString());
	}

}

class AutomaticDeploymentTestSubAspect extends AutomaticDeploymentTestAspect {
	after(StringBuffer b) : execMethod(b) {
		b.append("after : " + thisJoinPoint.toString());
	}
}

class DeploymentThread extends Thread {

	public void run() {
		deploy(new AutomaticDeploymentTestAspect()) {
		}

	}
}