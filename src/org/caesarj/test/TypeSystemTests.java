package org.caesarj.test;

public class TypeSystemTests extends FjTestCase {

	public TypeSystemTests(String name) {
		super(name);
	}

	public void testCaesarTestCase_01() throws Throwable {
        compileAndRun("typesystest01", "TypeSysTestCase");
    }

}