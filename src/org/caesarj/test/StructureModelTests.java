package org.caesarj.test;

public class StructureModelTests extends FjTestCase {

	public StructureModelTests(String name) {
		super(name);
	}

	public void testCaesarTestCase_01() throws Throwable {
        compileAndRun("smtest01", "SMTestCase");
    }

}