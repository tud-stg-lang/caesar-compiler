package org.caesarj.test;

import java.io.File;

import junit.framework.TestCase;

public class FjTestCase extends TestCase {

	public FjTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

/**
 * @returns a String: "./test/$CLASS", meaning the subdirectory of "./test" with the name 
 * of the executing test-file.
 */
	protected String getWorkingDirectory() {
		String res = "." + File.separator + "test" + File.separator 
			+ getClass().getName().substring(
			getClass().getName().lastIndexOf( "." ) + 1 );
            
        return res;
	}

	protected void warn( String message ) {
		System.out.println( "warning - " + getClass().getName() + ": " + message );
	}
	
	protected void doGeneratedTest( String testCaseName ) throws Throwable {
		System.out.println("Test "+testCaseName+" starts");
		Object generatedTest = Class.forName( "generated." + testCaseName ).newInstance();
		((TestCase) generatedTest).runBare();
	}

	public void removeClassFiles() {
		File workingDir = new File( getWorkingDirectory() + File.separator 
			+ "generated" );
		File[] files = workingDir.listFiles();
		int deleteCount = 0;
		try {
			for( int i = 0; i < files.length; i++ ) {
				if( files[ i ].getName().endsWith( ".class" ) ) {
					if( files[ i ].delete() )
						deleteCount++;
				}
			}
		} catch( Throwable t ) {
		} finally {
			if( deleteCount == 0 )
				warn( "no class files were deleted" );
		}
	}


}
