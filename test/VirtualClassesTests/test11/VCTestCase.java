package generated.test11;

import junit.framework.*;
import java.util.*;

/**
 * Inner classes of inner classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public void test() {

		System.out.println("-------> VCTest 11: Inner of Inner Classes: start");

		// just check if compiles

        System.out.println("-------> VCTest 11: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return "A.A.A.A";
			}
		}

		public cclass DeepestB
		{
			public String queryA()
			{
				return "A.A.B.A";
			}
		}

		public cclass DeepestC
		{
			public String queryA()
			{
				return "A.A.C.A";
			}
		}
	}

	public cclass InnerB extends InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return "A.B.A.A";
			}
		}

		public cclass DeepestD
		{
			public String queryA()
			{
				return "A.B.D.A";
			}
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerB
	{
		public cclass DeepestB
		{
			public String queryA()
			{
				return "B.B.B.A";
			}
		}

		public cclass DeepestD
		{
			public String queryA()
			{
				return "B.B.D.A";
			}
		}
	}
}