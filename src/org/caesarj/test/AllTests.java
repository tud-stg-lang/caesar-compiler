package org.caesarj.test;


import org.caesarj.test.CompileAndRunResultsTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("all familyj tests");
		//$JUnit-BEGIN$
		suite.addTestSuite( CompileAndRunResultsTest.class );
		//$JUnit-END$
		return suite;
	}
}
