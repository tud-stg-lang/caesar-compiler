package generated.test31;

import junit.framework.*;
import java.util.*;

/**
 * Test outer class creation.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult =	"2";


	public void test()
	{
		System.out.println("-------> VCTest 31: Test Outer Class Creation: start");

		String result = new OuterB_Impl(null).test();
		System.out.println(result);
		assertEquals(expectedResult, result);


		System.out.println("-------> VCTest 31: end");
	}
}

public cclass OuterB
{
	public String test()
	{
		OuterA oa = new OuterA_Impl(null).init(2);
		return "" + oa.getVal();
	}
}

public cclass OuterA
{
	int _val;

	public OuterA init(int val)
	{
		_val = val;
		return this;
	}

	public int getVal()
	{
		return _val;
	}
}


