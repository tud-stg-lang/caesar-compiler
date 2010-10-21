package org.caesarj.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.caesarj.compiler.Log;
import org.caesarj.compiler.Main;

public class LargeTests extends TestCase {

	public void testWeaving() throws Throwable {
		Log.setVerbose(true);
		FileOutputStream fos = new FileOutputStream(
				"large-tests/weavingtest.log");

		Main main = new Main(null, new PrintWriter(fos));

		Log.setErrorOutput(new PrintWriter(System.err));

		String args[] = {
				"-d",
				"large-tests/weavingtest/bin",
				"-v",
				"-classpath",
				"dist/caesar-runtime.jar" + File.pathSeparator
				+ "large-tests/weavingtest/lib/base.jar" + File.pathSeparator
				+ "large-tests/weavingtest/lib/base2", 
				"-inpath",
				"large-tests/weavingtest/lib/base.jar" + File.pathSeparator
				+ "large-tests/weavingtest/lib/base2",
				"@large-tests/weavingtest/weavingtest-filelist.txt" };

		boolean res = main.run(args);

		fos.close();

		assertTrue(res);
	}

	public void testTwomoreBottomUp() throws Throwable {
		Log.setVerbose(true);
		FileOutputStream fos = new FileOutputStream(
				"large-tests/twomore-bottomup.log");

		Main main = new Main(null, new PrintWriter(fos));

		Log.setErrorOutput(new PrintWriter(System.err));

		String args[] = {
				"-d",
				"large-tests/twomore-bottomup-bin",
				"-v",
				"-classpath",
				"dist/caesar-runtime.jar;../twomore-bottomup/lib/log4j-1.2.8.jar;../twomore-bottomup/lib/lucene-1.3-final.jar;../twomore-bottomup/lib/osxadapter.jar;../twomore-bottomup/tm4j/ant.jar;../twomore-bottomup/tm4j/antlr.jar;../twomore-bottomup/tm4j/commons-collections.jar;../twomore-bottomup/tm4j/commons-digester.jar;../twomore-bottomup/tm4j/commons-logging.jar;../twomore-bottomup/tm4j/jakarta-regexp.jar;../twomore-bottomup/tm4j/jargs.jar;../twomore-bottomup/tm4j/junit.jar;../twomore-bottomup/tm4j/mango.jar;../twomore-bottomup/tm4j/optional.jar;../twomore-bottomup/tm4j/resolver.jar;../twomore-bottomup/tm4j/tm4j-0.9.6.jar;../twomore-bottomup/tm4j/tm4jadmintool-0.9.6.jar;../twomore-bottomup/tm4j/tm4jdbc-0.9.6.jar;../twomore-bottomup/tm4j/tm4j-tmapi-0.9.6.jar;../twomore-bottomup/tm4j/tm4j-tologx-0.9.6.jar;../twomore-bottomup/tm4j/tm4ozone-0.9.6.jar;../twomore-bottomup/tm4j/tmapi-1_0rc1.jar;../twomore-bottomup/tm4j/xercesImpl.jar;../twomore-bottomup/tm4j/xmlParserAPIs.jar;../twomore-bottomup/tm4j/hibernate/c3p0.jar;../twomore-bottomup/tm4j/hibernate/cglib2.jar;../twomore-bottomup/tm4j/hibernate/commons-beanutils.jar;../twomore-bottomup/tm4j/hibernate/commons-dbcp.jar;../twomore-bottomup/tm4j/hibernate/commons-lang.jar;../twomore-bottomup/tm4j/hibernate/commons-pool.jar;../twomore-bottomup/tm4j/hibernate/connector.jar;../twomore-bottomup/tm4j/hibernate/dom4j.jar;../twomore-bottomup/tm4j/hibernate/ehcache.jar;../twomore-bottomup/tm4j/hibernate/hibernate2.jar;../twomore-bottomup/tm4j/hibernate/jaas.jar;../twomore-bottomup/tm4j/hibernate/jcs.jar;../twomore-bottomup/tm4j/hibernate/jdbc2_0-stdext.jar;../twomore-bottomup/tm4j/hibernate/jta.jar;../twomore-bottomup/tm4j/hibernate/odmg.jar;../twomore-bottomup/tm4j/hibernate/proxool.jar",
				"@large-tests/twomore-bottomup-filelist.txt" };

		boolean res = main.run(args);

		fos.close();

		assertTrue(res);
	}

