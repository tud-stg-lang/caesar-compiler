package org.caesarj.test;

public class CompileAndRunResultsWeaverTest extends FjTestCase 
{
	public CompileAndRunResultsWeaverTest(String name)	{
		super(name);
	}
	
	public void testNestedAspectTest() throws Throwable {
		compileAndRun("NestedAspectTest");
	}

	public void testJoinPointReflectionTest() throws Throwable {
		compileAndRun("JoinPointReflectionTest");
	}

	public void testDeploymentTest() throws Throwable {
		compileAndRun("DeploymentTest");
	}

	public void testAutomaticDeploymentTest() throws Throwable {
		compileAndRun("AutomaticDeploymentTest");
	}

	public void testPrivilegedAccessTest() throws Throwable {
		compileAndRun("PrivilegedAccessTest");
	}
}