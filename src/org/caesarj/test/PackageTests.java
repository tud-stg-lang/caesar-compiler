package org.caesarj.test;

public class PackageTests extends FjTestCase 
{
	public PackageTests(String name) {
		super(name);
	}
	
	/* Test access of outer fields */
	public void testJavaTestCase_0() throws Throwable {
		compileAndRun("pckgtest0", "PackageTestCase");
	}
	
	/* Test externalized class imports */
	public void testJavaTestCase_1() throws Throwable {
		compileAndRun("pckgtest1", "PackageTestCase");
	}
	
	/* Test deep nested externalized classes */
	public void testJavaTestCase_2() throws Throwable {
		compileAndRun("pckgtest2", "PackageTestCase");
	}
	
	/* Test Java class in cclass */
	public void testJavaTestCase_3() throws Throwable {
		compileAndCheckErrors("pckgtest3", new String[] { "" });;
	}
	
	/* Test cclass in Java class */
	public void testJavaTestCase_4() throws Throwable {
		compileAndCheckErrors("pckgtest4", new String[] { "" });;
	}
}