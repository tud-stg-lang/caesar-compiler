package generated.test35;

import junit.framework.*;
import java.util.*;

/**
 * Test Object methods through Caesar interfaces.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public void test()
	{
		System.out.println("-------> VCTest 35: Object methods: start");

        IfcB b = new ClassB();
        OuterA a = new OuterA_Impl(null);
        OuterA.InnerA aa = a.$newInnerA();

		/* test normal interface */
        String result1 = "[" + b.toString() + "][" + b.equals(aa) + "][" + b.hashCode() + "]";
        System.out.println(result1);

		/* test interface of outer class */
		String result2 = "[" + a.toString() + "][" + a.equals(aa) + "][" + a.hashCode() + "]";
		System.out.println(result2);

		/* test interface of inner class */
        String result3 = "[" + aa.toString() + "][" + aa.equals(aa) + "][" + aa.hashCode() + "]";
		System.out.println(result3);

        System.out.println("-------> VCTest 35: end");
	}
}

interface IfcB
{
	public String getA();
}

class ClassB implements IfcB
{
	public String getA()
	{
		return "A";
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
