package org.caesarj.test;

public class TypeSystemTests extends FjTestCase {

	public TypeSystemTests(String name) {
		super(name);
	}

	/*
	 * Test Dependent Types defined within plain Java classes
	 */
	public void testCaesarTestCase_01() throws Throwable {
	    compileDontRun("typesystest01");
    }

	public void testCaesarTestCase_02() throws Throwable {
	    compileDontRun("typesystest02");
    }

	public void testCaesarTestCase_03() throws Throwable {
	    compileDontRun("typesystest03");
    }

	public void testCaesarTestCase_04() throws Throwable {
	    compileDontRun("typesystest04");
    }

	public void testCaesarTestCase_05() throws Throwable {	    
	    compileDontRun("typesystest05");
    }

	public void testCaesarTestCase_06() throws Throwable {
	    compileDontRun("typesystest06");
    }

	public void testCaesarTestCase_07() throws Throwable {
	    compileDontRun("typesystest07");
    }

	public void testCaesarTestCase_08() throws Throwable {
	    compileDontRun("typesystest08");
    }

	public void testCaesarTestCase_09() throws Throwable {
	    compileDontRun("typesystest09");
    }

	/*
	 * Test Dependent Types defined within cclass
	 */
	public void testCaesarTestCase_50() throws Throwable {
	    compileDontRun("typesystest50");
    }

	public void testCaesarTestCase_51() throws Throwable {
	    compileDontRun("typesystest51");
    }


	/*
	 * TEST ERRORS
	 */
	/*
	public void testCaesarTestCase_100() throws Throwable {
	    compileAndCheckErrors("typesystest100", new String[]{""});
    }
    */

}