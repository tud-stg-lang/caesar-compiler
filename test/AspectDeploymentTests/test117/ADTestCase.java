package generated.test117;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test conditional pointcuts
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
    public ADTestCase()
    {
        super("test");
    }

    public static StringBuffer result = new StringBuffer();

	public String expectedResult =
    	":before cutPlus:A.A(1):before cutMinus:A.A(-1)";

    public void test()
    {
		System.out.println("-------> ADTest 17: Conditional pointcuts: start");

        new DeployA_Impl(null).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 17: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterA a1 = new OuterA_Impl(null).init(1);
		OuterA a2 = new OuterA_Impl(null).init(-1);

		deploy(new AspectA_Impl(null))
        {
        	a1.doA();
        	a2.doA();
        }
    }
}

cclass AspectA
{
	pointcut cutPlus() : call(* OuterA+.doA(..)) && target(a) && if(a.getId() > 0);

	before() : cutPlus()
	{
		ADTestCase.result.append(":before cutPlus");
	}

	pointcut cutMinus() : call(* OuterA+.doA(..)) && target(a) && if(a.getId() < 0);

	before() : cutPlus()
	{
		ADTestCase.result.append(":before cutMinus");
	}
}

cclass OuterA
{
	private int _id;

	public void init(int id)
	{
		_id = id;
	}

	public void doA()
	{
		ADTestCase.result.append(":A.A(" + _id + ")");
	}

	public int getId()
	{
		return _id;
	}
}


