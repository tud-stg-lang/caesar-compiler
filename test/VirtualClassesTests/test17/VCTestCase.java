package generated.test17;

import junit.framework.*;
import java.util.*;

/**
 * Test extending deep classes relationships.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A.A.A, A.A.B.A, A.A.C.A, A.B.A.A, B.B.B.A, A.A.C.A, B.B.D.A"
											  + ", A.B.A.A, B.B.B.A, A.A.C.A";

	public void test() {

		System.out.println("-------> VCTest 17: Extending Deep Classes: start");

		OuterB.InnerB b = (OuterB.InnerB)(new OuterB_Impl(null)).$newInnerB(); // !!! remove parameter

		String result = b.defaultObject().queryA();

		System.out.println(result);
		//assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 17: end");
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
				return "A.A.A";
			}
		}

		public cclass DeepestB extends DeepestA
		{
			DeepestA _child = null;

			public void setChild(OuterA.InnerA.DeepestA child)
			{
				_child = child;
			}

			public void setDefaultChild()
			{
				setChild($outer.$newDeepestC());
			}

			public String queryA()
			{
				return super.queryA() + ", A.A.B, child: " + _child.queryA();
			}
		}

		public cclass DeepestC extends DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.A.C";
			}
		}

		public OuterA.InnerA.DeepestB defaultObject()
		{
			DeepestB b = $newDeepestB();
			b.setDefaultChild();
			return b;
		}
	}

	public cclass InnerB extends InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.A";
			}
		}

		public cclass DeepestB
		{
			DeepestA _child2 = null;

			public void setChild2(OuterA.InnerA.DeepestA child)
			{
				_child2 = child;
			}

			public void setDefaultChild2()
			{
				setChild2($outer.$newDeepestD());
			}

			public String queryA()
			{
				return super.queryA() + ", A.B.A, child2: " + _child2.queryA();
			}
		}

		public cclass DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.C";
			}
		}

		public cclass DeepestD extends DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", A.B.D";
			}
		}

		public OuterA.InnerA.DeepestB defaultObject()
		{
			DeepestB b = super.defaultObject();
			b.setDefaultChild2();
			return b;
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA extends InnerA
	{
		public cclass DeepestA
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.A";
			}
		}

		public cclass DeepestB
		{
			DeepestA _child3 = null;

			public void setChild3(OuterA.InnerA.DeepestA child)
			{
				_child3 = child;
			}

			public void setDefaultChild3()
			{
				setChild3($outer.$newDeepestE());
			}

			public String queryA()
			{
				return super.queryA() + ", B.A.B, child3: " + _child3.queryA();
			}
		}

		public cclass DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.C";
			}
		}

		public cclass DeepestE extends DeepestC
		{
			public String queryA()
			{
				return super.queryA() + ", B.A.E";
			}
		}

		public OuterA.InnerA.DeepestB defaultObject()
		{
			DeepestB b = super.defaultObject();
			b.setDefaultChild3();
			return b;
		}
	}
}