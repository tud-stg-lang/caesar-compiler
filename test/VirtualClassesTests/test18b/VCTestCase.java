package generated.test18b;

import junit.framework.*;
import java.util.*;

/**
 * Test multiple inheritance of methods.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "";

	public void test() {

		System.out.println("-------> VCTest 18b: Multiple Inheritance: start");

		Z z = new Z();

		//assertEquals(expectedResult, );

        System.out.println("-------> VCTest 18b: end");
	}
}

public cclass X {
	public cclass A {}
}

public cclass Y {
	public cclass A {}
}

public cclass Z extends X & Y {} 