	public void testTwomoreFull() throws Throwable {
		Log.setVerbose(true);
		FileOutputStream fos = new FileOutputStream("large-tests/twomore.log");

		Main main = new Main(null, new PrintWriter(fos));

		Log.setErrorOutput(new PrintWriter(System.err));

		String args[] = {
				"-d",
				"large-tests/twomore-bin",
				"-v",
				"-classpath",
				"dist/caesar-runtime.jar;../twomore-cj/lib/log4j-1.2.8.jar;../twomore-cj/lib/kunststoff.jar;../twomore-cj/lib/skinlf.jar;../twomore-cj/lib/docking-0.3.jar;../twomore-cj/lib/lucene-1.3-final.jar;../twomore-cj/lib/jogl.jar;../twomore-cj/lib/osxadapter.jar;../twomore-cj/lib/tyRuBa.jar;../twomore-cj/lib/jh.jar;../twomore-cj/lib/toniclf_slim.jar;../twomore-cj/lib/looks-1.2.2.jar;../twomore-cj/lib/xml-actions.jar;../twomore-cj/tm4j/ant.jar;../twomore-cj/tm4j/antlr.jar;../twomore-cj/tm4j/commons-collections.jar;../twomore-cj/tm4j/commons-digester.jar;../twomore-cj/tm4j/commons-logging.jar;../twomore-cj/tm4j/jakarta-regexp.jar;../twomore-cj/tm4j/jargs.jar;../twomore-cj/tm4j/junit.jar;../twomore-cj/tm4j/mango.jar;../twomore-cj/tm4j/optional.jar;../twomore-cj/tm4j/resolver.jar;../twomore-cj/tm4j/tm4j-0.9.6.jar;../twomore-cj/tm4j/tm4j-tmapi-0.9.6.jar;../twomore-cj/tm4j/tm4j-tologx-0.9.6.jar;../twomore-cj/tm4j/tm4jadmintool-0.9.6.jar;../twomore-cj/tm4j/tm4jdbc-0.9.6.jar;../twomore-cj/tm4j/tm4ozone-0.9.6.jar;../twomore-cj/tm4j/tmapi-1_0rc1.jar;../twomore-cj/tm4j/xercesImpl.jar;../twomore-cj/tm4j/xmlParserAPIs.jar;../twomore-cj/tm4j/hibernate/c3p0.jar;../twomore-cj/tm4j/hibernate/cglib2.jar;../twomore-cj/tm4j/hibernate/commons-beanutils.jar;../twomore-cj/tm4j/hibernate/commons-dbcp.jar;../twomore-cj/tm4j/hibernate/commons-lang.jar;../twomore-cj/tm4j/hibernate/commons-pool.jar;../twomore-cj/tm4j/hibernate/connector.jar;../twomore-cj/tm4j/hibernate/dom4j.jar;../twomore-cj/tm4j/hibernate/ehcache.jar;../twomore-cj/tm4j/hibernate/hibernate2.jar;../twomore-cj/tm4j/hibernate/jaas.jar;../twomore-cj/tm4j/hibernate/jcs.jar;../twomore-cj/tm4j/hibernate/jdbc2_0-stdext.jar;../twomore-cj/tm4j/hibernate/jta.jar;../twomore-cj/tm4j/hibernate/odmg.jar;../twomore-cj/tm4j/hibernate/proxool.jar",
				"@large-tests/twomore-filelist.txt" };

		boolean res = main.run(args);

		fos.close();

		assertTrue(res);
	}

	public void testDungeon() throws Throwable {
		Log.setVerbose(true);
		FileOutputStream fos = new FileOutputStream("large-tests/dungeon.log");

		Main main = new Main(null, new PrintWriter(fos));

		Log.setErrorOutput(new PrintWriter(System.err));

		String args[] = {
				"-d",
				"large-tests/dungeon-bin",
				"-v",
				"-classpath",
				"dist/caesar-runtime.jar;../CesarJ_Dungeon/lib/j3dutils.jar;../CesarJ_Dungeon/lib/log4j-1.2/log4j-1.2.11.jar",
				"@large-tests/dungeon-filelist.txt" };

		boolean res = main.run(args);

		fos.close();

		assertTrue(res);
	}
}
