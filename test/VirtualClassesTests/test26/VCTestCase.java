package generated.test26;

import junit.framework.*;
import java.util.*;

/**
 * Test extends for furtherbindings.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A, A.B, B.B, A.C, B.C";


	public void test() {

		System.out.println("-------> VCTest 26: Test extends for furtherbindings: start");

		OuterB ob = new OuterB();
		final OuterA oa = ob;
		
		oa.InnerB ic = (oa.InnerB)ob.new InnerC();

		String result = ic.queryA();

		System.out.println(result);
		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 26: end");
	}
}

public cclass OuterA
{

	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A";
		}
	}

	public cclass InnerB extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.B";
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", B.B";
		}
	}

	public cclass InnerC extends InnerB
	{
		public String queryA()
		{
			return super.queryA() + ", B.C";
		}
	}
}