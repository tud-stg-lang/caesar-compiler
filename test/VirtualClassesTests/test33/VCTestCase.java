package generated.test33;

import junit.framework.*;
import java.util.*;

/**
 * Test inheritance from implicit type.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A, B.B.B, C.A.C";


	public void test()
	{
		System.out.println("-------> VCTest 33: Inheritance from Implicit Type: start");
        
        OuterC.InnerB cb = (OuterC.InnerB)new OuterC().new InnerB();
	
		String result = cb.queryA() + ", " + cb.queryB() + ", " + cb.queryC();

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 33: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}
}

public cclass OuterB extends OuterA
{
    public cclass InnerB extends InnerA
	{
	    public String queryB()
		{
			return "B.B.B";
		}
	}
}

public cclass OuterC extends OuterB  
{  
    public cclass InnerA
	{
	    public String queryC()
		{
			return "C.A.C";
		}
	}
}
