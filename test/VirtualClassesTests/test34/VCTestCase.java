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

		final OuterD od1 = new OuterD();
		final OuterD od2 = new OuterD();
		final OuterB ob = od1;
		final OuterC oc = od2;
		
        ob.InnerB db = (ob.InnerB)od1.newB();
        oc.InnerC dc = (oc.InnerC)od2.newC();

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
