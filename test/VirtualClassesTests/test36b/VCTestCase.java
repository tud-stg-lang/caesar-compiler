package generated.test36b;

import junit.framework.*;

import generated.test36b.pckgA.*;

/**
 * Instantiation cross package boundaries
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "BCA";

	public void test()
	{
		System.out.println("-------> VCTest 36b: Instantiation cross package boundaries: start");

		OuterD d = new OuterD();

		assertEquals(expectedResult, d.new InnerA().getA());

        System.out.println("-------> VCTest 36b: end");
	}
}