package generated.test34;

import junit.framework.*;
import java.util.*;

/**
 * Test mixin factory methods.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "B.B.B, C.C.C";


	public void test()
	{
		System.out.println("-------> VCTest 34: Mixin factory methods: start");

        OuterB.InnerB db = new OuterD().newB();
        OuterC.InnerC dc = new OuterD().newC();

		String result = db.queryB() + ", " + dc.queryC();

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 34: end");
	}
}

public cclass OuterA
{
}

public cclass OuterB extends OuterA
{
    public cclass InnerB
	{
	    public String queryB()
		{
			return "B.B.B";
		}
	}

	public OuterB.InnerB newB()
	{
		return new InnerB();
	}
}

public cclass OuterC extends OuterA
{
    public cclass InnerC
	{
	    public String queryC()
		{
			return "C.C.C";
		}
	}

	public OuterC.InnerC newC()
	{
		return new InnerC();
	}
}

public cclass OuterD extends OuterB & OuterC
{
}
