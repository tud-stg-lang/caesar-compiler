package generated.test33;

import junit.framework.*;
import java.util.*;

/**
 * Test virtual class scoping
 * 
 * Here we test the following:
 *     - resolution of types which has not been explicitly redefined in the same collaboration
 * 	   - lookup of ambiguous types inherited from different interfaces
 *
 * @author Ivica Aracic
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
        
		final OuterE oe = new OuterE();
        oe.InnerB cb = oe.new InnerB();
	
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

public cclass OuterD extends OuterB {}

public cclass OuterE extends OuterC & OuterD {

	public cclass InnerX extends InnerA {}
	public cclass InnerY extends InnerB {}

	private InnerA a;
	private InnerB b;
	private String s;

    public cclass X {
        private InnerA a;
        private InnerB b;
        private String s;
    }
}