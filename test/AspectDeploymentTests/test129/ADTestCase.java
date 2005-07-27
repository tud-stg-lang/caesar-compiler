package generated.test129;

import junit.framework.TestCase;

/**
 * Tests nested croscutting classes
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult = "PR";

	public void test()
	{
		System.out.println("-------> ADTest 29");

		StockPricing.PerRequestDiscount pricing = new StockPricing().new PerRequestDiscount();
		Test test = new Test();

		deploy pricing;

		test.foo();

		undeploy pricing;

		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 29: end");
	}
}

public class Test {
    public void foo() {}
}

public cclass Pricing
{
	public cclass CI {
	}

	public cclass Discount extends CI {
	}
}


public cclass StockPricing extends Pricing {

	public cclass CI {
	}

	public cclass Discount {
	}

	public cclass Common extends CI {
	}

	public cclass PerRequest extends Common {
		after() : execution(public void Test.foo()) {
			System.out.println("----> PR");
		   	ADTestCase.result.append("PR");
       	}
	}

	public cclass PerRequestDiscount extends Discount & PerRequest {
	}
}
