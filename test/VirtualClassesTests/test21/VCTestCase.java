package generated.test21;

import junit.framework.*;
import java.util.*;

/**
 * Test joining extensions of state.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "A.A, C.A, B.A, " +
	                                            "A.B, child: A.A, C.A, B.A, A.C, C.C, B.C, " +
	                                            "C.B, child3: A.A, C.A, B.A, A.C, C.C, B.C, C.E, " +
	                                            "B.B, child2: A.A, C.A, B.A, A.C, C.C, B.C, B.D";

	public void test() {

		System.out.println("-------> VCTest 21: Test joining extensions: start");

		String result = new OuterD().defaultObject().queryA();

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 21: end");
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
	    protected InnerA _child = null;

		public void setChild(OuterA.InnerA child)
		{
			_child = child;
		}

		public void setDefaultChild()
		{
			setChild(OuterA.this.new InnerC());
		}

		public String queryA()
		{
			return super.queryA() + ", A.B, child: " + _child.queryA();
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C";
		}
	}

	public OuterA.InnerB defaultObject()
	{
	    //InnerB b = $newInnerB();
		InnerB b = new InnerB();
		b.setDefaultChild();
		return b;
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.A";
		}
	}

	public cclass InnerB
	{
	    protected OuterB.InnerA _child2 = null;

		public void setChild2(OuterB.InnerA child)
		{
			_child2 = child;
		}

		public void setDefaultChild2()
		{
			setChild2(OuterB.this.new InnerD());
		}

		public String queryA()
		{
			return super.queryA() + ", B.B, child2: " + _child2.queryA();
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", B.C";
		}
	}

	public cclass InnerD extends InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", B.D";
		}
	}

	public OuterA.InnerB defaultObject()
	{
		OuterB.InnerB b = super.defaultObject();
		b.setDefaultChild2();
		return b;
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", C.A";
		}
	}

	public cclass InnerB
	{
	    protected OuterC.InnerA _child3 = null;

		public void setChild3(OuterC.InnerA child)
		{
			_child3 = child;
		}

		public void setDefaultChild3()
		{
			setChild3(OuterC.this.new InnerE());
		}

		public String queryA()
		{
			return super.queryA() + ", C.B, child3: " + _child3.queryA();
		}
	}

	public cclass InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", C.C";
		}
	}

	public cclass InnerE extends InnerC
	{
		public String queryA()
		{
			return super.queryA() + ", C.E";
		}
	}

	public OuterA.InnerB defaultObject()
	{
		OuterC.InnerB b = super.defaultObject();
		b.setDefaultChild3();
		return b;
	}
}

public cclass OuterD extends OuterB & OuterC
{

}