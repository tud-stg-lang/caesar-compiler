package generated;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * TODO privleged access
 */
public class CaesarTestCase_8 extends TestCase
{
	public CaesarTestCase_8()
	{
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":private:public:foo";

	public void test()
	{
		new TestCase_8_Impl(null).test();

        assertEquals(expectedResult, result.toString());

		assertEquals(expectedResult, result.toString());
	}
}

privileged public cclass TestCase_8
{
	public void test()
	{
		PrivateAccessClass_8 privObj = new PrivateAccessClass_8_Impl(null);

		CaesarTestCase_8.result.append(privObj.secret);

		privObj.secret = ":public";

		CaesarTestCase_8.result.append(privObj.secret);

		privObj.foo();
	}
}


cclass PrivateAccessClass_8
{
	private String secret = ":private";

	private void foo()
	{
		CaesarTestCase_8.result.append(":foo");
	}
}

