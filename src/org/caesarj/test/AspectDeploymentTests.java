package org.caesarj.test;

public class AspectDeploymentTests extends FjTestCase 
{
	public AspectDeploymentTests(String name) {
		super(name);
	}
	
	/* Test dynamic deployment with inheritance */
	public void testCaesarTestCase_0() throws Throwable {
		compileAndRun("test100", "ADTestCase");
	}

	/* Test longer inner aspect inheritance sequence */
	public void testCaesarTestCase_1() throws Throwable {
		compileAndRun("test101", "ADTestCase");
	}

	/* Test static aspect deployment */
	public void testCaesarTestCase_2() throws Throwable {
		compileAndRun("test102", "ADTestCase");
	}

	/* Test abstract crosscutting classes, implementing abstract pointcuts (not supported) */
	public void testCaesarTestCase_3() throws Throwable {
		compileAndRun("test103", "ADTestCase");
	}

	/* Test precedence declaration for crosscutting and deployed classes */
	public void testCaesarTestCase_4() throws Throwable {
		compileAndRun("test104", "ADTestCase");
	}
	
	/* Test after returning, after throwing, usage of join point reflection */
	public void testCaesarTestCase_5() throws Throwable {
		compileAndRun("test105", "ADTestCase");
	}

	/* Test around advices */
	public void testCaesarTestCase_6() throws Throwable {
		compileAndRun("test106", "ADTestCase");
	}

	/* Test thread safety of deployment */
	public void testCaesarTestCase_7() throws Throwable {
		compileAndRun("test107", "ADTestCase");
	}

	/* Test thread safety of deployment */
	public void testCaesarTestCase_9() throws Throwable {
		compileAndRun("test109", "ADTestCase");
	}
     
	/* Test statically deployed around advices */
	public void testCaesarTestCase_10() throws Throwable {
		compileAndRun("test110", "ADTestCase");
	}
	
	/* Test static aspect with concrete pointcut inherits
       from static abstract aspect with concrete advice. (not supported) */
	public void testCaesarTestCase_11() throws Throwable {
		compileAndRun("test111", "ADTestCase");
	}

	/* Test deployment of multiple instances with around advices */
	public void testCaesarTestCase_12() throws Throwable {
		compileAndRun("test112", "ADTestCase");
	}
	
	/* Test using Caesar type system for crosscutting */
	public void testCaesarTestCase_13() throws Throwable {
		compileAndRun("test113", "ADTestCase");
	}
	
	/* Test crosscutting outer joins */
	public void testCaesarTestCase_14() throws Throwable {
		compileAndRun("test114", "ADTestCase");
	}
	
	/* Test crosscuts in mixins */
	public void testCaesarTestCase_15() throws Throwable {
		compileAndRun("test115", "ADTestCase");
	}
	
	/* Test if mixins are weaved */
	public void testCaesarTestCase_16() throws Throwable {
		compileAndRun("test116", "ADTestCase");
	}

	/* Test conditional pointcuts (not supported yet) */
	public void testCaesarTestCase_17() throws Throwable {
		compileAndRun("test117", "ADTestCase");
	}
	
	/* Advices on inherited pointcuts */
	public void testCaesarTestCase_18() throws Throwable {
		compileAndRun("test118", "ADTestCase");
	}
	
	/* Test deploy block robustness */
	public void testCaesarTestCase_19() throws Throwable {
		compileAndRun("test119", "ADTestCase");
	}
	
	/* Test deployment of multiple objects on inherited registries */
	public void testCaesarTestCase_20() throws Throwable {
		compileAndRun("test120", "ADTestCase");
	}
	
	/* Test exception softening (not supported yet) */
	public void testCaesarTestCase_21() throws Throwable {
		compileAndRun("test121", "ADTestCase");
	}

	/* Test privileged access. (not supported yet) */
	public void testCaesarTestCase_22() throws Throwable {
		compileAndRun("test122", "ADTestCase");
	}
	
	/* Test multi-instance around calls */
	public void testCaesarTestCase_23() throws Throwable {
		compileAndRun("test123", "ADTestCase");
	}
	
	/* Test wrapping primitive types in around */
	public void testCaesarTestCase_24() throws Throwable {
		compileAndRun("test124", "ADTestCase");
	}
	
	/* Test passing join point reflection info */
	public void testCaesarTestCase_25() throws Throwable {
		compileAndRun("test125", "ADTestCase");
	}
	
	/* Test local deployment */
	public void testCaesarTestCase_26() throws Throwable {
		compileAndRun("test126", "ADTestCase");
	}
	
	/* Test cflow pointcuts */
	public void testCaesarTestCase_27() throws Throwable {
		compileAndRun("test127", "ADTestCase");
	}
	
	/* Test cross-thread cflow pointcuts */
	public void testCaesarTestCase_28() throws Throwable {
		compileAndRun("test128", "ADTestCase");
	}
}