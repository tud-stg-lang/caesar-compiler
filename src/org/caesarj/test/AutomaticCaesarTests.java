package org.caesarj.test;

public class AutomaticCaesarTests extends FjTestCase 
{
	public AutomaticCaesarTests(String name) {
		super(name);
	}
	
	public void testCaesarTestCase_0() throws Throwable {
		compileAndRun("CaesarTestCase_0");
	}

	public void testCaesarTestCase_1() throws Throwable {
		compileAndRun("CaesarTestCase_1");
	}

	public void testCaesarTestCase_2() throws Throwable {
		compileAndRun("CaesarTestCase_2");
	}

	public void testCaesarTestCase_3() throws Throwable {
		compileAndRun("CaesarTestCase_3");
	}

	public void testCaesarTestCase_4() throws Throwable {
		compileAndRun("CaesarTestCase_4");
	}

	public void testCaesarTestCase_5() throws Throwable {
		compileAndRun("CaesarTestCase_5");
	}

	public void testCaesarTestCase_6() throws Throwable {
		compileAndRun("CaesarTestCase_6");
	}

	public void testCaesarTestCase_7() throws Throwable {
		removeClassFiles(null);
		
		compileFile("Barrier");
		compileFile("CaesarTestCase_7");
				
		doGeneratedTest("CaesarTestCase_7");
	}

//  tests priveleged access - the functionality not implemented
//	public void testCaesarTestCase_8() throws Throwable {
//		compileAndRun("CaesarTestCase_8");
//	}

	public void testCaesarTestCase_9() throws Throwable {
		removeClassFiles(null);
		
		compileFile("Barrier");
		compileFile("CaesarTestCase_9");
				
		doGeneratedTest("CaesarTestCase_9");
	}
     	
	public void testCaesarTestCase_10() throws Throwable {
		compileAndRun("CaesarTestCase_10");
	}
	
	public void testCaesarTestCase_11() throws Throwable {
		compileAndRun("CaesarTestCase_11");
	}

	public void testCaesarTestCase_12() throws Throwable {
		compileAndRun("CaesarTestCase_12");
	}
}