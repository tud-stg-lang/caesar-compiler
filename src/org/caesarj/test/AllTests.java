package org.caesarj.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * provides all Caesar tests.
 * 
 * @author Andreas Wittmann, Sven Kloppenburg
 *
 * @see CompileAndRunResultsTest
 * @see CompileAndRunResultsCITest
 */
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("all Caesar tests");
		//$JUnit-BEGIN$
        suite.addTestSuite( VirtualClassesTests.class );
        //suite.addTestSuite( CompileAndRunResultsWeaverTest.class );
        suite.addTestSuite( AspectDeploymentTests.class);
        suite.addTestSuite( CompilerErrorsTests.class);
        suite.addTestSuite( StructureModelTests.class);
        //$JUnit-END$
		return suite;
	}
}
