package generated;

import junit.framework.TestCase;

public cclass AutomaticDeploymentTest extends TestCase {

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

cclass AutomaticDeploymentTestSuperAspect {
	pointcut execMethod(StringBuffer buffer) : execution(
		* automaticDeploymentTestMethod(..))
		&& args(buffer);

}

cclass AutomaticDeploymentTestAspect
	extends AutomaticDeploymentTestSuperAspect {

	pointcut callMethod(StringBuffer buffer) : call(
		* AutomaticDeploymentTest.automaticDeploymentTestMethod(..))
		&& args(buffer);

	before(StringBuffer b) : callMethod(b) {
		b.append("before : " + thisJoinPoint.toString());
	}

}

cclass AutomaticDeploymentTestSubAspect extends AutomaticDeploymentTestAspect {
	after(StringBuffer b) : execMethod(b) {
		b.append("after : " + thisJoinPoint.toString());
	}
}

cclass DeploymentThread extends Thread {

	public void run() {
		deploy(new AutomaticDeploymentTestAspect()) {
		}

	}
}