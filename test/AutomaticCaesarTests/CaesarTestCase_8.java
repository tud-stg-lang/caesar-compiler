package generated;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * TODO privleged access
 */
privileged public cclass CaesarTestCase_8 extends TestCase {

	public CaesarTestCase_8() {
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":private:public:foo";

	public void test() {
		
		PrivateAccessClass_8 privObj = new PrivateAccessClass_8();
		
		result.append(privObj.secret);
		
		privObj.secret = ":public";
		
		result.append(privObj.secret);
		
		privObj.foo();
	

		assertEquals(expectedResult, result.toString());
	}


}

cclass PrivateAccessClass_8 {
	
	private String secret = ":private";
	
	private void foo() {
		CaesarTestCase_8.result.append(":foo");
	}
}

