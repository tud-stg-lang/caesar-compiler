package generated.test36;

import junit.framework.*;
import java.util.*;

import generated.test36.pckgB.*;

/**
 * Test inheritance cross package boundaries
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResultE = "B.C:C.C:D.C:C.E";
	public static final String expectedResultF = "B.C:C.C:D.C:B.D:C.F";

	public void test()
	{
		System.out.println("-------> VCTest 36: Inheritance cross package boundaries: start");

		final OuterD d = new OuterD();
        d.InnerE e = d.new InnerE();
        d.InnerF f = d.new InnerF();

        String resultE = e.getA();
        String resultF = f.getA();

		System.out.println(resultE);
		System.out.println(resultF);

		assertEquals(expectedResultE, resultE);
		assertEquals(expectedResultF, resultF);

        System.out.println("-------> VCTest 36: end");
	}
}