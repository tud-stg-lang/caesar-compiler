package generated.test116;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test if mixins are weaved
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
    	":A.B:before cutAA:A.A:C.B:before cutAA:A.A:B.B:before cutAA:A.A:D.B:before cutAA:A.A";

    public void test()
    {
		System.out.println("-------> ADTest 16: Weaving mixins: start");

        new DeployA().test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 16: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterD od = new OuterD();

		deploy(new AspectA())
        {
        	od.doB();
        }
    }
}

cclass AspectA
{
	pointcut cutAA() : call(* OuterA+.doA(..));

	before() : cutAA()
	{
		ADTestCase.result.append(":before cutAA");
	}
}

cclass OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":A.A");
	}

	public void doB()
	{
		ADTestCase.result.append(":A.B");
		doA();
	}
}

cclass OuterB extends OuterA
{
	public void doB()
	{
		super.doB();
		ADTestCase.result.append(":B.B");
		doA();
	}
}


cclass OuterC extends OuterA
{
	public void doB()
	{
		super.doB();
		ADTestCase.result.append(":C.B");
		doA();
	}
}


cclass OuterD extends OuterB & OuterC
{
	public void doB()
	{
		super.doB();
		ADTestCase.result.append(":D.B");
		doA();
	}
}

